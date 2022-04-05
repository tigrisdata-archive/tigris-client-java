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

import com.google.common.util.concurrent.ListenableFuture;
import com.tigrisdata.db.api.v1.grpc.Api;
import com.tigrisdata.db.api.v1.grpc.TigrisDBGrpc;
import com.tigrisdata.db.client.error.TigrisDBException;
import com.tigrisdata.db.client.model.CollectionOptions;
import com.tigrisdata.db.client.model.CreateCollectionResponse;
import com.tigrisdata.db.client.model.DropCollectionResponse;
import com.tigrisdata.db.client.model.TigrisCollectionType;
import com.tigrisdata.db.client.model.TigrisDBResponse;
import com.tigrisdata.db.client.model.TigrisDBSchema;
import com.tigrisdata.db.client.model.TransactionOptions;
import static com.tigrisdata.db.client.model.TypeConverter.toCreateCollectionRequest;
import static com.tigrisdata.db.client.model.TypeConverter.toDropCollectionRequest;
import com.tigrisdata.db.client.utils.Utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class StandardTigrisAsyncDatabase implements TigrisAsyncDatabase {
  private final String databaseName;
  private final TigrisDBGrpc.TigrisDBFutureStub stub;
  private final Executor executor;

  public StandardTigrisAsyncDatabase(
      String databaseName, TigrisDBGrpc.TigrisDBFutureStub stub, Executor executor) {
    this.stub = stub;
    this.databaseName = databaseName;
    this.executor = executor;
  }

  @Override
  public CompletableFuture<List<String>> listCollections() {
    ListenableFuture<Api.ListCollectionsResponse> listListenableFuture =
        stub.listCollections(
            Api.ListCollectionsRequest.newBuilder().setDb(this.databaseName).build());
    return Utilities.transformFuture(
        listListenableFuture,
        listCollectionsResponse -> new ArrayList<>(listCollectionsResponse.getCollectionsList()),
        executor);
  }

  @Override
  public CompletableFuture<CreateCollectionResponse> createCollection(
      String collectionName, TigrisDBSchema schema, CollectionOptions collectionOptions)
      throws TigrisDBException {
    ListenableFuture<Api.CreateCollectionResponse> createCollectionResponseListenableFuture =
        stub.createCollection(
            toCreateCollectionRequest(
                databaseName, collectionName, schema, collectionOptions, Optional.empty()));
    return Utilities.transformFuture(
        createCollectionResponseListenableFuture,
        (input) -> new CreateCollectionResponse(new TigrisDBResponse(input.getMsg())),
        executor);
  }

  @Override
  public CompletableFuture<DropCollectionResponse> dropCollection(String collectionName) {
    // TODO: pull CollectionsOut in API
    ListenableFuture<Api.DropCollectionResponse> dropCollectionResponseListenableFuture =
        stub.dropCollection(
            toDropCollectionRequest(
                databaseName, collectionName, new CollectionOptions(), Optional.empty()));
    return Utilities.transformFuture(
        dropCollectionResponseListenableFuture,
        input -> new DropCollectionResponse(new TigrisDBResponse(input.getMsg())),
        executor);
  }

  @Override
  public <C extends TigrisCollectionType> TigrisAsyncCollection<C> getCollection(
      Class<C> collectionTypeClass) {
    return new StandardTigrisAsyncCollection<>(collectionTypeClass.getSimpleName().toLowerCase());
  }

  @Override
  public CompletableFuture<TransactionAsyncSession> beginTransaction(
      TransactionOptions transactionOptions) {
    throw new UnsupportedOperationException("Not yet implemented");
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
}
