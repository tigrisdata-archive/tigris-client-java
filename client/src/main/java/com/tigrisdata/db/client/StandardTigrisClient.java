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

import com.google.common.annotations.VisibleForTesting;
import com.tigrisdata.db.api.v1.grpc.Api;
import com.tigrisdata.db.api.v1.grpc.TigrisGrpc;
import static com.tigrisdata.db.client.Constants.CREATE_DB_FAILED;
import static com.tigrisdata.db.client.Constants.DROP_DB_FAILED;
import static com.tigrisdata.db.client.Constants.LIST_DBS_FAILED;
import static com.tigrisdata.db.client.TypeConverter.toCreateDatabaseRequest;
import static com.tigrisdata.db.client.TypeConverter.toDropDatabaseRequest;
import static com.tigrisdata.db.client.TypeConverter.toServerMetadata;
import com.tigrisdata.db.client.config.TigrisConfiguration;
import com.tigrisdata.db.client.error.TigrisException;
import com.tigrisdata.tools.schema.core.StandardModelToTigrisJsonSchema;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/** Client for Tigris */
public class StandardTigrisClient extends AbstractTigrisClient implements TigrisClient {

  private final TigrisGrpc.TigrisBlockingStub stub;
  private static final Logger log = LoggerFactory.getLogger(StandardTigrisClient.class);

  private StandardTigrisClient(TigrisConfiguration configuration) {
    super(configuration, new StandardModelToTigrisJsonSchema());
    this.stub = Utilities.newBlockingStub(channel, configuration);
  }

  @VisibleForTesting
  StandardTigrisClient(
      TigrisConfiguration configuration,
      ManagedChannelBuilder<? extends ManagedChannelBuilder> managedChannelBuilder) {
    super(configuration, managedChannelBuilder, new StandardModelToTigrisJsonSchema());
    this.stub = Utilities.newBlockingStub(channel, configuration);
  }

  /**
   * Creates a new instance of @{@link StandardTigrisClient} with the given inputs
   *
   * @param tigrisConfiguration configuration
   * @return a new instance of @{@link StandardTigrisClient}
   */
  public static StandardTigrisClient getInstance(TigrisConfiguration tigrisConfiguration) {
    return new StandardTigrisClient(tigrisConfiguration);
  }

  @Override
  public TigrisDatabase getDatabase(String databaseName) {
    return new StandardTigrisDatabase(
        databaseName, stub, channel, objectMapper, modelToJsonSchema, configuration);
  }

  @Override
  public List<TigrisDatabase> listDatabases(DatabaseOptions listDatabaseOptions)
      throws TigrisException {
    try {
      Api.ListDatabasesRequest listDatabasesRequest = Api.ListDatabasesRequest.newBuilder().build();
      Api.ListDatabasesResponse listDatabasesResponse = stub.listDatabases(listDatabasesRequest);
      List<TigrisDatabase> dbs = new ArrayList<>();
      for (Api.DatabaseInfo databaseInfo : listDatabasesResponse.getDatabasesList()) {
        dbs.add(
            new StandardTigrisDatabase(
                databaseInfo.getDb(),
                stub,
                channel,
                objectMapper,
                modelToJsonSchema,
                configuration));
      }
      return dbs;
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisException(
          LIST_DBS_FAILED,
          TypeConverter.extractTigrisError(statusRuntimeException),
          statusRuntimeException);
    }
  }

  @Override
  public TigrisDatabase createDatabaseIfNotExists(String databaseName) throws TigrisException {
    try {
      stub.createDatabase(toCreateDatabaseRequest(databaseName, DatabaseOptions.DEFAULT_INSTANCE));
      log.info("database created: {}", databaseName);
      return new StandardTigrisDatabase(
          databaseName, stub, channel, objectMapper, modelToJsonSchema, configuration);
    } catch (StatusRuntimeException statusRuntimeException) {
      // ignore the error if the database is already exists
      if (statusRuntimeException.getStatus().getCode() != Status.ALREADY_EXISTS.getCode()) {
        throw new TigrisException(
            CREATE_DB_FAILED,
            TypeConverter.extractTigrisError(statusRuntimeException),
            statusRuntimeException);
      }
      log.info("database already exists: {}", databaseName);
      return new StandardTigrisDatabase(
          databaseName, stub, channel, objectMapper, modelToJsonSchema, configuration);
    }
  }

  @Override
  public DropDatabaseResponse dropDatabase(String databaseName) throws TigrisException {
    try {
      Api.DropDatabaseResponse dropDatabaseResponse =
          stub.dropDatabase(toDropDatabaseRequest(databaseName, DatabaseOptions.DEFAULT_INSTANCE));
      return new DropDatabaseResponse(
          dropDatabaseResponse.getStatus(), dropDatabaseResponse.getMessage());
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisException(
          DROP_DB_FAILED,
          TypeConverter.extractTigrisError(statusRuntimeException),
          statusRuntimeException);
    }
  }

  @Override
  public ServerMetadata getServerMetadata() throws TigrisException {
    try {
      Api.GetInfoResponse apiResponse = stub.getInfo(Api.GetInfoRequest.newBuilder().build());
      return toServerMetadata(apiResponse);
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisException(
          DROP_DB_FAILED,
          TypeConverter.extractTigrisError(statusRuntimeException),
          statusRuntimeException);
    }
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
