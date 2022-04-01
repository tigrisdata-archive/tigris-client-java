/*
 * Copyright 2022 Tigris Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import com.tigrisdata.db.client.model.InsertOrReplaceRequestOptions;
import com.tigrisdata.db.client.model.InsertOrReplaceResponse;
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
import io.grpc.StatusRuntimeException;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
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

  StandardTigrisCollection(
      String databaseName, Class<T> collectionTypeClass, TigrisDBGrpc.TigrisDBBlockingStub stub) {
    this.databaseName = databaseName;
    this.collectionName = collectionTypeClass.getSimpleName().toLowerCase();
    this.collectionTypeClass = collectionTypeClass;
    this.stub = stub;
    this.objectMapper = new ObjectMapper();
  }

  @Override
  public Iterator<T> read(
      TigrisFilter filter, List<Field<?>> fields, ReadRequestOptions readRequestOptions)
      throws TigrisDBException {
    try {
      Api.ReadRequestOptions readRequestOptionsAPI =
          Api.ReadRequestOptions.newBuilder()
              .setLimit(readRequestOptions.getLimit())
              .setSkip(readRequestOptions.getSkip())
              .build();

      Api.ReadRequest readRequest =
          Api.ReadRequest.newBuilder()
              .setDb(databaseName)
              .setCollection(collectionName)
              .setFilter(ByteString.copyFrom(filter.toString(), StandardCharsets.UTF_8))
              .setFields(ByteString.copyFromUtf8(Utilities.fields(fields)))
              .setOptions(readRequestOptionsAPI)
              .build();
      Iterator<Api.ReadResponse> readResponseIterator = stub.read(readRequest);

      Function<Api.ReadResponse, T> converter =
          readResponse -> {
            try {
              return objectMapper.readValue(
                  readResponse.getDoc().toStringUtf8(), collectionTypeClass);
            } catch (JsonProcessingException e) {
              throw new IllegalArgumentException("Failed to convert response to  the user type", e);
            }
          };
      return Utilities.from(readResponseIterator, converter);
    } catch (JsonProcessingException jsonProcessingException) {
      throw new TigrisDBException("Failed to process fields", jsonProcessingException);
    } catch (StatusRuntimeException exception) {
      throw new TigrisDBException("Failed to read", exception);
    }
  }

  @Override
  public Iterator<T> read(TigrisFilter filter, List<Field<?>> fields) throws TigrisDBException {
    return this.read(filter, fields, new ReadRequestOptions());
  }

  @Override
  public T readOne(TigrisFilter filter) throws TigrisDBException {
    ReadRequestOptions readRequestOptions = new ReadRequestOptions();
    readRequestOptions.setLimit(1L);
    readRequestOptions.setSkip(0L);
    Iterator<T> iterator = this.read(filter, Collections.emptyList(), readRequestOptions);
    if (iterator.hasNext()) {
      return iterator.next();
    }
    return null;
  }

  @Override
  public InsertResponse insert(List<T> documents, InsertRequestOptions insertRequestOptions)
      throws TigrisDBException {
    try {
      Api.InsertRequest.Builder insertRequestBuilder =
          Api.InsertRequest.newBuilder()
              .setDb(databaseName)
              .setCollection(collectionName)
              .setOptions(
                  Api.InsertRequestOptions.newBuilder()
                      .setWriteOptions(Api.WriteOptions.newBuilder().build())
                      .build());
      for (T document : documents) {
        insertRequestBuilder.addDocuments(
            ByteString.copyFromUtf8(Utilities.OBJECT_MAPPER.writeValueAsString(document)));
      }
      stub.insert(insertRequestBuilder.build());
      // TODO actual status back
      return new InsertResponse(new TigrisDBResponse("inserted"));
    } catch (JsonProcessingException ex) {
      throw new TigrisDBException("Failed to serialize to JSON", ex);
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisDBException("Failed to insert ", statusRuntimeException);
    }
  }

  @Override
  public InsertResponse insert(List<T> documents) throws TigrisDBException {
    return insert(documents, new InsertRequestOptions(new WriteOptions()));
  }

  @Override
  public InsertResponse insert(T document) throws TigrisDBException {
    return insert(Collections.singletonList(document));
  }

  @Override
  public InsertOrReplaceResponse insertOrReplace(
      List<T> documents, InsertOrReplaceRequestOptions insertOrReplaceRequestOptions)
      throws TigrisDBException {
    try {
      Api.ReplaceRequest.Builder replaceRequestBuilder =
          Api.ReplaceRequest.newBuilder()
              .setDb(databaseName)
              .setCollection(collectionName)
              .setOptions(
                  Api.ReplaceRequestOptions.newBuilder()
                      .setWriteOptions(Api.WriteOptions.newBuilder().build())
                      .build());
      for (T document : documents) {
        replaceRequestBuilder.addDocuments(
            ByteString.copyFromUtf8(Utilities.OBJECT_MAPPER.writeValueAsString(document)));
      }
      stub.replace(replaceRequestBuilder.build());
      // TODO actual status back
      return new InsertOrReplaceResponse(new TigrisDBResponse("inserted"));
    } catch (JsonProcessingException ex) {
      throw new TigrisDBException("Failed to serialize to JSON", ex);
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisDBException("Failed to insert ", statusRuntimeException);
    }
  }

  @Override
  public InsertOrReplaceResponse insertOrReplace(List<T> documents) throws TigrisDBException {
    return insertOrReplace(documents, new InsertOrReplaceRequestOptions());
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
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisDBException("Failed to update ", statusRuntimeException);
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
    try {
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
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisDBException("Failed to delete", statusRuntimeException);
    }
  }

  @Override
  public DeleteResponse delete(TigrisFilter filter) throws TigrisDBException {
    return delete(filter, new DeleteRequestOptions(new WriteOptions()));
  }

  @Override
  public String name() {
    return collectionName;
  }
}
