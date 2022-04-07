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

import com.tigrisdata.db.client.error.TigrisDBException;
import com.tigrisdata.db.client.grpc.TestUserService;
import com.tigrisdata.db.client.model.CollectionInfo;
import com.tigrisdata.db.client.model.CollectionOptions;
import com.tigrisdata.db.client.model.CreateOrUpdateCollectionResponse;
import com.tigrisdata.db.client.model.DropCollectionResponse;
import com.tigrisdata.db.client.model.TigrisDBJSONSchema;
import com.tigrisdata.db.client.model.TransactionOptions;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class StandardTigrisAsyncDatabaseTest {
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
  public void testListCollections() throws InterruptedException, ExecutionException {
    TigrisDBAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
    TigrisAsyncDatabase db1 = asyncClient.getDatabase("db1");
    CompletableFuture<List<CollectionInfo>> collections = db1.listCollections();
    Assert.assertEquals(5, collections.get().size());
    MatcherAssert.assertThat(
        collections.get(),
        Matchers.containsInAnyOrder(
            new CollectionInfo("db1_c0"),
            new CollectionInfo("db1_c1"),
            new CollectionInfo("db1_c2"),
            new CollectionInfo("db1_c3"),
            new CollectionInfo("db1_c4")));
  }

  @Test
  public void testCreateCollection()
      throws TigrisDBException, InterruptedException, ExecutionException, IOException {
    TigrisDBAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
    TigrisAsyncDatabase db1 = asyncClient.getDatabase("db1");
    CompletableFuture<CreateOrUpdateCollectionResponse> response =
        db1.createOrUpdateCollection(
            new TigrisDBJSONSchema(new URL("file:src/test/resources/db1_c5.json")),
            new CollectionOptions());
    Assert.assertEquals("db1_c5 created", response.get().getTigrisDBResponse().getMessage());
    MatcherAssert.assertThat(
        db1.listCollections().get(),
        Matchers.containsInAnyOrder(
            new CollectionInfo("db1_c0"),
            new CollectionInfo("db1_c1"),
            new CollectionInfo("db1_c2"),
            new CollectionInfo("db1_c3"),
            new CollectionInfo("db1_c4"),
            new CollectionInfo("db1_c5")));
  }

  @Test
  public void testDropCollection() throws InterruptedException, ExecutionException {
    TigrisDBAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
    TigrisAsyncDatabase db1 = asyncClient.getDatabase("db1");
    CompletableFuture<DropCollectionResponse> response = db1.dropCollection("db1_c3");
    Assert.assertEquals("db1_c3 dropped", response.get().getTigrisDBResponse().getMessage());
    MatcherAssert.assertThat(
        db1.listCollections().get(),
        Matchers.containsInAnyOrder(
            new CollectionInfo("db1_c0"),
            new CollectionInfo("db1_c1"),
            new CollectionInfo("db1_c2"),
            new CollectionInfo("db1_c4")));
  }

  @Test
  public void testTransaction() {
    TigrisDBAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
    TigrisAsyncDatabase db1 = asyncClient.getDatabase("db1");
    CompletableFuture<TransactionSession> response = db1.beginTransaction(new TransactionOptions());
    response.join();
    Assert.assertTrue(response.isDone());
    Assert.assertFalse(response.isCompletedExceptionally());
  }

  @Test
  public void testGetCollection() {
    TigrisDBAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
    TigrisAsyncDatabase db1 = asyncClient.getDatabase("db1");
    TigrisAsyncCollection<DB1_C1> c1TigrisCollection = db1.getCollection(DB1_C1.class);
    Assert.assertEquals("db1_c1", c1TigrisCollection.name());
  }

  @Test
  public void testName() {
    TigrisDBAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
    TigrisAsyncDatabase db1 = asyncClient.getDatabase("db1");
    Assert.assertEquals("db1", db1.name());
  }
}
