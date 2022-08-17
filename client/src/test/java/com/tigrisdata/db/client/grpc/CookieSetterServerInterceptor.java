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

import com.tigrisdata.db.api.v1.grpc.TigrisGrpc;
import io.grpc.ForwardingServerCall;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/** Helper server interceptor that sets cookie header for beginTransaction method calls */
public class CookieSetterServerInterceptor implements ServerInterceptor {
  static final Metadata.Key<String> COOKIE =
      Metadata.Key.of("Set-Cookie", Metadata.ASCII_STRING_MARSHALLER);

  @Override
  public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
      ServerCall<ReqT, RespT> call,
      final Metadata requestHeaders,
      ServerCallHandler<ReqT, RespT> next) {
    return next.startCall(
        new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
          @Override
          public void sendHeaders(Metadata responseHeaders) {
            if (call.getMethodDescriptor()
                .getFullMethodName()
                .equals(TigrisGrpc.getBeginTransactionMethod().getFullMethodName())) {
              responseHeaders.put(
                  COOKIE,
                  "Tigris-Tx-Id="
                      + UUID.randomUUID()
                      + ";Expires="
                      + new SimpleDateFormat("EEE, dd MMM yyyyy HH:mm:ss z")
                          .format(new Date(System.currentTimeMillis() + 30_000L)));
            }
            super.sendHeaders(responseHeaders);
          }
        },
        requestHeaders);
  }
}
