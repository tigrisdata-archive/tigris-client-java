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
import com.tigrisdata.db.api.v1.grpc.Api;
import com.tigrisdata.db.api.v1.grpc.TigrisDBGrpc;
import static com.tigrisdata.db.client.Messages.BEGIN_TRANSACTION_FAILED;
import static com.tigrisdata.db.client.Messages.CREATE_OR_UPDATE_COLLECTION_FAILED;
import static com.tigrisdata.db.client.Messages.DROP_COLLECTION_FAILED;
import static com.tigrisdata.db.client.Messages.LIST_COLLECTION_FAILED;
import com.tigrisdata.db.client.error.TigrisDBException;
import com.tigrisdata.db.type.TigrisCollectionType;
import com.tigrisdata.tools.schema.core.DefaultModelToTigrisDBJsonSchema;
import com.tigrisdata.tools.schema.core.ModelToJsonSchema;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/** TigrisDB Database implementation */
public class StandardTigrisDatabase implements TigrisDatabase {

  private final String dbName;
  private final TigrisDBGrpc.TigrisDBBlockingStub stub;
  private final ManagedChannel managedChannel;
  private final ObjectMapper objectMapper;

  StandardTigrisDatabase(
      String dbName,
      TigrisDBGrpc.TigrisDBBlockingStub stub,
      ManagedChannel managedChannel,
      ObjectMapper objectMapper) {
    this.dbName = dbName;
    this.stub = stub;
    this.managedChannel = managedChannel;
    this.objectMapper = objectMapper;
  }

  @Override
  public List<CollectionInfo> listCollections() throws TigrisDBException {
    try {
      Api.ListCollectionsRequest listCollectionsRequest =
          Api.ListCollectionsRequest.newBuilder().setDb(dbName).build();
      Api.ListCollectionsResponse listCollectionsResponse =
          stub.listCollections(listCollectionsRequest);
      return listCollectionsResponse.getCollectionsList().stream()
          .map(TypeConverter::toCollectionInfo)
          .collect(Collectors.toList());
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisDBException(LIST_COLLECTION_FAILED, statusRuntimeException);
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
      throw new TigrisDBException(DROP_COLLECTION_FAILED, statusRuntimeException);
    }
  }

  @Override
  public <C extends TigrisCollectionType> TigrisCollection<C> getCollection(
      Class<C> collectionTypeClass) {
    return new StandardTigrisCollection<>(dbName, collectionTypeClass, stub, objectMapper);
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
      return new StandardTransactionSession(dbName, transactionCtx, managedChannel, objectMapper);
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisDBException(BEGIN_TRANSACTION_FAILED, statusRuntimeException);
    }
  }

  @Override
  public CreateOrUpdateCollectionsResponse createOrUpdateCollections(
      Class<? extends TigrisCollectionType>... collectionModelTypes) throws TigrisDBException {
    TransactionSession transactionSession = null;
    ModelToJsonSchema modelToJsonSchema = new DefaultModelToTigrisDBJsonSchema();
    try {
      transactionSession = beginTransaction(TransactionOptions.DEFAULT_INSTANCE);
      for (Class<? extends TigrisCollectionType> collectionModelType : collectionModelTypes) {

        TigrisDBSchema schema =
            new TigrisDBJSONSchema(modelToJsonSchema.toJsonSchema(collectionModelType).toString());
        transactionSession.createOrUpdateCollections(schema, CollectionOptions.DEFAULT_INSTANCE);
      }
      transactionSession.commit();
      return new CreateOrUpdateCollectionsResponse(
          new TigrisDBResponse("Collections created successfully"));
    } catch (Exception exception) {
      if (transactionSession != null) {
        transactionSession.rollback();
      }
      throw new TigrisDBException(CREATE_OR_UPDATE_COLLECTION_FAILED, exception);
    }
  }

  @Override
  public CreateOrUpdateCollectionsResponse createOrUpdateCollections(
      String[] packagesToScan, Optional<Predicate<Class<? extends TigrisCollectionType>>> filter)
      throws TigrisDBException {
    return this.createOrUpdateCollections(
        Utilities.scanTigrisDBCollectionModels(packagesToScan, filter));
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
