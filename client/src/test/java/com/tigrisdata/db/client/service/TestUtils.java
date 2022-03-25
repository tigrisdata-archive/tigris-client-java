package com.tigrisdata.db.client.service;

import com.tigrisdata.db.client.auth.TigrisAuthorizationToken;
import io.grpc.ManagedChannelBuilder;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.testing.GrpcCleanupRule;

public final class TestUtils {

  private TestUtils() {}

  public static StandardTigrisDBClient getTestClient(
      String grpcServerName, GrpcCleanupRule grpcCleanupRule) {
    ManagedChannelBuilder<InProcessChannelBuilder> channel =
        InProcessChannelBuilder.forName(grpcServerName);
    StandardTigrisDBClient client =
        new StandardTigrisDBClient(new TigrisAuthorizationToken("some.dummy.token"), channel);
    grpcCleanupRule.register(client.getChannel());
    return client;
  }
}
