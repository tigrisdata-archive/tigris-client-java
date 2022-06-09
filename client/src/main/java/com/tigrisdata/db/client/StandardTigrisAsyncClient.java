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
import com.google.common.annotations.VisibleForTesting;
import com.google.common.util.concurrent.ListenableFuture;
import com.tigrisdata.db.api.v1.grpc.Api;
import com.tigrisdata.db.api.v1.grpc.TigrisGrpc;
import static com.tigrisdata.db.client.Messages.CREATE_DB_FAILED;
import static com.tigrisdata.db.client.Messages.DROP_DB_FAILED;
import static com.tigrisdata.db.client.Messages.LIST_DBS_FAILED;
import static com.tigrisdata.db.client.Messages.SERVER_METADATA_FAILED;
import static com.tigrisdata.db.client.TypeConverter.toCreateDatabaseRequest;
import static com.tigrisdata.db.client.TypeConverter.toDropDatabaseRequest;
import static com.tigrisdata.db.client.TypeConverter.toListDatabasesRequest;
import static com.tigrisdata.db.client.TypeConverter.toServerMetadata;
import com.tigrisdata.db.client.auth.AuthorizationToken;
import com.tigrisdata.db.client.config.TigrisConfiguration;
import com.tigrisdata.db.client.error.TigrisException;
import com.tigrisdata.tools.schema.core.ModelToJsonSchema;
import com.tigrisdata.tools.schema.core.StandardModelToTigrisJsonSchema;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

/** Async client for Tigris */
public class StandardTigrisAsyncClient extends AbstractTigrisClient implements TigrisAsyncClient {

  private final TigrisGrpc.TigrisStub stub;
  private final TigrisGrpc.TigrisFutureStub futureStub;
  private final TigrisGrpc.TigrisBlockingStub blockingStub;
  private final Executor executor;
  private static final Logger log = LoggerFactory.getLogger(StandardTigrisAsyncClient.class);

  private StandardTigrisAsyncClient(TigrisConfiguration clientConfiguration) {
    this(clientConfiguration, Executors.newCachedThreadPool());
  }

  StandardTigrisAsyncClient(TigrisConfiguration clientConfiguration, Executor executor) {
    // TODO: authorization token injection
    super(clientConfiguration, Optional.empty(), new StandardModelToTigrisJsonSchema());
    this.stub = TigrisGrpc.newStub(channel);
    this.futureStub = TigrisGrpc.newFutureStub(channel);
    this.blockingStub = TigrisGrpc.newBlockingStub(channel);
    this.executor = executor;
  }

  @VisibleForTesting
  StandardTigrisAsyncClient(
      AuthorizationToken authorizationToken,
      TigrisConfiguration configuration,
      ManagedChannelBuilder<? extends ManagedChannelBuilder> managedChannelBuilder) {
    super(
        authorizationToken,
        configuration,
        managedChannelBuilder,
        new StandardModelToTigrisJsonSchema());
    this.stub = TigrisGrpc.newStub(channel);
    this.futureStub = TigrisGrpc.newFutureStub(channel);
    this.blockingStub = TigrisGrpc.newBlockingStub(channel);
    this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
  }

  /**
   * Creates a new instance of @{@link StandardTigrisAsyncClient} with the given inputs
   *
   * @param tigrisConfiguration configuration
   * @return a new instance of @{@link StandardTigrisAsyncClient}
   */
  public static StandardTigrisAsyncClient getInstance(TigrisConfiguration tigrisConfiguration) {
    return new StandardTigrisAsyncClient(tigrisConfiguration);
  }

  /**
   * Creates a new instance of @{@link StandardTigrisAsyncClient} with the given inputs
   *
   * @param tigrisConfiguration configuration
   * @param executor executor that executes the future translation
   * @return a new instance of @{@link StandardTigrisAsyncClient}
   */
  public static StandardTigrisAsyncClient getInstance(
      TigrisConfiguration tigrisConfiguration, Executor executor) {
    return new StandardTigrisAsyncClient(tigrisConfiguration, executor);
  }

  @Override
  public TigrisAsyncDatabase getDatabase(String databaseName) {
    return new StandardTigrisAsyncDatabase(
        databaseName,
        stub,
        futureStub,
        blockingStub,
        channel,
        executor,
        objectMapper,
        modelToJsonSchema);
  }

  @Override
  public CompletableFuture<List<TigrisAsyncDatabase>> listDatabases(
      DatabaseOptions listDatabaseOptions) {

    ListenableFuture<Api.ListDatabasesResponse> listenableFuture =
        futureStub.listDatabases(toListDatabasesRequest(listDatabaseOptions));
    return Utilities.transformFuture(
        listenableFuture,
        listDatabasesResponse -> {
          List<TigrisAsyncDatabase> tigrisAsyncDatabases = new ArrayList<>();
          for (Api.DatabaseInfo databaseInfo : listDatabasesResponse.getDatabasesList()) {
            tigrisAsyncDatabases.add(
                new StandardTigrisAsyncDatabase(
                    databaseInfo.getDb(),
                    stub,
                    futureStub,
                    blockingStub,
                    channel,
                    executor,
                    objectMapper,
                    modelToJsonSchema));
          }
          return tigrisAsyncDatabases;
        },
        executor,
        LIST_DBS_FAILED);
  }

  @Override
  public CompletableFuture<TigrisAsyncDatabase> createDatabaseIfNotExists(String databaseName) {
    ListenableFuture<Api.CreateDatabaseResponse> createDatabaseResponse =
        futureStub.createDatabase(
            toCreateDatabaseRequest(databaseName, DatabaseOptions.DEFAULT_INSTANCE));
    return Utilities.transformFuture(
        createDatabaseResponse,
        response ->
            new StandardTigrisAsyncDatabase(
                databaseName,
                stub,
                futureStub,
                blockingStub,
                channel,
                executor,
                objectMapper,
                modelToJsonSchema),
        executor,
        CREATE_DB_FAILED,
        Optional.of(
            new CreateDatabaseExceptionHandler(
                databaseName,
                stub,
                futureStub,
                blockingStub,
                executor,
                channel,
                objectMapper,
                modelToJsonSchema)));
  }

  @Override
  public CompletableFuture<DropDatabaseResponse> dropDatabase(String databaseName) {
    ListenableFuture<Api.DropDatabaseResponse> dropDatabaseResponse =
        futureStub.dropDatabase(
            toDropDatabaseRequest(databaseName, DatabaseOptions.DEFAULT_INSTANCE));
    return Utilities.transformFuture(
        dropDatabaseResponse,
        response -> new DropDatabaseResponse(response.getStatus(), response.getMessage()),
        executor,
        DROP_DB_FAILED);
  }

  @Override
  public CompletableFuture<ServerMetadata> getServerMetadata() {
    ListenableFuture<Api.GetInfoResponse> infoResponse =
        futureStub.getInfo(Api.GetInfoRequest.newBuilder().build());
    return Utilities.transformFuture(
        infoResponse, response -> toServerMetadata(response), executor, SERVER_METADATA_FAILED);
  }

  @Override
  public void close() {
    channel.shutdown();
  }

  @VisibleForTesting
  ManagedChannel getChannel() {
    return channel;
  }

  /**
   * This is the exception handler for CreateDatabase operation, here it will swallow the exception
   * if the server says database already exists, it will pass the exception further otherwise.
   */
  static class CreateDatabaseExceptionHandler
      implements BiConsumer<CompletableFuture<TigrisAsyncDatabase>, Throwable> {
    private final String dbName;
    private final TigrisGrpc.TigrisStub stub;
    private final TigrisGrpc.TigrisFutureStub futureStub;
    private final TigrisGrpc.TigrisBlockingStub blockingStub;

    private final Executor executor;
    private final ManagedChannel channel;
    private final ObjectMapper objectMapper;
    private final ModelToJsonSchema modelToJsonSchema;

    public CreateDatabaseExceptionHandler(
        String dbName,
        TigrisGrpc.TigrisStub stub,
        TigrisGrpc.TigrisFutureStub futureStub,
        TigrisGrpc.TigrisBlockingStub blockingStub,
        Executor executor,
        ManagedChannel channel,
        ObjectMapper objectMapper,
        ModelToJsonSchema modelToJsonSchema) {
      this.dbName = dbName;
      this.stub = stub;
      this.futureStub = futureStub;
      this.blockingStub = blockingStub;
      this.executor = executor;
      this.channel = channel;
      this.objectMapper = objectMapper;
      this.modelToJsonSchema = modelToJsonSchema;
    }

    @Override
    public void accept(
        CompletableFuture<TigrisAsyncDatabase> completableFuture, Throwable throwable) {
      if (throwable instanceof StatusRuntimeException) {
        if (((StatusRuntimeException) throwable).getStatus().getCode()
            == Status.ALREADY_EXISTS.getCode()) {
          // swallow the already exists exception
          log.info("database already exists: {}", dbName);
          completableFuture.complete(
              new StandardTigrisAsyncDatabase(
                  dbName,
                  stub,
                  futureStub,
                  blockingStub,
                  channel,
                  executor,
                  objectMapper,
                  modelToJsonSchema));
          return;
        }
      }
      // pass on the error otherwise
      if (throwable instanceof StatusRuntimeException) {
        completableFuture.completeExceptionally(
            new TigrisException(
                CREATE_DB_FAILED,
                TypeConverter.extractTigrisError((StatusRuntimeException) throwable),
                throwable));
      } else {
        completableFuture.completeExceptionally(new TigrisException(CREATE_DB_FAILED, throwable));
      }
    }
  }
}
