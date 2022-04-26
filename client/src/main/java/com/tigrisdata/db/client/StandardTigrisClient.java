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
import com.tigrisdata.db.api.v1.grpc.TigrisDBGrpc;
import static com.tigrisdata.db.client.Messages.CREATE_DB_FAILED;
import static com.tigrisdata.db.client.Messages.DROP_DB_FAILED;
import static com.tigrisdata.db.client.Messages.LIST_DBS_FAILED;
import static com.tigrisdata.db.client.TypeConverter.toCreateDatabaseRequest;
import static com.tigrisdata.db.client.TypeConverter.toDropDatabaseRequest;
import com.tigrisdata.db.client.auth.AuthorizationToken;
import com.tigrisdata.db.client.config.TigrisConfiguration;
import com.tigrisdata.db.client.error.TigrisException;
import com.tigrisdata.tools.schema.core.StandardModelToTigrisDBJsonSchema;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Client for TigrisDB */
public class StandardTigrisClient extends AbstractTigrisDBClient implements TigrisClient {

  private final TigrisDBGrpc.TigrisDBBlockingStub stub;
  private static final Logger log = LoggerFactory.getLogger(StandardTigrisClient.class);

  private StandardTigrisClient(TigrisConfiguration clientConfiguration) {
    super(clientConfiguration, Optional.empty(), new StandardModelToTigrisDBJsonSchema());
    this.stub = TigrisDBGrpc.newBlockingStub(channel);
  }

  @VisibleForTesting
  StandardTigrisClient(
      AuthorizationToken authorizationToken,
      TigrisConfiguration configuration,
      ManagedChannelBuilder<? extends ManagedChannelBuilder> managedChannelBuilder) {
    super(
        authorizationToken,
        configuration,
        managedChannelBuilder,
        new StandardModelToTigrisDBJsonSchema());
    this.stub = TigrisDBGrpc.newBlockingStub(channel);
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
    return new StandardTigrisDatabase(databaseName, stub, channel, objectMapper, modelToJsonSchema);
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
                databaseInfo.getDb(), stub, channel, objectMapper, modelToJsonSchema));
      }
      return dbs;
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisException(LIST_DBS_FAILED, statusRuntimeException);
    }
  }

  @Override
  public TigrisDatabase createDatabaseIfNotExists(String databaseName) throws TigrisException {
    try {
      stub.createDatabase(toCreateDatabaseRequest(databaseName, DatabaseOptions.DEFAULT_INSTANCE));
      log.info("database created: {}", databaseName);
      return new StandardTigrisDatabase(
          databaseName, stub, channel, objectMapper, modelToJsonSchema);
    } catch (StatusRuntimeException statusRuntimeException) {
      // ignore the error if the database is already exists
      if (statusRuntimeException.getStatus().getCode() != Status.ALREADY_EXISTS.getCode()) {
        throw new TigrisException(CREATE_DB_FAILED, statusRuntimeException);
      }
      log.info("database already exists: {}", databaseName);
      return new StandardTigrisDatabase(
          databaseName, stub, channel, objectMapper, modelToJsonSchema);
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
      throw new TigrisException(DROP_DB_FAILED, statusRuntimeException);
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