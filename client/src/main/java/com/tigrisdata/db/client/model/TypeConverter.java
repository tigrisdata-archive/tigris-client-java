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

import com.google.protobuf.ByteString;
import com.tigrisdata.db.api.v1.grpc.Api;
import com.tigrisdata.db.client.error.TigrisDBException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

  public static Api.CreateCollectionRequest toCreateCollectionRequest(
      String databaseName,
      String collectionName,
      TigrisDBSchema schema,
      CollectionOptions collectionOptions,
      Optional<Api.TransactionCtx> transactionCtx)
      throws TigrisDBException {
    try {
      return Api.CreateCollectionRequest.newBuilder()
          .setDb(databaseName)
          .setCollection(collectionName)
          .setSchema(ByteString.copyFrom(schema.getSchemaContent(), StandardCharsets.UTF_8))
          .setOptions(toCollectionOptions(collectionOptions, transactionCtx))
          .build();
    } catch (IOException ioException) {
      throw new TigrisDBException("Failed to read schema content", ioException);
    }
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

  private static Api.CollectionOptions toCollectionOptions(
      CollectionOptions collectionOptions, Optional<Api.TransactionCtx> transactionCtx) {
    Api.CollectionOptions.Builder collectionsOptionBuilder = Api.CollectionOptions.newBuilder();
    transactionCtx.ifPresent(collectionsOptionBuilder::setTxCtx);
    return collectionsOptionBuilder.build();
  }
}
