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
import static com.tigrisdata.db.client.Messages.DB_ALREADY_EXISTS;
import static com.tigrisdata.db.client.Messages.DROP_DB_FAILED;
import static com.tigrisdata.db.client.Messages.LIST_DBS_FAILED;
import static com.tigrisdata.db.client.TypeConverter.toCreateDatabaseRequest;
import static com.tigrisdata.db.client.TypeConverter.toDropDatabaseRequest;
import com.tigrisdata.db.client.auth.AuthorizationToken;
import com.tigrisdata.db.client.config.TigrisDBConfiguration;
import com.tigrisdata.db.client.error.TigrisDBException;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import java.util.ArrayList;
import java.util.List;

/** Client for TigrisDB */
public class StandardTigrisDBClient extends AbstractTigrisDBClient implements TigrisDBClient {

  private final TigrisDBGrpc.TigrisDBBlockingStub stub;

  private StandardTigrisDBClient(
      TigrisDBConfiguration clientConfiguration, AuthorizationToken authorizationToken) {
    super(clientConfiguration, authorizationToken);
    this.stub = TigrisDBGrpc.newBlockingStub(channel);
  }

  @VisibleForTesting
  StandardTigrisDBClient(
      AuthorizationToken authorizationToken,
      TigrisDBConfiguration configuration,
      ManagedChannelBuilder<? extends ManagedChannelBuilder> managedChannelBuilder) {
    super(authorizationToken, configuration, managedChannelBuilder);
    this.stub = TigrisDBGrpc.newBlockingStub(channel);
  }

  /**
   * Creates a new instance of @{@link StandardTigrisDBClient} with the given inputs
   *
   * @param authorizationToken authorization token
   * @param tigrisDBConfiguration configuration
   * @return a new instance of @{@link StandardTigrisDBClient}
   */
  public static StandardTigrisDBClient getInstance(
      AuthorizationToken authorizationToken, TigrisDBConfiguration tigrisDBConfiguration) {
    return new StandardTigrisDBClient(tigrisDBConfiguration, authorizationToken);
  }

  @Override
  public TigrisDatabase getDatabase(String databaseName) {
    return new StandardTigrisDatabase(databaseName, stub, channel, objectMapper);
  }

  @Override
  public List<TigrisDatabase> listDatabases(DatabaseOptions listDatabaseOptions)
      throws TigrisDBException {
    try {
      Api.ListDatabasesRequest listDatabasesRequest = Api.ListDatabasesRequest.newBuilder().build();
      Api.ListDatabasesResponse listDatabasesResponse = stub.listDatabases(listDatabasesRequest);
      List<TigrisDatabase> dbs = new ArrayList<>();
      for (Api.DatabaseInfo databaseInfo : listDatabasesResponse.getDatabasesList()) {
        dbs.add(new StandardTigrisDatabase(databaseInfo.getName(), stub, channel, objectMapper));
      }
      return dbs;
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisDBException(LIST_DBS_FAILED, statusRuntimeException);
    }
  }

  @Override
  public TigrisDBResponse createDatabaseIfNotExists(String databaseName) throws TigrisDBException {
    Api.CreateDatabaseResponse createDatabaseResponse = null;
    try {
      createDatabaseResponse =
          stub.createDatabase(
              toCreateDatabaseRequest(databaseName, DatabaseOptions.DEFAULT_INSTANCE));
      return new TigrisDBResponse(createDatabaseResponse.getMsg());
    } catch (StatusRuntimeException statusRuntimeException) {
      // ignore the error if the database is already exists
      if (statusRuntimeException.getStatus().getCode() != Status.ALREADY_EXISTS.getCode()) {
        throw new TigrisDBException(CREATE_DB_FAILED, statusRuntimeException);
      }
      return new TigrisDBResponse(DB_ALREADY_EXISTS);
    }
  }

  @Override
  public TigrisDBResponse dropDatabase(String databaseName) throws TigrisDBException {
    try {
      Api.DropDatabaseResponse dropDatabaseResponse =
          stub.dropDatabase(toDropDatabaseRequest(databaseName, DatabaseOptions.DEFAULT_INSTANCE));
      return new TigrisDBResponse(dropDatabaseResponse.getMsg());
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisDBException(DROP_DB_FAILED, statusRuntimeException);
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
