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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.util.concurrent.ListenableFuture;
import com.tigrisdata.db.api.v1.grpc.Api;
import com.tigrisdata.db.api.v1.grpc.TigrisDBGrpc;
import com.tigrisdata.db.client.model.AuthorizationToken;
import com.tigrisdata.db.client.config.TigrisDBConfiguration;
import com.tigrisdata.db.client.model.DatabaseOptions;
import com.tigrisdata.db.client.model.TigrisDBResponse;
import static com.tigrisdata.db.client.model.TypeConverter.toCreateDatabaseRequest;
import static com.tigrisdata.db.client.model.TypeConverter.toDropDatabaseRequest;
import static com.tigrisdata.db.client.model.TypeConverter.toListDatabasesRequest;
import static com.tigrisdata.db.client.utils.ErrorMessages.CREATE_DB_FAILED;
import static com.tigrisdata.db.client.utils.ErrorMessages.DROP_DB_FAILED;
import static com.tigrisdata.db.client.utils.ErrorMessages.LIST_DBS_FAILED;
import com.tigrisdata.db.client.utils.Utilities;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class StandardTigrisDBAsyncClient extends AbstractTigrisDBClient
    implements TigrisDBAsyncClient {

  private final TigrisDBGrpc.TigrisDBFutureStub stub;
  private final Executor executor;

  private StandardTigrisDBAsyncClient(
      TigrisDBConfiguration clientConfiguration, AuthorizationToken authorizationToken) {
    this(clientConfiguration, authorizationToken, Executors.newCachedThreadPool());
  }

  StandardTigrisDBAsyncClient(
      TigrisDBConfiguration clientConfiguration,
      AuthorizationToken authorizationToken,
      Executor executor) {
    super(clientConfiguration, authorizationToken);
    this.stub = TigrisDBGrpc.newFutureStub(channel);
    this.executor = executor;
  }

  @VisibleForTesting
  StandardTigrisDBAsyncClient(
      AuthorizationToken authorizationToken,
      TigrisDBConfiguration configuration,
      ManagedChannelBuilder<? extends ManagedChannelBuilder> managedChannelBuilder) {
    super(authorizationToken, configuration, managedChannelBuilder);
    this.stub = TigrisDBGrpc.newFutureStub(channel);
    this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
  }

  /**
   * Creates a new instance of @{@link StandardTigrisDBAsyncClient} with the given inputs
   *
   * @param authorizationToken authorization token
   * @param tigrisDBConfiguration configuration
   * @return a new instance of @{@link StandardTigrisDBAsyncClient}
   */
  public static StandardTigrisDBAsyncClient getInstance(
      AuthorizationToken authorizationToken, TigrisDBConfiguration tigrisDBConfiguration) {
    return new StandardTigrisDBAsyncClient(tigrisDBConfiguration, authorizationToken);
  }

  /**
   * Creates a new instance of @{@link StandardTigrisDBAsyncClient} with the given inputs
   *
   * @param authorizationToken authorization token
   * @param tigrisDBConfiguration configuration
   * @param executor executor that executes the future translation
   * @return a new instance of @{@link StandardTigrisDBAsyncClient}
   */
  public static StandardTigrisDBAsyncClient getInstance(
      AuthorizationToken authorizationToken,
      TigrisDBConfiguration tigrisDBConfiguration,
      Executor executor) {
    return new StandardTigrisDBAsyncClient(tigrisDBConfiguration, authorizationToken, executor);
  }

  @Override
  public TigrisAsyncDatabase getDatabase(String databaseName) {
    return new StandardTigrisAsyncDatabase(databaseName, stub, channel, executor, objectMapper);
  }

  @Override
  public CompletableFuture<List<TigrisAsyncDatabase>> listDatabases(
      DatabaseOptions listDatabaseOptions) {

    ListenableFuture<Api.ListDatabasesResponse> listenableFuture =
        stub.listDatabases(toListDatabasesRequest(listDatabaseOptions));
    return Utilities.transformFuture(
        listenableFuture,
        listDatabasesResponse -> {
          List<TigrisAsyncDatabase> tigrisAsyncDatabases = new ArrayList<>();
          for (Api.DatabaseInfo databaseInfo : listDatabasesResponse.getDatabasesList()) {
            tigrisAsyncDatabases.add(
                new StandardTigrisAsyncDatabase(
                    databaseInfo.getName(), stub, channel, executor, objectMapper));
          }
          return tigrisAsyncDatabases;
        },
        executor,
        LIST_DBS_FAILED);
  }

  @Override
  public CompletableFuture<TigrisDBResponse> createDatabase(
      String databaseName, DatabaseOptions databaseOptions) {
    ListenableFuture<Api.CreateDatabaseResponse> createDatabaseResponse =
        stub.createDatabase(toCreateDatabaseRequest(databaseName, databaseOptions));
    return Utilities.transformFuture(
        createDatabaseResponse,
        apiResponse -> new TigrisDBResponse(apiResponse.getMsg()),
        executor,
        CREATE_DB_FAILED);
  }

  @Override
  public CompletableFuture<TigrisDBResponse> dropDatabase(
      String databaseName, DatabaseOptions databaseOptions) {
    ListenableFuture<Api.DropDatabaseResponse> dropDatabaseResponse =
        stub.dropDatabase(toDropDatabaseRequest(databaseName, databaseOptions));
    return Utilities.transformFuture(
        dropDatabaseResponse,
        apiResponse -> new TigrisDBResponse(apiResponse.getMsg()),
        executor,
        DROP_DB_FAILED);
  }

  @Override
  public void close() {
    channel.shutdown();
  }

  @VisibleForTesting
  ManagedChannel getChannel() {
    return channel;
  }
}
