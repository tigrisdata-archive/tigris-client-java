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
import com.tigrisdata.db.api.v1.grpc.TigrisGrpc;
import static com.tigrisdata.db.client.Messages.BEGIN_TRANSACTION_FAILED;
import static com.tigrisdata.db.client.Messages.DESCRIBE_DB_FAILED;
import static com.tigrisdata.db.client.Messages.DROP_COLLECTION_FAILED;
import static com.tigrisdata.db.client.Messages.LIST_COLLECTION_FAILED;
import static com.tigrisdata.db.client.TypeConverter.toBeginTransactionRequest;
import static com.tigrisdata.db.client.TypeConverter.toDatabaseDescription;
import static com.tigrisdata.db.client.TypeConverter.toDropCollectionRequest;
import com.tigrisdata.db.client.error.TigrisException;
import com.tigrisdata.db.type.TigrisCollectionType;
import com.tigrisdata.tools.schema.core.ModelToJsonSchema;
import io.grpc.ManagedChannel;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/** Async implementation of Tigris database */
public class StandardTigrisAsyncDatabase extends AbstractTigrisDatabase
    implements TigrisAsyncDatabase {
  private final TigrisGrpc.TigrisFutureStub futureStub;
  private final ManagedChannel channel;
  private final Executor executor;
  private final ObjectMapper objectMapper;
  private final ModelToJsonSchema modelToJsonSchema;

  StandardTigrisAsyncDatabase(
      String databaseName,
      TigrisGrpc.TigrisFutureStub futureStub,
      TigrisGrpc.TigrisBlockingStub blockingStub,
      ManagedChannel channel,
      Executor executor,
      ObjectMapper objectMapper,
      ModelToJsonSchema modelToJsonSchema) {
    super(databaseName, blockingStub);
    this.futureStub = futureStub;
    this.channel = channel;
    this.executor = executor;
    this.objectMapper = objectMapper;
    this.modelToJsonSchema = modelToJsonSchema;
  }

  @Override
  public CompletableFuture<List<CollectionInfo>> listCollections() {
    ListenableFuture<Api.ListCollectionsResponse> listListenableFuture =
        futureStub.listCollections(Api.ListCollectionsRequest.newBuilder().setDb(this.db).build());
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
  public CompletableFuture<CreateOrUpdateCollectionsResponse> createOrUpdateCollections(
      Class<? extends TigrisCollectionType>... collectionModelTypes) {
    CompletableFuture<CreateOrUpdateCollectionsResponse> result = new CompletableFuture<>();

    CompletableFuture<TransactionSession> transactionResponseCompletableFuture =
        beginTransaction(TransactionOptions.DEFAULT_INSTANCE);

    transactionResponseCompletableFuture.whenComplete(
        ((transactionSession, throwable) -> {
          // pass on the error
          if (throwable != null) {
            result.completeExceptionally(throwable);
          }
          CreateOrUpdateCollectionsResponse response = null;
          for (Class<? extends TigrisCollectionType> collectionModel : collectionModelTypes) {
            try {
              String schemaContent = modelToJsonSchema.toJsonSchema(collectionModel).toString();

              response =
                  createOrUpdateCollections(
                      transactionSession,
                      new TigrisJSONSchema(schemaContent),
                      CollectionOptions.DEFAULT_INSTANCE);
            } catch (Exception ex) {
              result.completeExceptionally(ex);
            }
          }
          if (response != null) {
            result.complete(response);
          }
        }));
    return result;
  }

  @Override
  public CompletableFuture<CreateOrUpdateCollectionsResponse> createOrUpdateCollections(
      String[] packagesToScan, Optional<Predicate<Class<? extends TigrisCollectionType>>> filter) {
    return this.createOrUpdateCollections(
        Utilities.scanTigrisCollectionModels(packagesToScan, filter));
  }

  @Override
  public <T extends TigrisCollectionType> CompletableFuture<DropCollectionResponse> dropCollection(
      Class<T> collectionTypeClass) {
    ListenableFuture<Api.DropCollectionResponse> dropCollectionResponseListenableFuture =
        futureStub.dropCollection(
            toDropCollectionRequest(
                db,
                Utilities.getCollectionName(collectionTypeClass),
                CollectionOptions.DEFAULT_INSTANCE,
                Optional.empty()));
    return Utilities.transformFuture(
        dropCollectionResponseListenableFuture,
        response -> new DropCollectionResponse(response.getStatus(), response.getMessage()),
        executor,
        DROP_COLLECTION_FAILED);
  }

  @Override
  public <C extends TigrisCollectionType> TigrisAsyncCollection<C> getCollection(
      Class<C> collectionTypeClass) {
    return new StandardTigrisAsyncCollection<>(
        db, collectionTypeClass, channel, executor, objectMapper);
  }

  @Override
  public CompletableFuture<TransactionSession> beginTransaction(
      TransactionOptions transactionOptions) {
    ListenableFuture<Api.BeginTransactionResponse> beginTransactionResponseListenableFuture =
        futureStub.beginTransaction(toBeginTransactionRequest(db, transactionOptions));
    return Utilities.transformFuture(
        beginTransactionResponseListenableFuture,
        response -> new StandardTransactionSession(db, response.getTxCtx(), channel),
        executor,
        BEGIN_TRANSACTION_FAILED);
  }

  @Override
  public CompletableFuture<DatabaseDescription> describe() throws TigrisException {
    ListenableFuture<Api.DescribeDatabaseResponse> describeDatabaseResponseListenableFuture =
        futureStub.describeDatabase(Api.DescribeDatabaseRequest.newBuilder().setDb(db).build());

    return Utilities.transformFuture(
        describeDatabaseResponseListenableFuture,
        response -> {
          try {
            return toDatabaseDescription(response);
          } catch (TigrisException e) {
            throw new IllegalArgumentException(e);
          }
        },
        executor,
        DESCRIBE_DB_FAILED);
  }

  @Override
  public String name() {
    return db;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    StandardTigrisAsyncDatabase that = (StandardTigrisAsyncDatabase) o;

    return Objects.equals(db, that.db);
  }

  @Override
  public int hashCode() {
    return db != null ? db.hashCode() : 0;
  }

  @Override
  public String toString() {
    return "StandardTigrisAsyncDatabase{" + "db='" + db + '\'' + '}';
  }
}
