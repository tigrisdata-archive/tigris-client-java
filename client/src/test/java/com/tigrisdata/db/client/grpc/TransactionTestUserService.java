package com.tigrisdata.db.client.grpc;

import com.tigrisdata.db.api.v1.grpc.Api;
import io.grpc.stub.StreamObserver;

import java.util.UUID;

public class TransactionTestUserService extends TestUserService {

  private String txId;
  private String txOrigin;

  @Override
  public void beginTransaction(
      Api.BeginTransactionRequest request,
      StreamObserver<Api.BeginTransactionResponse> responseObserver) {
    txId = UUID.randomUUID().toString();
    txOrigin = txId + "_origin";
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
    if (!isValidTransactionState()) {
      responseObserver.onError(new IllegalStateException("Transaction is not " + "active"));
      return;
    }
    resetTx();
    txId = UUID.randomUUID().toString();
    txOrigin = txId + "_origin";
    responseObserver.onNext(Api.CommitTransactionResponse.newBuilder().build());
    responseObserver.onCompleted();
  }

  @Override
  public void rollbackTransaction(
      Api.RollbackTransactionRequest request,
      StreamObserver<Api.RollbackTransactionResponse> responseObserver) {
    if (!isValidTransactionState()) {
      responseObserver.onError(new IllegalStateException("Transaction is not " + "active"));
      return;
    }
    resetTx();
    responseObserver.onNext(Api.RollbackTransactionResponse.newBuilder().build());
    responseObserver.onCompleted();
  }

  @Override
  public void insert(
      Api.InsertRequest request, StreamObserver<Api.InsertResponse> responseObserver) {
    if (!isValidTransactionState()) {
      responseObserver.onError(new IllegalStateException("Transaction is not " + "active"));
    }
    super.insert(request, responseObserver);
  }

  @Override
  public void delete(
      Api.DeleteRequest request, StreamObserver<Api.DeleteResponse> responseObserver) {
    if (!isValidTransactionState()) {
      responseObserver.onError(new IllegalStateException("Transaction is not " + "active"));
    }
    super.delete(request, responseObserver);
  }

  @Override
  public void update(
      Api.UpdateRequest request, StreamObserver<Api.UpdateResponse> responseObserver) {
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
