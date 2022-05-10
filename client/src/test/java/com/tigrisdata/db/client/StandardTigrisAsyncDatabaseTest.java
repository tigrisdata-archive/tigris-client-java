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

import com.tigrisdata.db.client.collection.DB1_C1;
import com.tigrisdata.db.client.collection.DB1_C5;
import com.tigrisdata.db.client.collection.User;
import com.tigrisdata.db.client.collection.collection2.DB1_C3;
import com.tigrisdata.db.client.error.TigrisException;
import com.tigrisdata.db.client.grpc.TestUserService;
import com.tigrisdata.db.type.TigrisCollectionType;
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
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;

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
    TigrisAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
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
  public void testCreateOrUpdateCollections() throws InterruptedException, ExecutionException {
    TigrisAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
    TigrisAsyncDatabase db1 = asyncClient.getDatabase("db1");
    CompletableFuture<CreateOrUpdateCollectionsResponse> response =
        db1.createOrUpdateCollections(DB1_C5.class, User.class);
    Assert.assertEquals("created", response.get().getStatus());
    Assert.assertEquals("Collections created or changes applied", response.get().getMessage());
    MatcherAssert.assertThat(
        db1.listCollections().get(),
        Matchers.containsInAnyOrder(
            new CollectionInfo("db1_c0"),
            new CollectionInfo("db1_c1"),
            new CollectionInfo("db1_c2"),
            new CollectionInfo("db1_c3"),
            new CollectionInfo("db1_c4"),
            new CollectionInfo("db1_c5"),
            new CollectionInfo("users")));
  }

  @Test
  public void testCreateOrUpdateCollectionsUsingClasspathScan()
      throws InterruptedException, ExecutionException {
    TigrisAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
    TigrisAsyncDatabase db1 = asyncClient.getDatabase("db1");
    CompletableFuture<CreateOrUpdateCollectionsResponse> response =
        db1.createOrUpdateCollections(
            new String[] {"com.tigrisdata.db.client.collection"}, Optional.empty());
    Assert.assertEquals("created", response.get().getStatus());
    MatcherAssert.assertThat(
        db1.listCollections().get(),
        Matchers.containsInAnyOrder(
            new CollectionInfo("db1_c0"),
            new CollectionInfo("db1_c1"),
            new CollectionInfo("db1_c2"),
            new CollectionInfo("db1_c3"),
            new CollectionInfo("db1_c4"),
            new CollectionInfo("db1_c5"),
            new CollectionInfo("db1_c6"),
            new CollectionInfo("users")));
  }

  @Test
  public void testCreateOrUpdateCollectionsUsingClasspathScanWithFilter()
      throws InterruptedException, ExecutionException {
    TigrisAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
    TigrisAsyncDatabase db1 = asyncClient.getDatabase("db1");
    Predicate<Class<? extends TigrisCollectionType>> filter =
        clazz -> clazz.getSimpleName().startsWith("DB1");

    CompletableFuture<CreateOrUpdateCollectionsResponse> response =
        db1.createOrUpdateCollections(
            new String[] {"com.tigrisdata.db.client.collection"}, Optional.of(filter));
    Assert.assertEquals("created", response.get().getStatus());
    MatcherAssert.assertThat(
        db1.listCollections().get(),
        Matchers.containsInAnyOrder(
            new CollectionInfo("db1_c0"),
            new CollectionInfo("db1_c1"),
            new CollectionInfo("db1_c2"),
            new CollectionInfo("db1_c3"),
            new CollectionInfo("db1_c4"),
            new CollectionInfo("db1_c5"),
            new CollectionInfo("db1_c6")));
  }

  @Test
  public void testDropCollection() throws InterruptedException, ExecutionException {
    TigrisAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
    TigrisAsyncDatabase db1 = asyncClient.getDatabase("db1");
    CompletableFuture<DropCollectionResponse> response = db1.dropCollection(DB1_C3.class);
    Assert.assertEquals("db1_c3 dropped", response.get().getMessage());
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
    TigrisAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
    TigrisAsyncDatabase db1 = asyncClient.getDatabase("db1");
    CompletableFuture<TransactionSession> response = db1.beginTransaction(new TransactionOptions());
    response.join();
    Assert.assertTrue(response.isDone());
    Assert.assertFalse(response.isCompletedExceptionally());
  }

  @Test
  public void testGetCollection() {
    TigrisAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
    TigrisAsyncDatabase db1 = asyncClient.getDatabase("db1");
    TigrisAsyncCollection<DB1_C1> c1TigrisCollection = db1.getCollection(DB1_C1.class);
    Assert.assertEquals("db1_c1", c1TigrisCollection.name());
  }

  @Test
  public void testName() {
    TigrisAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
    TigrisAsyncDatabase db1 = asyncClient.getDatabase("db1");
    Assert.assertEquals("db1", db1.name());
  }

  @Test
  public void testDescribe()
      throws TigrisException, IOException, ExecutionException, InterruptedException {
    TigrisAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
    TigrisAsyncDatabase db1 = asyncClient.getDatabase("db1");
    DatabaseDescription databaseDescription = db1.describe().get();
    Assert.assertEquals("db1", databaseDescription.getName());
    Assert.assertEquals(
        "{\"title\":\"db1_c5\",\"description\":\"This document records the details of user for tigris store\","
            + "\"properties\":{\"id\":{\"description\":\"A unique identifier for the user\",\"type\":\"int\"},"
            + "\"name\":{\"description\":\"Name of the user\",\"type\":\"string\"},"
            + "\"balance\":{\"description\":\"user balance in USD\",\"type\":\"double\"}},"
            + "\"primary_key\":[\"id\"]}",
        databaseDescription.getCollectionsDescription().get(0).getSchema().getSchemaContent());
    Assert.assertNotNull(databaseDescription.getMetadata());
    Assert.assertNotNull(databaseDescription.getMetadata());
  }

  @Test
  public void testToString() {
    TigrisAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
    TigrisAsyncDatabase db1 = asyncClient.getDatabase("db1");
    Assert.assertEquals("StandardTigrisAsyncDatabase{db='db1'}", db1.toString());
  }

  @Test
  public void testHashcode() {
    TigrisAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
    TigrisAsyncDatabase db11 = asyncClient.getDatabase("db1");
    TigrisAsyncDatabase db12 = asyncClient.getDatabase("db1");
    Assert.assertEquals(db11.hashCode(), db12.hashCode());

    // null dbName resolves to 0 hashcode
    Assert.assertEquals(
        0, new StandardTigrisAsyncDatabase(null, null, null, null, null, null, null).hashCode());
  }

  @Test
  public void testEquals() {
    TigrisAsyncDatabase db1 =
        new StandardTigrisAsyncDatabase("db1", null, null, null, null, null, null);
    TigrisAsyncDatabase db2 =
        new StandardTigrisAsyncDatabase("db1", null, null, null, null, null, null);
    Assert.assertTrue(db1.equals(db2));
    Assert.assertTrue(db1.equals(db1));

    Assert.assertFalse(db1.equals(null));
    Assert.assertFalse(db1.equals("string-obj"));
  }
}
