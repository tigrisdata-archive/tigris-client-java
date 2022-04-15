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
package com.tigrisdata.db.client;

import com.tigrisdata.db.client.auth.TigrisAuthorizationToken;
import com.tigrisdata.db.client.config.TigrisDBConfiguration;
import io.grpc.ManagedChannelBuilder;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.testing.GrpcCleanupRule;

public final class TestUtils {

  private TestUtils() {}

  public static StandardTigrisDBClient getTestClient(
      String grpcServerName, GrpcCleanupRule grpcCleanupRule) {
    ManagedChannelBuilder<InProcessChannelBuilder> channelBuilder =
        InProcessChannelBuilder.forName(grpcServerName);
    StandardTigrisDBClient client =
        new StandardTigrisDBClient(
            new TigrisAuthorizationToken("some.dummy.token"),
            TigrisDBConfiguration.newBuilder("some-url").build(),
            channelBuilder);
    grpcCleanupRule.register(client.getChannel());
    return client;
  }

  public static StandardTigrisDBAsyncClient getTestAsyncClient(
      String grpcServerName, GrpcCleanupRule grpcCleanupRule) {
    ManagedChannelBuilder<InProcessChannelBuilder> channelBuilder =
        InProcessChannelBuilder.forName(grpcServerName);
    StandardTigrisDBAsyncClient client =
        new StandardTigrisDBAsyncClient(
            new TigrisAuthorizationToken("some.dummy.token"),
            TigrisDBConfiguration.newBuilder("some-url").build(),
            channelBuilder);
    grpcCleanupRule.register(client.getChannel());
    return client;
  }
}