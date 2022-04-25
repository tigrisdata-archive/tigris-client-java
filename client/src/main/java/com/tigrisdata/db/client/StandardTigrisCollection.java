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
package com.tigrisdata.db.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tigrisdata.db.api.v1.grpc.Api;
import com.tigrisdata.db.api.v1.grpc.TigrisDBGrpc;
import static com.tigrisdata.db.client.Messages.DELETE_FAILED;
import static com.tigrisdata.db.client.Messages.INSERT_FAILED;
import static com.tigrisdata.db.client.Messages.INSERT_OR_REPLACE_FAILED;
import static com.tigrisdata.db.client.Messages.READ_FAILED;
import static com.tigrisdata.db.client.Messages.UPDATE_FAILED;
import static com.tigrisdata.db.client.TypeConverter.readOneDefaultReadRequestOptions;
import static com.tigrisdata.db.client.TypeConverter.toDeleteRequest;
import static com.tigrisdata.db.client.TypeConverter.toReadRequest;
import static com.tigrisdata.db.client.TypeConverter.toReplaceRequest;
import static com.tigrisdata.db.client.TypeConverter.toUpdateRequest;
import com.tigrisdata.db.client.error.TigrisException;
import com.tigrisdata.db.type.TigrisCollectionType;
import io.grpc.StatusRuntimeException;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
/** TigrisDB collection implementation */
public class StandardTigrisCollection<T extends TigrisCollectionType>
    implements TigrisCollection<T> {

  private final String databaseName;
  private final String collectionName;
  private final Class<T> collectionTypeClass;
  private final TigrisDBGrpc.TigrisDBBlockingStub stub;
  private final ObjectMapper objectMapper;

  StandardTigrisCollection(
      String databaseName,
      Class<T> collectionTypeClass,
      TigrisDBGrpc.TigrisDBBlockingStub stub,
      ObjectMapper objectMapper) {
    this.databaseName = databaseName;
    this.collectionName = Utilities.getCollectionName(collectionTypeClass);
    this.collectionTypeClass = collectionTypeClass;
    this.stub = stub;
    this.objectMapper = objectMapper;
  }

  @Override
  public Iterator<T> read(
      TigrisFilter filter, ReadFields fields, ReadRequestOptions readRequestOptions)
      throws TigrisException {
    try {
      Api.ReadRequest readRequest =
          toReadRequest(
              databaseName, collectionName, filter, fields, readRequestOptions, objectMapper);
      Iterator<Api.ReadResponse> readResponseIterator = stub.read(readRequest);

      Function<Api.ReadResponse, T> converter =
          readResponse -> {
            try {
              return objectMapper.readValue(
                  readResponse.getData().toStringUtf8(), collectionTypeClass);
            } catch (JsonProcessingException e) {
              throw new IllegalArgumentException("Failed to convert response to  the user type", e);
            }
          };
      return Utilities.transformIterator(readResponseIterator, converter);
    } catch (StatusRuntimeException exception) {
      throw new TigrisException(READ_FAILED, exception);
    }
  }

  @Override
  public Iterator<T> read(TigrisFilter filter, ReadFields fields) throws TigrisException {
    return this.read(filter, fields, new ReadRequestOptions());
  }

  @Override
  public Optional<T> readOne(TigrisFilter filter) throws TigrisException {
    Iterator<T> iterator =
        this.read(filter, ReadFields.empty(), readOneDefaultReadRequestOptions());
    try {
      if (iterator.hasNext()) {
        return Optional.of(iterator.next());
      }
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisException(READ_FAILED, statusRuntimeException);
    }
    return Optional.empty();
  }

  @Override
  public InsertResponse insert(List<T> documents, InsertRequestOptions insertRequestOptions)
      throws TigrisException {
    try {
      Api.InsertRequest insertRequest =
          TypeConverter.toInsertRequest(
              databaseName, collectionName, documents, insertRequestOptions, objectMapper);
      Api.InsertResponse response = stub.insert(insertRequest);
      return new InsertResponse(
          response.getStatus(),
          response.getMetadata().getCreatedAt(),
          response.getMetadata().getUpdatedAt());
    } catch (JsonProcessingException ex) {
      throw new TigrisException("Failed to serialize documents to JSON", ex);
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisException(INSERT_FAILED, statusRuntimeException);
    }
  }

  @Override
  public InsertResponse insert(List<T> documents) throws TigrisException {
    return insert(documents, new InsertRequestOptions(new WriteOptions()));
  }

  @Override
  public InsertResponse insert(T document) throws TigrisException {
    return insert(Collections.singletonList(document));
  }

  @Override
  public InsertOrReplaceResponse insertOrReplace(
      List<T> documents, InsertOrReplaceRequestOptions insertOrReplaceRequestOptions)
      throws TigrisException {
    try {
      Api.ReplaceRequest replaceRequest =
          toReplaceRequest(
              databaseName, collectionName, documents, insertOrReplaceRequestOptions, objectMapper);
      Api.ReplaceResponse response = stub.replace(replaceRequest);
      return new InsertOrReplaceResponse(
          response.getStatus(),
          response.getMetadata().getCreatedAt(),
          response.getMetadata().getUpdatedAt());
    } catch (JsonProcessingException ex) {
      throw new TigrisException("Failed to serialize to JSON", ex);
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisException(INSERT_OR_REPLACE_FAILED, statusRuntimeException);
    }
  }

  @Override
  public InsertOrReplaceResponse insertOrReplace(List<T> documents) throws TigrisException {
    return insertOrReplace(documents, new InsertOrReplaceRequestOptions());
  }

  @Override
  public UpdateResponse update(
      TigrisFilter filter, UpdateFields updateFields, UpdateRequestOptions updateRequestOptions)
      throws TigrisException {
    try {
      Api.UpdateRequest updateRequest =
          toUpdateRequest(
              databaseName,
              collectionName,
              filter,
              updateFields,
              updateRequestOptions,
              objectMapper);
      Api.UpdateResponse updateResponse = stub.update(updateRequest);
      return new UpdateResponse(
          updateResponse.getStatus(),
          updateResponse.getMetadata().getCreatedAt(),
          updateResponse.getMetadata().getUpdatedAt(),
          updateResponse.getModifiedCount());
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisException(UPDATE_FAILED, statusRuntimeException);
    }
  }

  @Override
  public UpdateResponse update(TigrisFilter filter, UpdateFields updateFields)
      throws TigrisException {
    return update(filter, updateFields, new UpdateRequestOptions());
  }

  @Override
  public DeleteResponse delete(TigrisFilter filter, DeleteRequestOptions deleteRequestOptions)
      throws TigrisException {
    try {
      Api.DeleteRequest deleteRequest =
          toDeleteRequest(databaseName, collectionName, filter, deleteRequestOptions, objectMapper);
      Api.DeleteResponse response = stub.delete(deleteRequest);
      return new DeleteResponse(
          response.getStatus(),
          response.getMetadata().getCreatedAt(),
          response.getMetadata().getUpdatedAt());
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisException(DELETE_FAILED, statusRuntimeException);
    }
  }

  @Override
  public DeleteResponse delete(TigrisFilter filter) throws TigrisException {
    return delete(filter, new DeleteRequestOptions(new WriteOptions()));
  }

  @Override
  public String name() {
    return collectionName;
  }
}
