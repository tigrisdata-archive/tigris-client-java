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
package com.tigrisdata.db.client.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;
import com.tigrisdata.db.api.v1.grpc.Api;
import com.tigrisdata.db.client.error.TigrisDBException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public final class TypeConverter {
  private TypeConverter() {}

  public static Api.ListDatabasesRequest toListDatabasesRequest(DatabaseOptions databaseOptions) {
    return Api.ListDatabasesRequest.newBuilder().build();
  }

  public static Api.CreateDatabaseRequest toCreateDatabaseRequest(
      String databaseName, DatabaseOptions databaseOptions) {
    return Api.CreateDatabaseRequest.newBuilder()
        .setDb(databaseName)
        .setOptions(Api.DatabaseOptions.newBuilder().build())
        .build();
  }

  public static Api.DropDatabaseRequest toDropDatabaseRequest(
      String databaseName, DatabaseOptions databaseOptions) {
    return Api.DropDatabaseRequest.newBuilder()
        .setDb(databaseName)
        .setOptions(Api.DatabaseOptions.newBuilder().build())
        .build();
  }

  public static CollectionInfo toCollectionInfo(Api.CollectionInfo collectionInfo) {
    return new CollectionInfo(collectionInfo.getName());
  }

  public static DatabaseInfo toDatabaseInfo(Api.DatabaseInfo databaseInfo) {
    return new DatabaseInfo(databaseInfo.getName());
  }

  public static Api.CreateOrUpdateCollectionRequest toCreateCollectionRequest(
      String databaseName,
      TigrisDBSchema schema,
      CollectionOptions collectionOptions,
      Optional<Api.TransactionCtx> transactionCtx)
      throws TigrisDBException {
    try {
      return Api.CreateOrUpdateCollectionRequest.newBuilder()
          .setDb(databaseName)
          .setCollection(schema.getName())
          .setSchema(ByteString.copyFrom(schema.getSchemaContent(), StandardCharsets.UTF_8))
          .setOptions(toCollectionOptions(collectionOptions, transactionCtx))
          .build();
    } catch (IOException ioException) {
      throw new TigrisDBException("Failed to read schema content", ioException);
    }
  }

  public static ReadRequestOptions readOneDefaultReadRequestOptions() {
    ReadRequestOptions readRequestOptions = new ReadRequestOptions();
    readRequestOptions.setLimit(1L);
    readRequestOptions.setSkip(0L);
    return readRequestOptions;
  }

  public static Api.DropCollectionRequest toDropCollectionRequest(
      String databaseName,
      String collectionName,
      CollectionOptions collectionOptions,
      Optional<Api.TransactionCtx> transactionCtx) {
    return Api.DropCollectionRequest.newBuilder()
        .setDb(databaseName)
        .setCollection(collectionName)
        .setOptions(toCollectionOptions(collectionOptions, transactionCtx))
        .build();
  }

  public static Api.BeginTransactionRequest toBeginTransactionRequest(
      String databaseName, TransactionOptions transactionOptions) {
    return Api.BeginTransactionRequest.newBuilder()
        .setDb(databaseName)
        .setOptions(Api.TransactionOptions.newBuilder().build())
        .build();
  }

  public static Api.ReadRequest toReadRequest(
      String databaseName,
      String collectionName,
      TigrisFilter filter,
      ReadFields fields,
      ReadRequestOptions readRequestOptions,
      ObjectMapper objectMapper) {
    Api.ReadRequestOptions readRequestOptionsAPI =
        Api.ReadRequestOptions.newBuilder()
            .setLimit(readRequestOptions.getLimit())
            .setSkip(readRequestOptions.getSkip())
            .build();

    Api.ReadRequest.Builder readRequestBuilder =
        Api.ReadRequest.newBuilder()
            .setDb(databaseName)
            .setCollection(collectionName)
            .setFilter(ByteString.copyFrom(filter.toJSON(objectMapper), StandardCharsets.UTF_8))
            .setOptions(readRequestOptionsAPI);
    if (!fields.isEmpty()) {
      readRequestBuilder.setFields(ByteString.copyFromUtf8(fields.toJSON(objectMapper)));
    }
    return readRequestBuilder.build();
  }

  public static <T> Api.InsertRequest toInsertRequest(
      String databaseName,
      String collectionName,
      List<T> documents,
      InsertRequestOptions insertRequestOptions,
      ObjectMapper objectMapper)
      throws JsonProcessingException {
    Api.InsertRequest.Builder insertRequestBuilder =
        Api.InsertRequest.newBuilder()
            .setDb(databaseName)
            .setCollection(collectionName)
            .setOptions(
                Api.InsertRequestOptions.newBuilder()
                    .setWriteOptions(toWriteOptions(insertRequestOptions.getWriteOptions()))
                    .build());
    for (T document : documents) {
      insertRequestBuilder.addDocuments(
          ByteString.copyFromUtf8(objectMapper.writeValueAsString(document)));
    }
    return insertRequestBuilder.build();
  }

  public static <T> Api.ReplaceRequest toReplaceRequest(
      String databaseName,
      String collectionName,
      List<T> documents,
      InsertOrReplaceRequestOptions insertOrReplaceRequestOptions,
      ObjectMapper objectMapper)
      throws JsonProcessingException {
    Api.ReplaceRequest.Builder replaceRequestBuilder =
        Api.ReplaceRequest.newBuilder()
            .setDb(databaseName)
            .setCollection(collectionName)
            .setOptions(
                Api.ReplaceRequestOptions.newBuilder()
                    .setWriteOptions(
                        toWriteOptions(insertOrReplaceRequestOptions.getWriteOptions()))
                    .build());
    for (T document : documents) {
      replaceRequestBuilder.addDocuments(
          ByteString.copyFromUtf8(objectMapper.writeValueAsString(document)));
    }
    return replaceRequestBuilder.build();
  }

  public static Api.UpdateRequest toUpdateRequest(
      String databaseName,
      String collectionName,
      TigrisFilter filter,
      UpdateFields updateFields,
      UpdateRequestOptions updateRequestOptions,
      ObjectMapper objectMapper) {

    return Api.UpdateRequest.newBuilder()
        .setDb(databaseName)
        .setCollection(collectionName)
        .setFilter(ByteString.copyFromUtf8(filter.toJSON(objectMapper)))
        .setFields(ByteString.copyFromUtf8(updateFields.toJSON(objectMapper)))
        .setOptions(
            Api.UpdateRequestOptions.newBuilder()
                .setWriteOptions(toWriteOptions(updateRequestOptions.getWriteOptions()))
                .build())
        .build();
  }

  public static Api.DeleteRequest toDeleteRequest(
      String databaseName,
      String collectionName,
      TigrisFilter filter,
      DeleteRequestOptions deleteRequestOptions,
      ObjectMapper objectMapper) {
    return Api.DeleteRequest.newBuilder()
        .setDb(databaseName)
        .setCollection(collectionName)
        .setFilter(ByteString.copyFromUtf8(filter.toJSON(objectMapper)))
        .setOptions(
            Api.DeleteRequestOptions.newBuilder()
                .setWriteOptions(toWriteOptions(deleteRequestOptions.getWriteOptions()))
                .build())
        .build();
  }

  public static TransactionCtx toTransactionCtx(Api.TransactionCtx transactionCtx) {
    return new TransactionCtx(transactionCtx.getId(), transactionCtx.getOrigin());
  }

  public static Api.TransactionCtx toTransactionCtx(TransactionCtx transactionCtx) {
    return Api.TransactionCtx.newBuilder()
        .setId(transactionCtx.getId())
        .setOrigin(transactionCtx.getOrigin())
        .build();
  }

  private static Api.CollectionOptions toCollectionOptions(
      CollectionOptions collectionOptions, Optional<Api.TransactionCtx> transactionCtx) {
    Api.CollectionOptions.Builder collectionsOptionBuilder = Api.CollectionOptions.newBuilder();
    transactionCtx.ifPresent(collectionsOptionBuilder::setTxCtx);
    return collectionsOptionBuilder.build();
  }

  private static Api.WriteOptions toWriteOptions(WriteOptions writeOptions) {
    Api.WriteOptions.Builder writeOptionsBuilder = Api.WriteOptions.newBuilder();
    if (writeOptions.getTransactionCtx() != null) {
      writeOptionsBuilder.setTxCtx(toTransactionCtx(writeOptions.getTransactionCtx()));
    }
    return writeOptionsBuilder.build();
  }
}
