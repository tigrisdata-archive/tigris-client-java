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
import com.tigrisdata.db.client.grpc.TestTigrisService;
import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
                .build())
        .start();
  }

  @After
  public void reset() {
    TEST_USER_SERVICE.reset();
  }

  @Test
  public void testGetDatabase() {
    TigrisAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
    TigrisAsyncDatabase db1 = asyncClient.getDatabase("db1");
    Assert.assertEquals("db1", db1.name());
  }

  @Test
  public void testListDatabases() throws InterruptedException, ExecutionException {
    TigrisAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
    CompletableFuture<List<TigrisAsyncDatabase>> listDatabasesResponse =
        asyncClient.listDatabases(DatabaseOptions.DEFAULT_INSTANCE);
    List<TigrisAsyncDatabase> databases = listDatabasesResponse.get();
    Assert.assertEquals(3, databases.size());
    // note: equals ignores stub and channel so avoid passing them
    MatcherAssert.assertThat(
        databases,
        Matchers.containsInAnyOrder(
            new StandardTigrisAsyncDatabase("db1", null, null, null, null, null, null, null, null),
            new StandardTigrisAsyncDatabase("db2", null, null, null, null, null, null, null, null),
            new StandardTigrisAsyncDatabase(
                "db3", null, null, null, null, null, null, null, null)));
  }

  @Test
  public void testCreateDatabase() throws InterruptedException, ExecutionException {
    TigrisAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
    CompletableFuture<TigrisAsyncDatabase> response = asyncClient.createDatabaseIfNotExists("db4");
    Assert.assertNotNull(response.get());
    // 4th db created
    Assert.assertEquals(
        4, asyncClient.listDatabases(DatabaseOptions.DEFAULT_INSTANCE).get().size());
  }

  @Test
  public void testAlreadyExisingDatabaseCreation() throws InterruptedException, ExecutionException {
    TigrisAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
    CompletableFuture<TigrisAsyncDatabase> response =
        asyncClient.createDatabaseIfNotExists("pre-existing-db-name");
    Assert.assertNotNull(response.get());
  }

  @Test
  public void testDropDatabase() throws InterruptedException, ExecutionException {
    TigrisAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
    CompletableFuture<DropDatabaseResponse> response = asyncClient.dropDatabase("db2");
    Assert.assertEquals("db2 dropped", response.get().getMessage());
    // 4th db created
    Assert.assertEquals(
        2, asyncClient.listDatabases(DatabaseOptions.DEFAULT_INSTANCE).get().size());
    MatcherAssert.assertThat(
        asyncClient.listDatabases(DatabaseOptions.DEFAULT_INSTANCE).get(),
        Matchers.containsInAnyOrder(
            new StandardTigrisAsyncDatabase("db1", null, null, null, null, null, null, null, null),
            new StandardTigrisAsyncDatabase(
                "db3", null, null, null, null, null, null, null, null)));
  }

  @Test
  public void testServerMetadata() throws Exception {
    TigrisAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
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
            TigrisConfiguration.newBuilder("some-url").build(), mockedChannelBuilder);
    asyncClient.close();
    Mockito.verify(mockedChannel, Mockito.times(1)).shutdown();
  }
}
