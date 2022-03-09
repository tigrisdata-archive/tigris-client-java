package com.tigrisdata.db.client.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;
import com.tigrisdata.db.api.v1.grpc.TigrisDBGrpc;
import com.tigrisdata.db.api.v1.grpc.User;
import com.tigrisdata.db.client.error.TigrisDBException;
import com.tigrisdata.db.client.model.DeleteRequestOptions;
import com.tigrisdata.db.client.model.DeleteResponse;
import com.tigrisdata.db.client.model.InsertRequestOptions;
import com.tigrisdata.db.client.model.InsertResponse;
import com.tigrisdata.db.client.model.ReadRequestOptions;
import com.tigrisdata.db.client.model.ReplaceRequestOptions;
import com.tigrisdata.db.client.model.ReplaceResponse;
import com.tigrisdata.db.client.model.TigrisCollectionType;
import com.tigrisdata.db.client.model.TigrisDBResponse;
import com.tigrisdata.db.client.model.TigrisFilter;
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
  public Iterator<T> read(TigrisFilter filter, ReadRequestOptions readRequestOptions)
      throws TigrisDBException {
    User.ReadRequest readRequest =
        User.ReadRequest.newBuilder()
            .setDb(databaseName)
            .setCollection(collectionName)
            .setFilter(ByteString.copyFrom(filter.toString(), StandardCharsets.UTF_8))
            .setOptions(User.ReadRequestOptions.newBuilder().build())
            .build();
    Iterator<User.ReadResponse> readResponseIterator = stub.read(readRequest);

    Function<User.ReadResponse, T> converter =
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
  }

  @Override
  public InsertResponse insert(List<T> documents, InsertRequestOptions insertRequestOptions)
      throws TigrisDBException {
    User.InsertRequest.Builder insertRequestBuilder =
        User.InsertRequest.newBuilder()
            .setDb(databaseName)
            .setCollection(collectionName)
            .setOptions(User.InsertRequestOptions.newBuilder().build());
    for (T document : documents) {
      insertRequestBuilder.addDocuments(ByteString.copyFromUtf8(document.toString()));
    }
    stub.insert(insertRequestBuilder.build());
    // TODO actual status back
    return new InsertResponse(new TigrisDBResponse("inserted"));
  }

  @Override
  public ReplaceResponse replace(List<T> documents, ReplaceRequestOptions replaceRequestOptions)
      throws TigrisDBException {
    User.ReplaceRequest.Builder replaceRequestBuilder =
        User.ReplaceRequest.newBuilder()
            .setDb(databaseName)
            .setCollection(collectionName)
            .setOptions(User.ReplaceRequestOptions.newBuilder().build());
    for (T document : documents) {
      replaceRequestBuilder.addDocuments(ByteString.copyFromUtf8(document.toString()));
    }
    stub.replace(replaceRequestBuilder.build());
    // TODO actual status back
    return new ReplaceResponse(new TigrisDBResponse("replaced"));
  }

  @Override
  public DeleteResponse delete(TigrisFilter filter, DeleteRequestOptions deleteRequestOptions)
      throws TigrisDBException {
    User.DeleteRequest deleteRequest =
        User.DeleteRequest.newBuilder()
            .setDb(databaseName)
            .setCollection(collectionName)
            .setOptions(
                User.DeleteRequestOptions.newBuilder()
                    .setWriteOptions(User.WriteOptions.newBuilder().build())
                    .build())
            .build();
    stub.delete(deleteRequest);
    // TODO actual status back
    return new DeleteResponse(new TigrisDBResponse("deleted"));
  }

  @Override
  public Iterator<T> read(TigrisFilter filter) throws TigrisDBException {
    return this.read(filter, new ReadRequestOptions());
  }

  @Override
  public InsertResponse insert(List<T> documents) throws TigrisDBException {
    return this.insert(documents, new InsertRequestOptions(new WriteOptions()));
  }

  @Override
  public ReplaceResponse replace(List<T> documents) throws TigrisDBException {
    return this.replace(documents, new ReplaceRequestOptions(new WriteOptions()));
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
