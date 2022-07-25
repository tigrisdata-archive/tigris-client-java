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
import static com.tigrisdata.db.client.Constants.PUBLISH_FAILED;
import static com.tigrisdata.db.client.Constants.READ_FAILED;
import static com.tigrisdata.db.client.Constants.SUBSCRIBE_FAILED;
import static com.tigrisdata.db.client.Constants.UPDATE_FAILED;
import static com.tigrisdata.db.client.TypeConverter.toDeleteRequest;
import static com.tigrisdata.db.client.TypeConverter.toPublishRequest;
import static com.tigrisdata.db.client.TypeConverter.toReadRequest;
import static com.tigrisdata.db.client.TypeConverter.toReplaceRequest;
import static com.tigrisdata.db.client.TypeConverter.toUpdateRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tigrisdata.db.api.v1.grpc.Api;
import com.tigrisdata.db.api.v1.grpc.TigrisGrpc;
import com.tigrisdata.db.client.error.TigrisException;
import com.tigrisdata.db.type.TigrisCollectionType;
import io.grpc.StatusRuntimeException;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

abstract class AbstractTigrisCollection<T extends TigrisCollectionType> {

  protected final String databaseName;
  protected final String collectionName;
  protected final Class<T> collectionTypeClass;
  protected final TigrisGrpc.TigrisBlockingStub blockingStub;
  protected final ObjectMapper objectMapper;

  public AbstractTigrisCollection(
      String databaseName,
      Class<T> collectionTypeClass,
      TigrisGrpc.TigrisBlockingStub blockingStub,
      ObjectMapper objectMapper) {
    this.databaseName = databaseName;
    this.collectionTypeClass = collectionTypeClass;
    this.collectionName = Utilities.getCollectionName(collectionTypeClass);
    this.blockingStub = blockingStub;
    this.objectMapper = objectMapper;
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
            TypeConverter.transactionAwareStub(
                    blockingStub, ((StandardTransactionSession) tx).getTransactionCtx())
                .read(readRequest);
      } else {
        readResponseIterator = blockingStub.read(readRequest);
      }
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
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisException(
          READ_FAILED,
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
            TypeConverter.transactionAwareStub(
                    blockingStub, ((StandardTransactionSession) tx).getTransactionCtx())
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
            TypeConverter.transactionAwareStub(
                    blockingStub, ((StandardTransactionSession) tx).getTransactionCtx())
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
            TypeConverter.transactionAwareStub(
                    blockingStub, ((StandardTransactionSession) tx).getTransactionCtx())
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
            TypeConverter.transactionAwareStub(
                    blockingStub, ((StandardTransactionSession) tx).getTransactionCtx())
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

  protected PublishResponse<T> publishInternal(List<T> messages) throws TigrisException {
    try {
      Api.PublishRequest publishRequest =
          toPublishRequest(databaseName, collectionName, messages, objectMapper);

      Api.PublishResponse response = blockingStub.publish(publishRequest);

      return new PublishResponse<>(
          response.getStatus(),
          response.getMetadata().getCreatedAt(),
          response.getMetadata().getUpdatedAt(),
          TypeConverter.toArrayOfMap(response.getKeysList(), objectMapper),
          messages);
    } catch (JsonProcessingException ex) {
      throw new TigrisException(JSON_SER_DE_ERROR, ex);
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisException(
          PUBLISH_FAILED,
          TypeConverter.extractTigrisError(statusRuntimeException),
          statusRuntimeException);
    }
  }

  public Iterator<T> subscribeInternal() throws TigrisException {
    try {
      Api.SubscribeRequest subscribeRequest =
          Api.SubscribeRequest.newBuilder()
              .setDb(databaseName)
              .setCollection(collectionName)
              .build();
      Iterator<Api.SubscribeResponse> subscribeResponseIterator =
          blockingStub.subscribe(subscribeRequest);
      Function<Api.SubscribeResponse, T> converter =
          subscribeResponse -> {
            try {
              return objectMapper.readValue(
                  subscribeResponse.getMessage().toStringUtf8(), collectionTypeClass);
            } catch (JsonProcessingException e) {
              throw new IllegalArgumentException("Failed to convert response to  the user type", e);
            }
          };
      return Utilities.transformIterator(subscribeResponseIterator, converter);
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisException(
          SUBSCRIBE_FAILED,
          TypeConverter.extractTigrisError(statusRuntimeException),
          statusRuntimeException);
    }
  }
}
