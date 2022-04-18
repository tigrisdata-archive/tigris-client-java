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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ListenableFuture;
import com.tigrisdata.db.api.v1.grpc.Api;
import com.tigrisdata.db.api.v1.grpc.TigrisDBGrpc;
import static com.tigrisdata.db.client.Messages.BEGIN_TRANSACTION_FAILED;
import static com.tigrisdata.db.client.Messages.COLLECTIONS_APPLIED;
import static com.tigrisdata.db.client.Messages.DROP_COLLECTION_FAILED;
import static com.tigrisdata.db.client.Messages.LIST_COLLECTION_FAILED;
import static com.tigrisdata.db.client.TypeConverter.toBeginTransactionRequest;
import static com.tigrisdata.db.client.TypeConverter.toDropCollectionRequest;
import com.tigrisdata.db.client.error.TigrisDBException;
import com.tigrisdata.db.type.TigrisCollectionType;
import io.grpc.ManagedChannel;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/** Async implementation of TigrisDB */
public class StandardTigrisAsyncDatabase implements TigrisAsyncDatabase {
  private final String databaseName;
  private final TigrisDBGrpc.TigrisDBFutureStub stub;
  private final ManagedChannel channel;
  private final Executor executor;
  private final ObjectMapper objectMapper;

  StandardTigrisAsyncDatabase(
      String databaseName,
      TigrisDBGrpc.TigrisDBFutureStub stub,
      ManagedChannel channel,
      Executor executor,
      ObjectMapper objectMapper) {
    this.stub = stub;
    this.channel = channel;
    this.databaseName = databaseName;
    this.executor = executor;
    this.objectMapper = objectMapper;
  }

  @Override
  public CompletableFuture<List<CollectionInfo>> listCollections() {
    ListenableFuture<Api.ListCollectionsResponse> listListenableFuture =
        stub.listCollections(
            Api.ListCollectionsRequest.newBuilder().setDb(this.databaseName).build());
    return Utilities.transformFuture(
        listListenableFuture,
        listCollectionsResponse ->
            listCollectionsResponse.getCollectionsList().stream()
                .map(TypeConverter::toCollectionInfo)
                .collect(Collectors.toList()),
        executor,
        LIST_COLLECTION_FAILED);
  }

  @Override
  public CompletableFuture<ApplySchemasResponse> applySchemas(File schemaDirectory) {
    List<URL> schemaURLs = new ArrayList<>();
    try {
      for (File file :
          schemaDirectory.listFiles(file -> file.getName().toLowerCase().endsWith(".json"))) {
        schemaURLs.add(file.toURI().toURL());
      }
      return applySchemas(schemaURLs);
    } catch (NullPointerException | MalformedURLException ex) {
      CompletableFuture completableFuture = new CompletableFuture();
      completableFuture.completeExceptionally(ex);
      return completableFuture;
    }
  }

  @Override
  public CompletableFuture<ApplySchemasResponse> applySchemas(List<URL> collectionsSchemas) {
    CompletableFuture<ApplySchemasResponse> result = new CompletableFuture<>();

    CompletableFuture<TransactionSession> transactionResponseCompletableFuture =
        beginTransaction(TransactionOptions.DEFAULT_INSTANCE);

    transactionResponseCompletableFuture.whenComplete(
        ((transactionSession, throwable) -> {
          // pass on the error
          if (throwable != null) {
            result.completeExceptionally(throwable);
          }
          for (URL collectionsSchema : collectionsSchemas) {
            try {
              transactionSession.applySchema(
                  new TigrisDBJSONSchema(collectionsSchema), CollectionOptions.DEFAULT_INSTANCE);
            } catch (TigrisDBException ex) {
              result.completeExceptionally(ex);
            }
          }
          result.complete(new ApplySchemasResponse(new TigrisDBResponse(COLLECTIONS_APPLIED)));
        }));
    return result;
  }

  @Override
  public CompletableFuture<DropCollectionResponse> dropCollection(String collectionName) {
    // TODO: pull CollectionsOut in API
    ListenableFuture<Api.DropCollectionResponse> dropCollectionResponseListenableFuture =
        stub.dropCollection(
            toDropCollectionRequest(
                databaseName,
                collectionName,
                CollectionOptions.DEFAULT_INSTANCE,
                Optional.empty()));
    return Utilities.transformFuture(
        dropCollectionResponseListenableFuture,
        input -> new DropCollectionResponse(new TigrisDBResponse(input.getMsg())),
        executor,
        DROP_COLLECTION_FAILED);
  }

  @Override
  public <C extends TigrisCollectionType> TigrisAsyncCollection<C> getCollection(
      Class<C> collectionTypeClass) {
    return new StandardTigrisAsyncCollection<>(
        databaseName, collectionTypeClass, channel, executor, objectMapper);
  }

  @Override
  public CompletableFuture<TransactionSession> beginTransaction(
      TransactionOptions transactionOptions) {
    ListenableFuture<Api.BeginTransactionResponse> beginTransactionResponseListenableFuture =
        stub.beginTransaction(toBeginTransactionRequest(databaseName, transactionOptions));
    return Utilities.transformFuture(
        beginTransactionResponseListenableFuture,
        input ->
            new StandardTransactionSession(databaseName, input.getTxCtx(), channel, objectMapper),
        executor,
        BEGIN_TRANSACTION_FAILED);
  }

  @Override
  public String name() {
    return databaseName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    StandardTigrisAsyncDatabase that = (StandardTigrisAsyncDatabase) o;

    return Objects.equals(databaseName, that.databaseName);
  }

  @Override
  public int hashCode() {
    return databaseName != null ? databaseName.hashCode() : 0;
  }

  @Override
  public String toString() {
    return "StandardTigrisAsyncDatabase{" + "databaseName='" + databaseName + '\'' + '}';
  }
}
