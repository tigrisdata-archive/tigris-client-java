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

import static com.tigrisdata.db.client.Constants.DELETE_FAILED;
import static com.tigrisdata.db.client.Constants.INSERT_FAILED;
import static com.tigrisdata.db.client.Constants.INSERT_OR_REPLACE_FAILED;
import static com.tigrisdata.db.client.Constants.JSON_SER_DE_ERROR;
import static com.tigrisdata.db.client.Constants.READ_FAILED;
import static com.tigrisdata.db.client.Constants.SEARCH_FAILED;
import static com.tigrisdata.db.client.Constants.UPDATE_FAILED;
import static com.tigrisdata.db.client.TypeConverter.toDeleteRequest;
import static com.tigrisdata.db.client.TypeConverter.toReadRequest;
import static com.tigrisdata.db.client.TypeConverter.toReplaceRequest;
import static com.tigrisdata.db.client.TypeConverter.toUpdateRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tigrisdata.db.api.v1.grpc.Api;
import com.tigrisdata.db.api.v1.grpc.Api.SearchResponse;
import com.tigrisdata.db.api.v1.grpc.TigrisGrpc;
import com.tigrisdata.db.client.config.TigrisConfiguration;
import com.tigrisdata.db.client.error.TigrisException;
import com.tigrisdata.db.client.search.SearchRequest;
import com.tigrisdata.db.client.search.SearchRequestOptions;
import com.tigrisdata.db.client.search.SearchResult;
import com.tigrisdata.db.type.TigrisDocumentCollectionType;
import io.grpc.StatusRuntimeException;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

abstract class AbstractTigrisCollection<T extends TigrisDocumentCollectionType> {

  protected final String databaseName;
  protected final String collectionName;
  protected final Class<T> documentCollectionTypeClass;
  protected final TigrisGrpc.TigrisBlockingStub blockingStub;
  protected final ObjectMapper objectMapper;
  protected final TigrisConfiguration configuration;

  public AbstractTigrisCollection(
      String databaseName,
      Class<T> documentCollectionTypeClass,
      TigrisGrpc.TigrisBlockingStub blockingStub,
      ObjectMapper objectMapper,
      TigrisConfiguration configuration) {
    this.databaseName = databaseName;
    this.documentCollectionTypeClass = documentCollectionTypeClass;
    this.collectionName = Utilities.getCollectionName(documentCollectionTypeClass);
    this.blockingStub = blockingStub;
    this.objectMapper = objectMapper;
    this.configuration = configuration;
  }

  protected Iterator<T> readInternal(
      TigrisFilter filter,
      ReadFields fields,
      ReadRequestOptions readRequestOptions,
      TransactionSession tx)
      throws TigrisException {
    try {
      Api.ReadRequest readRequest =
          toReadRequest(
              databaseName, collectionName, filter, fields, readRequestOptions, objectMapper);
      Iterator<Api.ReadResponse> readResponseIterator;
      if (tx != null) {
        readResponseIterator =
            TypeConverter.transactionAwareStub(blockingStub, ((StandardTransactionSession) tx))
                .read(readRequest);
      } else {
        readResponseIterator = blockingStub.read(readRequest);
      }
      Function<Api.ReadResponse, T> converter =
          readResponse -> {
            try {
              return objectMapper.readValue(
                  readResponse.getData().toStringUtf8(), documentCollectionTypeClass);
            } catch (JsonProcessingException e) {
              throw new IllegalArgumentException("Failed to convert response to  the user type", e);
            }
          };
      return Utilities.transformIterator(readResponseIterator, converter);
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisException(
          READ_FAILED,
          TypeConverter.extractTigrisError(statusRuntimeException),
          statusRuntimeException);
    }
  }

  protected Iterator<SearchResult<T>> searchInternal(
      SearchRequest request, SearchRequestOptions options) throws TigrisException {
    Api.SearchRequest apiSearchRequest =
        TypeConverter.toSearchRequest(databaseName, collectionName, request, options, objectMapper);
    try {
      Iterator<Api.SearchResponse> resp = blockingStub.search(apiSearchRequest);
      Function<SearchResponse, SearchResult<T>> converter =
          r -> SearchResult.from(r, objectMapper, documentCollectionTypeClass);
      return Utilities.transformIterator(resp, converter);
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisException(
          SEARCH_FAILED,
          TypeConverter.extractTigrisError(statusRuntimeException),
          statusRuntimeException);
    }
  }

  protected InsertResponse<T> insertInternal(
      List<T> documents, InsertRequestOptions insertRequestOptions, TransactionSession tx)
      throws TigrisException {
    try {
      Api.InsertRequest insertRequest =
          TypeConverter.toInsertRequest(
              databaseName, collectionName, documents, insertRequestOptions, objectMapper);
      Api.InsertResponse response;
      if (tx != null) {
        response =
            TypeConverter.transactionAwareStub(blockingStub, ((StandardTransactionSession) tx))
                .insert(insertRequest);
      } else {
        response = blockingStub.insert(insertRequest);
      }
      return new InsertResponse<>(
          response.getStatus(),
          response.getMetadata().getCreatedAt(),
          response.getMetadata().getUpdatedAt(),
          TypeConverter.toArrayOfMap(response.getKeysList(), objectMapper),
          documents);
    } catch (JsonProcessingException ex) {
      throw new TigrisException(JSON_SER_DE_ERROR, ex);
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisException(
          INSERT_FAILED,
          TypeConverter.extractTigrisError(statusRuntimeException),
          statusRuntimeException);
    }
  }

  protected UpdateResponse updateInternal(
      TigrisFilter filter,
      UpdateFields updateFields,
      UpdateRequestOptions updateRequestOptions,
      TransactionSession tx)
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

      Api.UpdateResponse updateResponse;
      if (tx != null) {
        updateResponse =
            TypeConverter.transactionAwareStub(blockingStub, ((StandardTransactionSession) tx))
                .update(updateRequest);
      } else {
        updateResponse = blockingStub.update(updateRequest);
      }
      return new UpdateResponse(
          updateResponse.getStatus(),
          updateResponse.getMetadata().getCreatedAt(),
          updateResponse.getMetadata().getUpdatedAt(),
          updateResponse.getModifiedCount());
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisException(
          UPDATE_FAILED,
          TypeConverter.extractTigrisError(statusRuntimeException),
          statusRuntimeException);
    }
  }

  protected DeleteResponse deleteInternal(
      TigrisFilter filter, DeleteRequestOptions deleteRequestOptions, TransactionSession tx)
      throws TigrisException {
    try {
      Api.DeleteRequest deleteRequest =
          toDeleteRequest(databaseName, collectionName, filter, deleteRequestOptions, objectMapper);

      Api.DeleteResponse response;
      if (tx != null) {
        response =
            TypeConverter.transactionAwareStub(blockingStub, ((StandardTransactionSession) tx))
                .delete(deleteRequest);
      } else {
        response = blockingStub.delete(deleteRequest);
      }
      return new DeleteResponse(
          response.getStatus(),
          response.getMetadata().getCreatedAt(),
          response.getMetadata().getUpdatedAt());
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisException(
          DELETE_FAILED,
          TypeConverter.extractTigrisError(statusRuntimeException),
          statusRuntimeException);
    }
  }

  protected InsertOrReplaceResponse<T> insertOrReplaceInternal(
      List<T> documents,
      InsertOrReplaceRequestOptions insertOrReplaceRequestOptions,
      TransactionSession tx)
      throws TigrisException {
    try {
      Api.ReplaceRequest replaceRequest =
          toReplaceRequest(
              databaseName, collectionName, documents, insertOrReplaceRequestOptions, objectMapper);

      Api.ReplaceResponse response;
      if (tx != null) {
        response =
            TypeConverter.transactionAwareStub(blockingStub, ((StandardTransactionSession) tx))
                .replace(replaceRequest);
      } else {
        response = blockingStub.replace(replaceRequest);
      }
      return new InsertOrReplaceResponse<>(
          response.getStatus(),
          response.getMetadata().getCreatedAt(),
          response.getMetadata().getUpdatedAt(),
          TypeConverter.toArrayOfMap(response.getKeysList(), objectMapper),
          documents);
    } catch (JsonProcessingException ex) {
      throw new TigrisException(JSON_SER_DE_ERROR, ex);
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisException(
          INSERT_OR_REPLACE_FAILED,
          TypeConverter.extractTigrisError(statusRuntimeException),
          statusRuntimeException);
    }
  }
}
