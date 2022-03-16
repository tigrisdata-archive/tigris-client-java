package com.tigrisdata.db.client.interceptors;

import com.tigrisdata.db.client.auth.AuthorizationToken;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.ForwardingClientCallListener;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;

public class AuthHeaderInterceptor implements ClientInterceptor {
  private static final Metadata.Key<String> AUTH_HEADER_KEY =
      Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER);

  private final AuthorizationToken authorizationToken;

  public AuthHeaderInterceptor(AuthorizationToken authorizationToken) {
    this.authorizationToken = authorizationToken;
  }

  @Override
  public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
      MethodDescriptor<ReqT, RespT> methodDescriptor, CallOptions callOptions, Channel channel) {
    return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(
        channel.newCall(methodDescriptor, callOptions)) {

      @Override
      public void start(Listener<RespT> responseListener, Metadata headers) {
        headers.put(AUTH_HEADER_KEY, authorizationToken.getAuthorizationToken());
        super.start(
            new ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(
                responseListener) {},
            headers);
      }
    };
  }
}
