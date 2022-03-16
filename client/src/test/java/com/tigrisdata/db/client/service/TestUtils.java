package com.tigrisdata.db.client.service;

import com.tigrisdata.db.client.auth.JTWAuthorization;
import com.tigrisdata.db.client.config.TigrisDBConfiguration;
import io.grpc.ManagedChannelBuilder;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.testing.GrpcCleanupRule;

public final class TestUtils {

  private TestUtils() {}

  public static StandardTigrisDBClient getTestClient(
      String grpcServerName, GrpcCleanupRule grpcCleanupRule) {
    ManagedChannelBuilder channel = InProcessChannelBuilder.forName(grpcServerName);
    StandardTigrisDBClient client =
        new StandardTigrisDBClient(
            TigrisDBConfiguration.newBuilder().build(),
            new JTWAuthorization("some.dummy.token"),
            channel);
    grpcCleanupRule.register(client.getChannel());
    return client;
  }
}
