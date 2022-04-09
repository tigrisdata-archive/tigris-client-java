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
import com.tigrisdata.db.api.v1.grpc.Api;
import com.tigrisdata.db.api.v1.grpc.TigrisDBGrpc;
import com.tigrisdata.db.client.error.TigrisDBException;
import com.tigrisdata.db.client.model.DeleteRequestOptions;
import com.tigrisdata.db.client.model.DeleteResponse;
import com.tigrisdata.db.client.model.InsertOrReplaceRequestOptions;
import com.tigrisdata.db.client.model.InsertOrReplaceResponse;
import com.tigrisdata.db.client.model.InsertRequestOptions;
import com.tigrisdata.db.client.model.InsertResponse;
import com.tigrisdata.db.client.model.ReadFields;
import com.tigrisdata.db.client.model.ReadRequestOptions;
import com.tigrisdata.db.client.model.TigrisCollectionType;
import com.tigrisdata.db.client.model.TigrisDBResponse;
import com.tigrisdata.db.client.model.TigrisFilter;
import com.tigrisdata.db.client.model.TypeConverter;
import static com.tigrisdata.db.client.model.TypeConverter.readOneDefaultReadRequestOptions;
import static com.tigrisdata.db.client.model.TypeConverter.toDeleteRequest;
import static com.tigrisdata.db.client.model.TypeConverter.toReadRequest;
import static com.tigrisdata.db.client.model.TypeConverter.toReplaceRequest;
import static com.tigrisdata.db.client.model.TypeConverter.toUpdateRequest;
import com.tigrisdata.db.client.model.UpdateFields;
import com.tigrisdata.db.client.model.UpdateRequestOptions;
import com.tigrisdata.db.client.model.UpdateResponse;
import com.tigrisdata.db.client.model.WriteOptions;
import static com.tigrisdata.db.client.utils.ErrorMessages.DELETE_FAILED;
import static com.tigrisdata.db.client.utils.ErrorMessages.INSERT_FAILED;
import static com.tigrisdata.db.client.utils.ErrorMessages.INSERT_OR_REPLACE_FAILED;
import static com.tigrisdata.db.client.utils.ErrorMessages.READ_FAILED;
import static com.tigrisdata.db.client.utils.ErrorMessages.UPDATE_FAILED;
import com.tigrisdata.db.client.utils.Utilities;
import io.grpc.StatusRuntimeException;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

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
    this.collectionName = collectionTypeClass.getSimpleName().toLowerCase();
    this.collectionTypeClass = collectionTypeClass;
    this.stub = stub;
    this.objectMapper = objectMapper;
  }

  @Override
  public Iterator<T> read(
      TigrisFilter filter, ReadFields fields, ReadRequestOptions readRequestOptions)
      throws TigrisDBException {
    try {
      Api.ReadRequest readRequest =
          toReadRequest(
              databaseName, collectionName, filter, fields, readRequestOptions, objectMapper);
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
      return Utilities.transformIterator(readResponseIterator, converter);
    } catch (StatusRuntimeException exception) {
      throw new TigrisDBException(READ_FAILED, exception);
    }
  }

  @Override
  public Iterator<T> read(TigrisFilter filter, ReadFields fields) throws TigrisDBException {
    return this.read(filter, fields, new ReadRequestOptions());
  }

  @Override
  public Optional<T> readOne(TigrisFilter filter) throws TigrisDBException {
    Iterator<T> iterator =
        this.read(filter, ReadFields.empty(), readOneDefaultReadRequestOptions());
    try {
      if (iterator.hasNext()) {
        return Optional.of(iterator.next());
      }
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisDBException(READ_FAILED, statusRuntimeException);
    }
    return Optional.empty();
  }

  @Override
  public InsertResponse insert(List<T> documents, InsertRequestOptions insertRequestOptions)
      throws TigrisDBException {
    try {
      Api.InsertRequest insertRequest =
          TypeConverter.toInsertRequest(
              databaseName, collectionName, documents, insertRequestOptions, objectMapper);
      stub.insert(insertRequest);
      // TODO actual status back
      return new InsertResponse(new TigrisDBResponse(Utilities.INSERT_SUCCESS_RESPONSE));
    } catch (JsonProcessingException ex) {
      throw new TigrisDBException("Failed to serialize documents to JSON", ex);
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisDBException(INSERT_FAILED, statusRuntimeException);
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
      Api.ReplaceRequest replaceRequest =
          toReplaceRequest(
              databaseName, collectionName, documents, insertOrReplaceRequestOptions, objectMapper);
      stub.replace(replaceRequest);
      // TODO actual status back
      return new InsertOrReplaceResponse(new TigrisDBResponse(Utilities.INSERT_SUCCESS_RESPONSE));
    } catch (JsonProcessingException ex) {
      throw new TigrisDBException("Failed to serialize to JSON", ex);
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisDBException(INSERT_OR_REPLACE_FAILED, statusRuntimeException);
    }
  }

  @Override
  public InsertOrReplaceResponse insertOrReplace(List<T> documents) throws TigrisDBException {
    return insertOrReplace(documents, new InsertOrReplaceRequestOptions());
  }

  @Override
  public UpdateResponse update(
      TigrisFilter filter, UpdateFields updateFields, UpdateRequestOptions updateRequestOptions)
      throws TigrisDBException {
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
      return new UpdateResponse(updateResponse.getRc());
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisDBException(UPDATE_FAILED, statusRuntimeException);
    }
  }

  @Override
  public UpdateResponse update(TigrisFilter filter, UpdateFields updateFields)
      throws TigrisDBException {
    return update(filter, updateFields, new UpdateRequestOptions());
  }

  @Override
  public DeleteResponse delete(TigrisFilter filter, DeleteRequestOptions deleteRequestOptions)
      throws TigrisDBException {
    try {
      Api.DeleteRequest deleteRequest =
          toDeleteRequest(databaseName, collectionName, filter, deleteRequestOptions, objectMapper);
      stub.delete(deleteRequest);
      // TODO actual status back
      return new DeleteResponse(new TigrisDBResponse(Utilities.DELETE_SUCCESS_RESPONSE));
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisDBException(DELETE_FAILED, statusRuntimeException);
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
