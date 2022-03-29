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

import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

public class ContextSettingServerInterceptor implements ServerInterceptor {

  private static final Metadata.Key<String> TX_ID =
      Metadata.Key.of("tx-id", Metadata.ASCII_STRING_MARSHALLER);
  private static final Metadata.Key<String> TX_ORIGIN =
      Metadata.Key.of("tx-origin", Metadata.ASCII_STRING_MARSHALLER);

  public static final Context.Key<String> TX_ID_CONTEXT_KEY = Context.key("tx-id");
  public static final Context.Key<String> TX_ORIGIN_CONTEXT_KEY = Context.key("tx-origin");

  @Override
  public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
      ServerCall<ReqT, RespT> serverCall,
      Metadata metadata,
      ServerCallHandler<ReqT, RespT> serverCallHandler) {
    Context current =
        Context.current()
            .withValues(
                TX_ID_CONTEXT_KEY,
                metadata.get(TX_ID),
                TX_ORIGIN_CONTEXT_KEY,
                metadata.get(TX_ORIGIN));
    return Contexts.interceptCall(current, serverCall, metadata, serverCallHandler);
  }
}
