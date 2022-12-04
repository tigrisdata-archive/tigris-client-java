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
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

public class AuthEnabledTestTigrisService extends TestTigrisService {
  public AuthEnabledTestTigrisService() {
    super();
  }

  @Override
  public void beginTransaction(
      Api.BeginTransactionRequest request,
      StreamObserver<Api.BeginTransactionResponse> responseObserver) {
    if (!containsAuthHeaders()) {
      responseObserver.onError(new StatusRuntimeException(Status.UNAUTHENTICATED));
      return;
    }
    super.beginTransaction(request, responseObserver);
  }

  @Override
  public void commitTransaction(
      Api.CommitTransactionRequest request,
      StreamObserver<Api.CommitTransactionResponse> responseObserver) {
    if (!containsAuthHeaders()) {
      responseObserver.onError(new StatusRuntimeException(Status.UNAUTHENTICATED));
      return;
    }
    super.commitTransaction(request, responseObserver);
  }

  @Override
  public void rollbackTransaction(
      Api.RollbackTransactionRequest request,
      StreamObserver<Api.RollbackTransactionResponse> responseObserver) {
    if (!containsAuthHeaders()) {
      responseObserver.onError(new StatusRuntimeException(Status.UNAUTHENTICATED));
      return;
    }
    super.rollbackTransaction(request, responseObserver);
  }

  @Override
  public void insert(
      Api.InsertRequest request, StreamObserver<Api.InsertResponse> responseObserver) {
    if (!containsAuthHeaders()) {
      responseObserver.onError(new StatusRuntimeException(Status.UNAUTHENTICATED));
      return;
    }
    super.insert(request, responseObserver);
  }

  @Override
  public void delete(
      Api.DeleteRequest request, StreamObserver<Api.DeleteResponse> responseObserver) {
    if (!containsAuthHeaders()) {
      responseObserver.onError(new StatusRuntimeException(Status.UNAUTHENTICATED));
      return;
    }
    super.delete(request, responseObserver);
  }

  @Override
  public void update(
      Api.UpdateRequest request, StreamObserver<Api.UpdateResponse> responseObserver) {
    if (!containsAuthHeaders()) {
      responseObserver.onError(new StatusRuntimeException(Status.UNAUTHENTICATED));
      return;
    }
    super.update(request, responseObserver);
  }

  @Override
  public void replace(
      Api.ReplaceRequest request, StreamObserver<Api.ReplaceResponse> responseObserver) {
    if (!containsAuthHeaders()) {
      responseObserver.onError(new StatusRuntimeException(Status.UNAUTHENTICATED));
      return;
    }
    super.replace(request, responseObserver);
  }

  @Override
  public void read(Api.ReadRequest request, StreamObserver<Api.ReadResponse> responseObserver) {
    if (!containsAuthHeaders()) {
      responseObserver.onError(new StatusRuntimeException(Status.UNAUTHENTICATED));
      return;
    }
    super.read(request, responseObserver);
  }

  @Override
  public void createOrUpdateCollection(
      Api.CreateOrUpdateCollectionRequest request,
      StreamObserver<Api.CreateOrUpdateCollectionResponse> responseObserver) {
    if (!containsAuthHeaders()) {
      responseObserver.onError(new StatusRuntimeException(Status.UNAUTHENTICATED));
      return;
    }
    super.createOrUpdateCollection(request, responseObserver);
  }

  @Override
  public void dropCollection(
      Api.DropCollectionRequest request,
      StreamObserver<Api.DropCollectionResponse> responseObserver) {
    if (!containsAuthHeaders()) {
      responseObserver.onError(new StatusRuntimeException(Status.UNAUTHENTICATED));
      return;
    }
    super.dropCollection(request, responseObserver);
  }

  @Override
  public void listProjects(
      Api.ListProjectsRequest request, StreamObserver<Api.ListProjectsResponse> responseObserver) {
    if (!containsAuthHeaders()) {
      responseObserver.onError(new StatusRuntimeException(Status.UNAUTHENTICATED));
      return;
    }
    super.listProjects(request, responseObserver);
  }

  @Override
  public void listCollections(
      Api.ListCollectionsRequest request,
      StreamObserver<Api.ListCollectionsResponse> responseObserver) {
    if (!containsAuthHeaders()) {
      responseObserver.onError(new StatusRuntimeException(Status.UNAUTHENTICATED));
      return;
    }
    super.listCollections(request, responseObserver);
  }

  @Override
  public void createProject(
      Api.CreateProjectRequest request,
      StreamObserver<Api.CreateProjectResponse> responseObserver) {
    if (!containsAuthHeaders()) {
      responseObserver.onError(new StatusRuntimeException(Status.UNAUTHENTICATED));
      return;
    }
    super.createProject(request, responseObserver);
  }

  @Override
  public void deleteProject(
      Api.DeleteProjectRequest request,
      StreamObserver<Api.DeleteProjectResponse> responseObserver) {
    if (!containsAuthHeaders()) {
      responseObserver.onError(new StatusRuntimeException(Status.UNAUTHENTICATED));
      return;
    }
    super.deleteProject(request, responseObserver);
  }

  private boolean containsAuthHeaders() {
    String incomingAuthorizationHeader =
        ContextSettingServerInterceptor.AUTHORIZATION_CONTEXT_KEY.get();
    return incomingAuthorizationHeader != null
        && incomingAuthorizationHeader.equals(
            "bearer <header>.ewogIAogICJleHAiOiAyOTk5OTk5OTk5Cn0=.<signature>");
  }
}
