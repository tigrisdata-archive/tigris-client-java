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
package com.tigrisdata.db.client.grpc;

import com.tigrisdata.db.api.v1.grpc.Api;
import io.grpc.stub.StreamObserver;

import java.util.UUID;

public class TransactionTestTigrisService extends TestTigrisService {

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
    if (isValidTransactionState()) {
      responseObserver.onError(new IllegalStateException("Transaction is not active"));
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
    if (isValidTransactionState()) {
      responseObserver.onError(new IllegalStateException("Transaction is not active"));
      return;
    }
    resetTx();
    responseObserver.onNext(Api.RollbackTransactionResponse.newBuilder().build());
    responseObserver.onCompleted();
  }

  @Override
  public void insert(
      Api.InsertRequest request, StreamObserver<Api.InsertResponse> responseObserver) {
    if (isValidTransactionState()) {
      responseObserver.onError(new IllegalStateException("Transaction is not active"));
    }
    super.insert(request, responseObserver);
  }

  @Override
  public void delete(
      Api.DeleteRequest request, StreamObserver<Api.DeleteResponse> responseObserver) {
    if (isValidTransactionState()) {
      responseObserver.onError(new IllegalStateException("Transaction is not active"));
    }
    super.delete(request, responseObserver);
  }

  @Override
  public void update(
      Api.UpdateRequest request, StreamObserver<Api.UpdateResponse> responseObserver) {
    if (isValidTransactionState()) {
      responseObserver.onError(new IllegalStateException("Transaction is not active"));
    }
    super.update(request, responseObserver);
  }

  @Override
  public void replace(
      Api.ReplaceRequest request, StreamObserver<Api.ReplaceResponse> responseObserver) {
    if (isValidTransactionState()) {
      responseObserver.onError(new IllegalStateException("Transaction is not active"));
    }
    super.replace(request, responseObserver);
  }

  @Override
  public void read(Api.ReadRequest request, StreamObserver<Api.ReadResponse> responseObserver) {
    if (isValidTransactionState()) {
      responseObserver.onError(new IllegalStateException("Transaction is not active"));
    }
    super.read(request, responseObserver);
  }

  @Override
  public void createOrUpdateCollection(
      Api.CreateOrUpdateCollectionRequest request,
      StreamObserver<Api.CreateOrUpdateCollectionResponse> responseObserver) {
    if (isValidTransactionState()) {
      responseObserver.onError(new IllegalStateException("Transaction is not active"));
    }
    super.createOrUpdateCollection(request, responseObserver);
  }

  @Override
  public void dropCollection(
      Api.DropCollectionRequest request,
      StreamObserver<Api.DropCollectionResponse> responseObserver) {
    if (isValidTransactionState()) {
      responseObserver.onError(new IllegalStateException("Transaction is not active"));
    }
    super.dropCollection(request, responseObserver);
  }

  @Override
  public void listProjects(
      Api.ListProjectsRequest request, StreamObserver<Api.ListProjectsResponse> responseObserver) {
    if (isValidTransactionState()) {
      responseObserver.onError(new IllegalStateException("Transaction is not active"));
    }
    super.listProjects(request, responseObserver);
  }

  @Override
  public void listCollections(
      Api.ListCollectionsRequest request,
      StreamObserver<Api.ListCollectionsResponse> responseObserver) {
    if (isValidTransactionState()) {
      responseObserver.onError(new IllegalStateException("Transaction is not active"));
    }
    super.listCollections(request, responseObserver);
  }

  @Override
  public void createProject(
      Api.CreateProjectRequest request,
      StreamObserver<Api.CreateProjectResponse> responseObserver) {
    if (isValidTransactionState()) {
      responseObserver.onError(new IllegalStateException("Transaction is not active"));
    }
    super.createProject(request, responseObserver);
  }

  @Override
  public void deleteProject(
      Api.DeleteProjectRequest request,
      StreamObserver<Api.DeleteProjectResponse> responseObserver) {
    if (isValidTransactionState()) {
      responseObserver.onError(new IllegalStateException("Transaction is not active"));
    }
    super.deleteProject(request, responseObserver);
  }

  @Override
  public void reset() {
    super.reset();
    resetTx();
  }

  private void resetTx() {
    txId = "";
    txOrigin = "";
  }

  private boolean isValidTransactionState() {
    String incomingTxId = ContextSettingServerInterceptor.TX_ID_CONTEXT_KEY.get();
    String incomingTxOrigin = ContextSettingServerInterceptor.TX_ORIGIN_CONTEXT_KEY.get();
    String incomingCookie = ContextSettingServerInterceptor.COOKIE_CONTEXT_KEY.get();
    return !txId.equals(incomingTxId)
        || !txOrigin.equals(incomingTxOrigin)
        || !incomingCookie.contains("Tigris-Tx-Id=");
  }
}
