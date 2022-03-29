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
package com.tigrisdata.db.client.service;

import com.tigrisdata.db.client.auth.TigrisAuthorizationToken;
import com.tigrisdata.db.client.error.TigrisDBException;
import com.tigrisdata.db.client.grpc.TestUserService;
import com.tigrisdata.db.client.model.DatabaseOptions;
import com.tigrisdata.db.client.model.TigrisDBResponse;
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

public class StandardTigrisDBClientTest {

  private static String SERVER_NAME;
  private static TestUserService TEST_USER_SERVICE;
  @ClassRule public static final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

  @BeforeClass
  public static void setup() throws Exception {
    SERVER_NAME = InProcessServerBuilder.generateName();
    TEST_USER_SERVICE = new TestUserService();
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
    TigrisDBClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    Assert.assertEquals("db1", db1.name());
  }

  @Test
  public void testListDatabases() throws TigrisDBException {
    TigrisDBClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    List<TigrisDatabase> databases = client.listDatabases(new DatabaseOptions());

    Assert.assertEquals(3, databases.size());
    // note: equals ignores stub and channel so avoid passing them
    MatcherAssert.assertThat(
        databases,
        Matchers.containsInAnyOrder(
            new StandardTigrisDatabase("db1", null, null),
            new StandardTigrisDatabase("db2", null, null),
            new StandardTigrisDatabase("db3", null, null)));
  }

  @Test
  public void testCreateDatabase() throws TigrisDBException {
    TigrisDBClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDBResponse response = client.createDatabase("db4", new DatabaseOptions());
    Assert.assertEquals("db4 created", response.getMessage());
    // 4th db created
    Assert.assertEquals(4, client.listDatabases(new DatabaseOptions()).size());
  }

  @Test
  public void testDropDatabase() throws TigrisDBException {
    TigrisDBClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDBResponse response = client.dropDatabase("db2", new DatabaseOptions());
    Assert.assertEquals("db2 dropped", response.getMessage());
    // 4th db created
    Assert.assertEquals(2, client.listDatabases(new DatabaseOptions()).size());
    MatcherAssert.assertThat(
        client.listDatabases(new DatabaseOptions()),
        Matchers.containsInAnyOrder(
            new StandardTigrisDatabase("db1", null, null),
            new StandardTigrisDatabase("db3", null, null)));
  }

  @Test
  public void testClose() throws Exception {
    ManagedChannelBuilder mockedChannelBuilder = Mockito.mock(ManagedChannelBuilder.class);
    Mockito.when(mockedChannelBuilder.intercept(ArgumentMatchers.any(ClientInterceptor.class)))
        .thenReturn(mockedChannelBuilder);
    ManagedChannel mockedChannel = Mockito.mock(ManagedChannel.class);
    Mockito.when(mockedChannelBuilder.build()).thenReturn(mockedChannel);

    TigrisDBClient client =
        new StandardTigrisDBClient(
            new TigrisAuthorizationToken("some.test.token"), mockedChannelBuilder);
    client.close();
    Mockito.verify(mockedChannel, Mockito.times(1)).shutdown();
  }
}
