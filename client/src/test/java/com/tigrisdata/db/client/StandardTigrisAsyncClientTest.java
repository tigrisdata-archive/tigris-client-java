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

import com.tigrisdata.db.client.config.TigrisConfiguration;
import com.tigrisdata.db.client.grpc.TestObservabilityService;
import com.tigrisdata.db.client.grpc.TestTigrisService;
import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

public class StandardTigrisAsyncClientTest {

  private static String SERVER_NAME;
  private static TestTigrisService TEST_USER_SERVICE;

  @ClassRule public static final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

  @BeforeClass
  public static void setup() throws Exception {
    SERVER_NAME = InProcessServerBuilder.generateName();
    TEST_USER_SERVICE = new TestTigrisService();
    grpcCleanup
        .register(
            InProcessServerBuilder.forName(SERVER_NAME)
                .directExecutor()
                .addService(TEST_USER_SERVICE)
                .addService(new TestObservabilityService())
                .build())
        .start();
  }

  @After
  public void reset() {
    TEST_USER_SERVICE.reset();
  }

  @Test
  public void testGetDatabase() {
    TigrisAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup, "db1");
    TigrisAsyncDatabase db1 = asyncClient.getDatabase();
    Assert.assertEquals("db1", db1.name());
  }

  @Test
  public void testServerMetadata() throws Exception {
    TigrisAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup, "db1");
    ServerMetadata serverMetadata = asyncClient.getServerMetadata().get();
    Assert.assertEquals("1.2.3-alpha.4", serverMetadata.getServerVersion());
  }

  @Test
  public void testClose() throws Exception {
    ManagedChannelBuilder mockedChannelBuilder = Mockito.mock(ManagedChannelBuilder.class);
    Mockito.when(mockedChannelBuilder.intercept(ArgumentMatchers.any(ClientInterceptor.class)))
        .thenReturn(mockedChannelBuilder);
    ManagedChannel mockedChannel = Mockito.mock(ManagedChannel.class);
    Mockito.when(mockedChannelBuilder.build()).thenReturn(mockedChannel);

    TigrisAsyncClient asyncClient =
        new StandardTigrisAsyncClient(
            TigrisConfiguration.newBuilder("some-url", "db1").build(), mockedChannelBuilder);
    asyncClient.close();
    Mockito.verify(mockedChannel, Mockito.times(1)).shutdown();
  }
}
