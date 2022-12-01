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
import com.tigrisdata.db.api.v1.grpc.TigrisGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class FailingTestTigrisService extends TigrisGrpc.TigrisImplBase {

  public static final String ALLOW_BEGIN_TRANSACTION_DB_NAME = "pass-begin";
  public static final String ALLOW_ROLLBACK_TRANSACTION_DB_NAME = "pass-rollback";

  @Override
  public void beginTransaction(
      Api.BeginTransactionRequest request,
      StreamObserver<Api.BeginTransactionResponse> responseObserver) {
    if (request.getProject().contains(ALLOW_BEGIN_TRANSACTION_DB_NAME)) {
      responseObserver.onNext(
          Api.BeginTransactionResponse.newBuilder()
              .setTxCtx(Api.TransactionCtx.newBuilder().build())
              .build());
      responseObserver.onCompleted();
      return;
    }

    responseObserver.onError(
        Status.FAILED_PRECONDITION
            .withDescription("Test failure " + request.getProject())
            .asRuntimeException());
  }

  @Override
  public void commitTransaction(
      Api.CommitTransactionRequest request,
      StreamObserver<Api.CommitTransactionResponse> responseObserver) {
    responseObserver.onError(
        Status.FAILED_PRECONDITION
            .withDescription("Test failure " + request.getProject())
            .asRuntimeException());
  }

  @Override
  public void rollbackTransaction(
      Api.RollbackTransactionRequest request,
      StreamObserver<Api.RollbackTransactionResponse> responseObserver) {
    if (request.getProject().contains(ALLOW_ROLLBACK_TRANSACTION_DB_NAME)) {
      responseObserver.onNext(Api.RollbackTransactionResponse.newBuilder().build());
      responseObserver.onCompleted();
      return;
    }

    responseObserver.onError(
        Status.FAILED_PRECONDITION
            .withDescription("Test failure " + request.getProject())
            .asRuntimeException());
  }

  @Override
  public void insert(
      Api.InsertRequest request, StreamObserver<Api.InsertResponse> responseObserver) {
    responseObserver.onError(
        Status.FAILED_PRECONDITION
            .withDescription("Test failure " + request.getProject())
            .asRuntimeException());
  }

  @Override
  public void replace(
      Api.ReplaceRequest request, StreamObserver<Api.ReplaceResponse> responseObserver) {
    responseObserver.onError(
        Status.FAILED_PRECONDITION
            .withDescription("Test failure " + request.getProject())
            .asRuntimeException());
  }

  @Override
  public void delete(
      Api.DeleteRequest request, StreamObserver<Api.DeleteResponse> responseObserver) {
    responseObserver.onError(
        Status.FAILED_PRECONDITION
            .withDescription("Test failure " + request.getProject())
            .asRuntimeException());
  }

  @Override
  public void update(
      Api.UpdateRequest request, StreamObserver<Api.UpdateResponse> responseObserver) {
    responseObserver.onError(
        Status.FAILED_PRECONDITION
            .withDescription("Test failure " + request.getProject())
            .asRuntimeException());
  }

  @Override
  public void read(Api.ReadRequest request, StreamObserver<Api.ReadResponse> responseObserver) {
    responseObserver.onError(
        Status.FAILED_PRECONDITION
            .withDescription("Test failure " + request.getProject())
            .asRuntimeException());
  }

  @Override
  public void search(
      Api.SearchRequest request, StreamObserver<Api.SearchResponse> responseObserver) {
    responseObserver.onError(
        Status.FAILED_PRECONDITION
            .withDescription("Test failure " + request.getProject())
            .asRuntimeException());
  }

  @Override
  public void createOrUpdateCollection(
      Api.CreateOrUpdateCollectionRequest request,
      StreamObserver<Api.CreateOrUpdateCollectionResponse> responseObserver) {
    responseObserver.onError(
        Status.FAILED_PRECONDITION
            .withDescription("Test failure " + request.getProject())
            .asRuntimeException());
  }

  @Override
  public void dropCollection(
      Api.DropCollectionRequest request,
      StreamObserver<Api.DropCollectionResponse> responseObserver) {
    responseObserver.onError(
        Status.FAILED_PRECONDITION
            .withDescription("Test failure " + request.getProject())
            .asRuntimeException());
  }

  @Override
  public void listProjects(
      Api.ListProjectsRequest request, StreamObserver<Api.ListProjectsResponse> responseObserver) {
    responseObserver.onError(
        Status.FAILED_PRECONDITION
            .withDescription("Test failure listDatabases")
            .asRuntimeException());
  }

  @Override
  public void listCollections(
      Api.ListCollectionsRequest request,
      StreamObserver<Api.ListCollectionsResponse> responseObserver) {
    responseObserver.onError(
        Status.FAILED_PRECONDITION
            .withDescription("Test failure " + request.getProject())
            .asRuntimeException());
  }

  @Override
  public void createProject(
      Api.CreateProjectRequest request,
      StreamObserver<Api.CreateProjectResponse> responseObserver) {
    responseObserver.onError(
        Status.FAILED_PRECONDITION
            .withDescription("Test failure " + request.getProject())
            .asRuntimeException());
  }

  @Override
  public void deleteProject(
      Api.DeleteProjectRequest request,
      StreamObserver<Api.DeleteProjectResponse> responseObserver) {
    responseObserver.onError(
        Status.FAILED_PRECONDITION
            .withDescription("Test failure " + request.getProject())
            .asRuntimeException());
  }
}
