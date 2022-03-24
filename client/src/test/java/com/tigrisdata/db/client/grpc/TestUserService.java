package com.tigrisdata.db.client.grpc;

import com.google.protobuf.ByteString;
import com.tigrisdata.db.api.v1.grpc.Api;
import com.tigrisdata.db.api.v1.grpc.TigrisDBGrpc;
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
      Api.BeginTransactionRequest request,
      StreamObserver<Api.BeginTransactionResponse> responseObserver) {
    this.txId = UUID.randomUUID().toString();
    this.txOrigin = txId + "_origin";
    responseObserver.onNext(
        Api.BeginTransactionResponse.newBuilder()
            .setTxCtx(Api.TransactionCtx.newBuilder().setId(txId).setOrigin(txOrigin).build())
            .build());
    responseObserver.onCompleted();
  }

  @Override
  public void commitTransaction(
      Api.CommitTransactionRequest request,
      StreamObserver<Api.CommitTransactionResponse> responseObserver) {
    if (!request.getTxCtx().getId().equals(txId)
        || !request.getTxCtx().getOrigin().equals(txOrigin)) {
      responseObserver.onError(new IllegalArgumentException("Unexpected transaction"));
      responseObserver.onCompleted();
    }
    resetTx();
    responseObserver.onNext(Api.CommitTransactionResponse.newBuilder().build());
    responseObserver.onCompleted();
  }

  @Override
  public void rollbackTransaction(
      Api.RollbackTransactionRequest request,
      StreamObserver<Api.RollbackTransactionResponse> responseObserver) {
    if (!request.getTxCtx().getId().equals(txId)
        || !request.getTxCtx().getOrigin().equals(txOrigin)) {
      responseObserver.onError(new IllegalArgumentException("Unexpected transaction"));
      responseObserver.onCompleted();
    }
    resetTx();
    responseObserver.onNext(Api.RollbackTransactionResponse.newBuilder().build());
    responseObserver.onCompleted();
  }

  @Override
  public void insert(
      Api.InsertRequest request, StreamObserver<Api.InsertResponse> responseObserver) {
    responseObserver.onNext(Api.InsertResponse.newBuilder().build());
    responseObserver.onCompleted();
  }

  @Override
  public void delete(
      Api.DeleteRequest request, StreamObserver<Api.DeleteResponse> responseObserver) {
    responseObserver.onNext(Api.DeleteResponse.newBuilder().build());
    responseObserver.onCompleted();
  }

  @Override
  public void update(
      Api.UpdateRequest request, StreamObserver<Api.UpdateResponse> responseObserver) {
    responseObserver.onNext(Api.UpdateResponse.newBuilder().setRc(123).build());
    responseObserver.onCompleted();
  }

  @Override
  public void read(Api.ReadRequest request, StreamObserver<Api.ReadResponse> responseObserver) {

    final String responseString = "{ \"msg\": \"" + request.getFilter().toStringUtf8() + "\"}";
    responseObserver.onNext(
        Api.ReadResponse.newBuilder().setDoc(ByteString.copyFromUtf8(responseString)).build());
    responseObserver.onCompleted();
  }

  @Override
  public void createCollection(
      Api.CreateCollectionRequest request,
      StreamObserver<Api.CreateCollectionResponse> responseObserver) {
    dbToCollectionsMap.get(request.getDb()).add(request.getCollection());
    responseObserver.onNext(
        Api.CreateCollectionResponse.newBuilder()
            .setMsg(request.getCollection() + " created")
            .build());
    responseObserver.onCompleted();
  }

  @Override
  public void alterCollection(
      Api.AlterCollectionRequest request,
      StreamObserver<Api.AlterCollectionResponse> responseObserver) {
    responseObserver.onNext(
        Api.AlterCollectionResponse.newBuilder()
            .setMsg(request.getCollection() + " altered")
            .build());
    responseObserver.onCompleted();
  }

  @Override
  public void dropCollection(
      Api.DropCollectionRequest request,
      StreamObserver<Api.DropCollectionResponse> responseObserver) {
    dbToCollectionsMap.get(request.getDb()).remove(request.getCollection());
    responseObserver.onNext(
        Api.DropCollectionResponse.newBuilder()
            .setMsg(request.getCollection() + " dropped")
            .build());
    responseObserver.onCompleted();
  }

  @Override
  public void truncateCollection(
      Api.TruncateCollectionRequest request,
      StreamObserver<Api.TruncateCollectionResponse> responseObserver) {
    responseObserver.onNext(
        Api.TruncateCollectionResponse.newBuilder()
            .setMsg(request.getCollection() + " truncated")
            .build());
    responseObserver.onCompleted();
  }

  @Override
  public void listDatabases(
      Api.ListDatabasesRequest request,
      StreamObserver<Api.ListDatabasesResponse> responseObserver) {
    Api.ListDatabasesResponse listDatabasesResponse =
        Api.ListDatabasesResponse.newBuilder().addAllDbs(dbs).build();
    responseObserver.onNext(listDatabasesResponse);
    responseObserver.onCompleted();
  }

  @Override
  public void listCollections(
      Api.ListCollectionsRequest request,
      StreamObserver<Api.ListCollectionsResponse> responseObserver) {
    Api.ListCollectionsResponse.Builder builder = Api.ListCollectionsResponse.newBuilder();
    for (String collectionName : dbToCollectionsMap.get(request.getDb())) {
      builder.addCollections(collectionName);
    }
    responseObserver.onNext(builder.build());
    responseObserver.onCompleted();
  }

  @Override
  public void createDatabase(
      Api.CreateDatabaseRequest request,
      StreamObserver<Api.CreateDatabaseResponse> responseObserver) {
    this.dbs.add(request.getDb());
    responseObserver.onNext(
        Api.CreateDatabaseResponse.newBuilder().setMsg(request.getDb() + " created").build());
    responseObserver.onCompleted();
  }

  @Override
  public void dropDatabase(
      Api.DropDatabaseRequest request, StreamObserver<Api.DropDatabaseResponse> responseObserver) {
    this.dbs.remove(request.getDb());
    responseObserver.onNext(
        Api.DropDatabaseResponse.newBuilder().setMsg(request.getDb() + " dropped").build());
    responseObserver.onCompleted();
  }

  private void resetTx() {
    this.txOrigin = "";
    this.txId = "";
  }
}
