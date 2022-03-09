package com.tigrisdata.db.client.grpc;

import com.google.protobuf.ByteString;
import com.tigrisdata.db.api.v1.grpc.TigrisDBGrpc;
import com.tigrisdata.db.api.v1.grpc.User;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TestUserService extends TigrisDBGrpc.TigrisDBImplBase {

  private List<String> dbs;
  private Map<String, List<String>> dbToCollectionsMap;

  private String txId;
  private String txOrigin;

  public TestUserService() {
    this.reset();
  }

  public void reset() {
    // default dbs
    this.dbs =
        new ArrayList<String>() {
          {
            add("db1");
            add("db2");
            add("db3");
          }
        };

    this.dbToCollectionsMap = new HashMap<>();
    for (String db : dbs) {
      List<String> collections = new ArrayList<>();
      for (int i = 0; i < 5; i++) {
        collections.add(db + "_c" + i);
      }
      dbToCollectionsMap.put(db, collections);
    }
  }

  @Override
  public void beginTransaction(
      User.BeginTransactionRequest request,
      StreamObserver<User.BeginTransactionResponse> responseObserver) {
    this.txId = UUID.randomUUID().toString();
    this.txOrigin = txId + "_origin";
    responseObserver.onNext(
        User.BeginTransactionResponse.newBuilder()
            .setTxCtx(User.TransactionCtx.newBuilder().setId(txId).setOrigin(txOrigin).build())
            .build());
    responseObserver.onCompleted();
  }

  @Override
  public void commitTransaction(
      User.CommitTransactionRequest request,
      StreamObserver<User.CommitTransactionResponse> responseObserver) {
    if (!request.getTxCtx().getId().equals(txId)
        || !request.getTxCtx().getOrigin().equals(txOrigin)) {
      responseObserver.onError(new IllegalArgumentException("Unexpected" + " " + "transaction"));
      responseObserver.onCompleted();
    }
    resetTx();
    responseObserver.onNext(User.CommitTransactionResponse.newBuilder().build());
    responseObserver.onCompleted();
  }

  @Override
  public void rollbackTransaction(
      User.RollbackTransactionRequest request,
      StreamObserver<User.RollbackTransactionResponse> responseObserver) {
    if (!request.getTxCtx().getId().equals(txId)
        || !request.getTxCtx().getOrigin().equals(txOrigin)) {
      responseObserver.onError(new IllegalArgumentException("Unexpected" + " " + "transaction"));
      responseObserver.onCompleted();
    }
    resetTx();
    responseObserver.onNext(User.RollbackTransactionResponse.newBuilder().build());
    responseObserver.onCompleted();
  }

  @Override
  public void insert(
      User.InsertRequest request, StreamObserver<User.InsertResponse> responseObserver) {
    responseObserver.onNext(User.InsertResponse.newBuilder().build());
    responseObserver.onCompleted();
  }

  @Override
  public void replace(
      User.ReplaceRequest request, StreamObserver<User.ReplaceResponse> responseObserver) {
    responseObserver.onNext(User.ReplaceResponse.newBuilder().build());
    responseObserver.onCompleted();
  }

  @Override
  public void delete(
      User.DeleteRequest request, StreamObserver<User.DeleteResponse> responseObserver) {
    responseObserver.onNext(User.DeleteResponse.newBuilder().build());
    responseObserver.onCompleted();
  }

  @Override
  public void update(
      User.UpdateRequest request, StreamObserver<User.UpdateResponse> responseObserver) {
    super.update(request, responseObserver);
  }

  @Override
  public void read(User.ReadRequest request, StreamObserver<User.ReadResponse> responseObserver) {

    final String responseString = "{ \"msg\": \"" + request.getFilter().toStringUtf8() + "\"}";
    responseObserver.onNext(
        User.ReadResponse.newBuilder().setDoc(ByteString.copyFromUtf8(responseString)).build());
    responseObserver.onCompleted();
  }

  @Override
  public void createCollection(
      User.CreateCollectionRequest request,
      StreamObserver<User.CreateCollectionResponse> responseObserver) {
    dbToCollectionsMap.get(request.getDb()).add(request.getCollection());
    responseObserver.onNext(
        User.CreateCollectionResponse.newBuilder()
            .setMsg(request.getCollection() + " created")
            .build());
    responseObserver.onCompleted();
  }

  @Override
  public void alterCollection(
      User.AlterCollectionRequest request,
      StreamObserver<User.AlterCollectionResponse> responseObserver) {
    responseObserver.onNext(
        User.AlterCollectionResponse.newBuilder()
            .setMsg(request.getCollection() + " altered")
            .build());
    responseObserver.onCompleted();
  }

  @Override
  public void dropCollection(
      User.DropCollectionRequest request,
      StreamObserver<User.DropCollectionResponse> responseObserver) {
    dbToCollectionsMap.get(request.getDb()).remove(request.getCollection());
    responseObserver.onNext(
        User.DropCollectionResponse.newBuilder()
            .setMsg(request.getCollection() + " dropped")
            .build());
    responseObserver.onCompleted();
  }

  @Override
  public void truncateCollection(
      User.TruncateCollectionRequest request,
      StreamObserver<User.TruncateCollectionResponse> responseObserver) {
    responseObserver.onNext(
        User.TruncateCollectionResponse.newBuilder()
            .setMsg(request.getCollection() + " truncated")
            .build());
    responseObserver.onCompleted();
  }

  @Override
  public void listDatabases(
      User.ListDatabasesRequest request,
      StreamObserver<User.ListDatabasesResponse> responseObserver) {
    User.ListDatabasesResponse listDatabasesResponse =
        User.ListDatabasesResponse.newBuilder().addAllDbs(dbs).build();
    responseObserver.onNext(listDatabasesResponse);
    responseObserver.onCompleted();
  }

  @Override
  public void listCollections(
      User.ListCollectionsRequest request,
      StreamObserver<User.ListCollectionsResponse> responseObserver) {
    User.ListCollectionsResponse.Builder builder = User.ListCollectionsResponse.newBuilder();
    for (String collectionName : dbToCollectionsMap.get(request.getDb())) {
      builder.addCollections(collectionName);
    }
    responseObserver.onNext(builder.build());
    responseObserver.onCompleted();
  }

  @Override
  public void createDatabase(
      User.CreateDatabaseRequest request,
      StreamObserver<User.CreateDatabaseResponse> responseObserver) {
    this.dbs.add(request.getDb());
    responseObserver.onNext(
        User.CreateDatabaseResponse.newBuilder().setMsg(request.getDb() + " created").build());
    responseObserver.onCompleted();
  }

  @Override
  public void dropDatabase(
      User.DropDatabaseRequest request,
      StreamObserver<User.DropDatabaseResponse> responseObserver) {
    this.dbs.remove(request.getDb());
    responseObserver.onNext(
        User.DropDatabaseResponse.newBuilder().setMsg(request.getDb() + " dropped").build());
    responseObserver.onCompleted();
  }

  private void resetTx() {
    this.txOrigin = "";
    this.txId = "";
  }
}
