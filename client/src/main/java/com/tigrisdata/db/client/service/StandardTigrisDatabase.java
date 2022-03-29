package com.tigrisdata.db.client.service;

import com.google.protobuf.ByteString;
import com.tigrisdata.db.api.v1.grpc.Api;
import com.tigrisdata.db.api.v1.grpc.TigrisDBGrpc;
import com.tigrisdata.db.client.error.TigrisDBException;
import com.tigrisdata.db.client.model.AlterCollectionResponse;
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
  public AlterCollectionResponse alterCollection(
      String collectionName, TigrisDBSchema schema, CollectionOptions collectionOptions)
      throws TigrisDBException {
    try {
      Api.AlterCollectionRequest alterCollectionRequest =
          Api.AlterCollectionRequest.newBuilder()
              .setDb(dbName)
              .setCollection(collectionName)
              .setSchema(ByteString.copyFrom(schema.getSchemaContent(), StandardCharsets.UTF_8))
              .build();
      return new AlterCollectionResponse(
          new TigrisDBResponse(stub.alterCollection(alterCollectionRequest).getMsg()));
    } catch (IOException ioException) {
      throw new TigrisDBException("Failed to read schema content", ioException);
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisDBException("Failed to alter collection", statusRuntimeException);
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
