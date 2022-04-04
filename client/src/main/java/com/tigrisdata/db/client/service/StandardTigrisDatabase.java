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

import com.google.protobuf.ByteString;
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
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StandardTigrisDatabase implements TigrisDatabase {

  private final String dbName;
  private final TigrisDBGrpc.TigrisDBBlockingStub stub;
  private final ManagedChannel managedChannel;

  StandardTigrisDatabase(
      String dbName, TigrisDBGrpc.TigrisDBBlockingStub stub, ManagedChannel managedChannel) {
    this.dbName = dbName;
    this.stub = stub;
    this.managedChannel = managedChannel;
  }

  @Override
  public List<String> listCollections() throws TigrisDBException {
    try {
      Api.ListCollectionsRequest listCollectionsRequest =
          Api.ListCollectionsRequest.newBuilder().setDb(dbName).build();
      Api.ListCollectionsResponse listCollectionsResponse =
          stub.listCollections(listCollectionsRequest);
      return new ArrayList<>(listCollectionsResponse.getCollectionsList());
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisDBException("Failed to list collections", statusRuntimeException);
    }
  }

  @Override
  public CreateCollectionResponse createCollection(
      String collectionName, TigrisDBSchema schema, CollectionOptions collectionOptions)
      throws TigrisDBException {
    try {
      Api.CreateCollectionRequest createCollectionRequest =
          Api.CreateCollectionRequest.newBuilder()
              .setDb(dbName)
              .setCollection(collectionName)
              .setSchema(ByteString.copyFrom(schema.getSchemaContent(), StandardCharsets.UTF_8))
              .build();
      return new CreateCollectionResponse(
          new TigrisDBResponse(stub.createCollection(createCollectionRequest).getMsg()));
    } catch (IOException ioException) {
      throw new TigrisDBException("Failed to read schema content", ioException);
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisDBException("Failed to create collection", statusRuntimeException);
    }
  }

  @Override
  public DropCollectionResponse dropCollection(String collectionName) throws TigrisDBException {
    try {
      Api.DropCollectionRequest dropCollectionRequest =
          Api.DropCollectionRequest.newBuilder()
              .setDb(dbName)
              .setCollection(collectionName)
              .build();
      return new DropCollectionResponse(
          new TigrisDBResponse(stub.dropCollection(dropCollectionRequest).getMsg()));
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisDBException("Failed to drop collection", statusRuntimeException);
    }
  }

  @Override
  public <C extends TigrisCollectionType> TigrisCollection<C> getCollection(
      Class<C> collectionTypeClass) {
    return new StandardTigrisCollection<>(dbName, collectionTypeClass, stub);
  }

  @Override
  public TransactionSession beginTransaction(TransactionOptions transactionOptions)
      throws TigrisDBException {
    try {
      Api.BeginTransactionRequest beginTransactionRequest =
          Api.BeginTransactionRequest.newBuilder()
              .setDb(dbName)
              .setOptions(Api.TransactionOptions.newBuilder().build())
              .build();
      Api.BeginTransactionResponse beginTransactionResponse =
          stub.beginTransaction(beginTransactionRequest);
      Api.TransactionCtx transactionCtx = beginTransactionResponse.getTxCtx();
      return new StandardTransactionSession(dbName, transactionCtx, managedChannel);
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisDBException("Failed to begin transaction", statusRuntimeException);
    }
  }

  @Override
  public String name() {
    return dbName;
  }

  @Override
  public String toString() {
    return "StandardTigrisDatabase{" + "dbName='" + dbName + '\'' + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    StandardTigrisDatabase that = (StandardTigrisDatabase) o;

    return Objects.equals(dbName, that.dbName);
  }

  @Override
  public int hashCode() {
    return dbName != null ? dbName.hashCode() : 0;
  }
}
