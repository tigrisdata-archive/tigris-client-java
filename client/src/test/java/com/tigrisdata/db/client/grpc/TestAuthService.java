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

import com.tigrisdata.db.api.v1.grpc.AuthGrpc;
import com.tigrisdata.db.api.v1.grpc.AuthOuterClass;
import io.grpc.stub.StreamObserver;

public class TestAuthService extends AuthGrpc.AuthImplBase {
  @Override
  public void getAccessToken(
      AuthOuterClass.GetAccessTokenRequest request,
      StreamObserver<AuthOuterClass.GetAccessTokenResponse> responseObserver) {
    responseObserver.onNext(
        AuthOuterClass.GetAccessTokenResponse.newBuilder()
            .setAccessToken("<header>.ewogIAogICJleHAiOiAyOTk5OTk5OTk5Cn0=.<signature>")
            .setRefreshToken("test-refresh-token")
            .build());
    responseObserver.onCompleted();
  }
}
