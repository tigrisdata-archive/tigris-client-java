package com.tigrisdata.db.client.grpc;

import com.tigrisdata.db.api.v1.grpc.User;
import io.grpc.stub.StreamObserver;

import java.util.UUID;

public class TransactionTestUserService extends TestUserService {

  private String txId;
  private String txOrigin;

  @Override
  public void beginTransaction(
      User.BeginTransactionRequest request,
      StreamObserver<User.BeginTransactionResponse> responseObserver) {
    txId = UUID.randomUUID().toString();
    txOrigin = txId + "_origin";
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
    if (!isValidTransactionState()) {
      responseObserver.onError(new IllegalStateException("Transaction is not " + "active"));
    }
    resetTx();
    txId = UUID.randomUUID().toString();
    txOrigin = txId + "_origin";
    responseObserver.onNext(User.CommitTransactionResponse.newBuilder().build());
    responseObserver.onCompleted();
  }

  @Override
  public void rollbackTransaction(
      User.RollbackTransactionRequest request,
      StreamObserver<User.RollbackTransactionResponse> responseObserver) {
    if (!isValidTransactionState()) {
      responseObserver.onError(new IllegalStateException("Transaction is not " + "active"));
    }
    resetTx();
    responseObserver.onNext(User.RollbackTransactionResponse.newBuilder().build());
    responseObserver.onCompleted();
  }

  @Override
  public void insert(
      User.InsertRequest request, StreamObserver<User.InsertResponse> responseObserver) {
    if (!isValidTransactionState()) {
      responseObserver.onError(new IllegalStateException("Transaction is not " + "active"));
    }
    super.insert(request, responseObserver);
  }

  @Override
  public void delete(
      User.DeleteRequest request, StreamObserver<User.DeleteResponse> responseObserver) {
    if (!isValidTransactionState()) {
      responseObserver.onError(new IllegalStateException("Transaction is not " + "active"));
    }
    super.delete(request, responseObserver);
  }

  @Override
  public void replace(
      User.ReplaceRequest request, StreamObserver<User.ReplaceResponse> responseObserver) {
    if (!isValidTransactionState()) {
      responseObserver.onError(new IllegalStateException("Transaction is not " + "active"));
    }
    super.replace(request, responseObserver);
  }

  @Override
  public void update(
      User.UpdateRequest request, StreamObserver<User.UpdateResponse> responseObserver) {
    if (!isValidTransactionState()) {
      responseObserver.onError(new IllegalStateException("Transaction is not " + "active"));
    }
    super.update(request, responseObserver);
  }

  private void resetTx() {
    txId = "";
    txOrigin = "";
  }

  private boolean isValidTransactionState() {
    String incomingTxId = ContextSettingServerInterceptor.TX_ID_CONTEXT_KEY.get();
    String incomingTxOrigin = ContextSettingServerInterceptor.TX_ORIGIN_CONTEXT_KEY.get();
    if (txId.equals(incomingTxId) && txOrigin.equals(incomingTxOrigin)) {
      return true;
    }
    return false;
  }
}
