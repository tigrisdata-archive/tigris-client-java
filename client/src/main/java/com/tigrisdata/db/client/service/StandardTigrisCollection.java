package com.tigrisdata.db.client.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;
import com.tigrisdata.db.api.v1.grpc.Api;
import com.tigrisdata.db.api.v1.grpc.TigrisDBGrpc;
import com.tigrisdata.db.client.error.TigrisDBException;
import com.tigrisdata.db.client.model.DeleteRequestOptions;
import com.tigrisdata.db.client.model.DeleteResponse;
import com.tigrisdata.db.client.model.Field;
import com.tigrisdata.db.client.model.InsertRequestOptions;
import com.tigrisdata.db.client.model.InsertResponse;
import com.tigrisdata.db.client.model.Operators;
import com.tigrisdata.db.client.model.ReadRequestOptions;
import com.tigrisdata.db.client.model.TigrisCollectionType;
import com.tigrisdata.db.client.model.TigrisDBResponse;
import com.tigrisdata.db.client.model.TigrisFilter;
import com.tigrisdata.db.client.model.UpdateRequestOptions;
import com.tigrisdata.db.client.model.UpdateResponse;
import com.tigrisdata.db.client.model.WriteOptions;
import com.tigrisdata.db.client.utils.Utilities;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class StandardTigrisCollection<T extends TigrisCollectionType>
    implements TigrisCollection<T> {

  private final String databaseName;
  private final String collectionName;
  private final Class<T> collectionTypeClass;
  private final TigrisDBGrpc.TigrisDBBlockingStub stub;
  private final ObjectMapper objectMapper;

  public StandardTigrisCollection(
      String databaseName, Class<T> collectionTypeClass, TigrisDBGrpc.TigrisDBBlockingStub stub) {
    this.databaseName = databaseName;
    this.collectionName = collectionTypeClass.getSimpleName();
    this.collectionTypeClass = collectionTypeClass;
    this.stub = stub;
    this.objectMapper = new ObjectMapper();
  }

  @Override
  public Iterator<T> read(
      TigrisFilter filter, List<Field<?>> fields, ReadRequestOptions readRequestOptions)
      throws TigrisDBException {
    try {
      Api.ReadRequest readRequest =
          Api.ReadRequest.newBuilder()
              .setDb(databaseName)
              .setCollection(collectionName)
              .setFilter(ByteString.copyFrom(filter.toString(), StandardCharsets.UTF_8))
              .setFields(ByteString.copyFromUtf8(Utilities.fields(fields)))
              .setOptions(Api.ReadRequestOptions.newBuilder().build())
              .build();
      Iterator<Api.ReadResponse> readResponseIterator = stub.read(readRequest);

      Function<Api.ReadResponse, T> converter =
          readResponse -> {
            try {
              return objectMapper.readValue(
                  readResponse.getDoc().toStringUtf8(), collectionTypeClass);
            } catch (JsonProcessingException e) {
              throw new IllegalArgumentException(
                  "Failed to convert response to" + " the user " + "type", e);
            }
          };
      return Utilities.from(readResponseIterator, converter);
    } catch (JsonProcessingException jsonProcessingException) {
      throw new TigrisDBException("Failed to process fields", jsonProcessingException);
    }
  }

  @Override
  public Iterator<T> read(TigrisFilter filter, List<Field<?>> fields) throws TigrisDBException {
    return this.read(filter, fields, new ReadRequestOptions());
  }

  @Override
  public InsertResponse insert(List<T> documents, InsertRequestOptions insertRequestOptions)
      throws TigrisDBException {
    Api.InsertRequest.Builder insertRequestBuilder =
        Api.InsertRequest.newBuilder()
            .setDb(databaseName)
            .setCollection(collectionName)
            .setOptions(
                Api.InsertRequestOptions.newBuilder()
                    .setMustNotExist(insertRequestOptions.isMustNotExist())
                    .setWriteOptions(Api.WriteOptions.newBuilder().build())
                    .build());
    for (T document : documents) {
      insertRequestBuilder.addDocuments(ByteString.copyFromUtf8(document.toString()));
    }
    stub.insert(insertRequestBuilder.build());
    // TODO actual status back
    return new InsertResponse(new TigrisDBResponse("inserted"));
  }

  @Override
  public InsertResponse insert(List<T> documents) throws TigrisDBException {
    return this.insert(documents, new InsertRequestOptions(new WriteOptions()));
  }

  @Override
  public UpdateResponse update(
      TigrisFilter filter, List<Field<?>> fields, UpdateRequestOptions updateRequestOptions)
      throws TigrisDBException {
    try {
      Api.UpdateRequest updateRequest =
          Api.UpdateRequest.newBuilder()
              .setDb(databaseName)
              .setCollection(collectionName)
              .setFilter(ByteString.copyFromUtf8(filter.toString()))
              .setFields(ByteString.copyFromUtf8(Utilities.fieldsOperation(Operators.SET, fields)))
              .build();
      Api.UpdateResponse updateResponse = stub.update(updateRequest);
      return new UpdateResponse(updateResponse.getRc());
    } catch (JsonProcessingException jsonProcessingException) {
      throw new TigrisDBException("Failed to process fields", jsonProcessingException);
    }
  }

  @Override
  public UpdateResponse update(TigrisFilter filter, List<Field<?>> fields)
      throws TigrisDBException {
    return update(filter, fields, new UpdateRequestOptions());
  }

  @Override
  public DeleteResponse delete(TigrisFilter filter, DeleteRequestOptions deleteRequestOptions)
      throws TigrisDBException {
    Api.DeleteRequest deleteRequest =
        Api.DeleteRequest.newBuilder()
            .setDb(databaseName)
            .setCollection(collectionName)
            .setOptions(
                Api.DeleteRequestOptions.newBuilder()
                    .setWriteOptions(Api.WriteOptions.newBuilder().build())
                    .build())
            .build();
    stub.delete(deleteRequest);
    // TODO actual status back
    return new DeleteResponse(new TigrisDBResponse("deleted"));
  }

  @Override
  public DeleteResponse delete(TigrisFilter filter) throws TigrisDBException {
    return this.delete(filter, new DeleteRequestOptions(new WriteOptions()));
  }

  @Override
  public String name() {
    return collectionName;
  }
}
