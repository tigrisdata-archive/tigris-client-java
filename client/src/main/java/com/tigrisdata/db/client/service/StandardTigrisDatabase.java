package com.tigrisdata.db.client.service;

import com.google.protobuf.ByteString;
import com.tigrisdata.db.api.v1.grpc.TigrisDBGrpc;
import com.tigrisdata.db.api.v1.grpc.User;
import com.tigrisdata.db.client.error.TigrisDBException;
import com.tigrisdata.db.client.model.AlterCollectionResponse;
import com.tigrisdata.db.client.model.CollectionOptions;
import com.tigrisdata.db.client.model.CreateCollectionResponse;
import com.tigrisdata.db.client.model.DropCollectionResponse;
import com.tigrisdata.db.client.model.TigrisCollectionType;
import com.tigrisdata.db.client.model.TigrisDBResponse;
import com.tigrisdata.db.client.model.TigrisDBSchema;
import com.tigrisdata.db.client.model.TransactionOptions;
import com.tigrisdata.db.client.model.TruncateCollectionResponse;
import io.grpc.ManagedChannel;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StandardTigrisDatabase implements TigrisDatabase {

  private final String dbName;
  private final TigrisDBGrpc.TigrisDBBlockingStub stub;
  private final ManagedChannel managedChannel;

  public StandardTigrisDatabase(
      String dbName, TigrisDBGrpc.TigrisDBBlockingStub stub, ManagedChannel managedChannel) {
    this.dbName = dbName;
    this.stub = stub;
    this.managedChannel = managedChannel;
  }

  @Override
  public List<String> listCollections() throws TigrisDBException {
    User.ListCollectionsRequest listCollectionsRequest =
        User.ListCollectionsRequest.newBuilder().setDb(dbName).build();
    User.ListCollectionsResponse listCollectionsResponse =
        stub.listCollections(listCollectionsRequest);
    return new ArrayList<>(listCollectionsResponse.getCollectionsList());
  }

  @Override
  public CreateCollectionResponse createCollection(
      String collectionName, TigrisDBSchema schema, CollectionOptions collectionOptions)
      throws TigrisDBException {
    User.CreateCollectionRequest createCollectionRequest =
        User.CreateCollectionRequest.newBuilder()
            .setDb(dbName)
            .setCollection(collectionName)
            .setSchema(ByteString.copyFrom(schema.toString(), StandardCharsets.UTF_8))
            .build();
    return new CreateCollectionResponse(
        new TigrisDBResponse(stub.createCollection(createCollectionRequest).getMsg()));
  }

  @Override
  public AlterCollectionResponse alterCollection(
      String collectionName, TigrisDBSchema schema, CollectionOptions collectionOptions)
      throws TigrisDBException {
    User.AlterCollectionRequest alterCollectionRequest =
        User.AlterCollectionRequest.newBuilder()
            .setDb(dbName)
            .setCollection(collectionName)
            .setSchema(ByteString.copyFrom(schema.toString(), StandardCharsets.UTF_8))
            .build();
    return new AlterCollectionResponse(
        new TigrisDBResponse(stub.alterCollection(alterCollectionRequest).getMsg()));
  }

  @Override
  public TruncateCollectionResponse truncateCollection(String collectionName)
      throws TigrisDBException {
    User.TruncateCollectionRequest truncateCollectionRequest =
        User.TruncateCollectionRequest.newBuilder()
            .setDb(dbName)
            .setCollection(collectionName)
            .build();
    return new TruncateCollectionResponse(
        new TigrisDBResponse(stub.truncateCollection(truncateCollectionRequest).getMsg()));
  }

  @Override
  public DropCollectionResponse dropCollection(String collectionName) throws TigrisDBException {
    User.DropCollectionRequest dropCollectionRequest =
        User.DropCollectionRequest.newBuilder().setDb(dbName).setCollection(collectionName).build();
    return new DropCollectionResponse(
        new TigrisDBResponse(stub.dropCollection(dropCollectionRequest).getMsg()));
  }

  @Override
  public <C extends TigrisCollectionType> TigrisCollection<C> getCollection(
      Class<C> collectionTypeClass) {
    return new StandardTigrisCollection<>(dbName, collectionTypeClass, stub);
  }

  @Override
  public TransactionSession beginTransaction(TransactionOptions transactionOptions)
      throws TigrisDBException {
    User.BeginTransactionRequest beginTransactionRequest =
        User.BeginTransactionRequest.newBuilder()
            .setDb(dbName)
            .setOptions(User.TransactionOptions.newBuilder().build())
            .build();
    User.BeginTransactionResponse beginTransactionResponse =
        stub.beginTransaction(beginTransactionRequest);
    User.TransactionCtx transactionCtx = beginTransactionResponse.getTxCtx();
    return new StandardTransactionSession(dbName, transactionCtx, managedChannel);
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
