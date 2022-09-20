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

import com.tigrisdata.db.client.collection.ChatMessage;
import com.tigrisdata.db.client.collection.DB1_C1;
import com.tigrisdata.db.client.collection.DB1_C5;
import com.tigrisdata.db.client.collection.User;
import com.tigrisdata.db.client.collection.collection2.DB1_C3;
import com.tigrisdata.db.client.error.TigrisException;
import com.tigrisdata.db.client.grpc.ContextSettingServerInterceptor;
import com.tigrisdata.db.client.grpc.TestTigrisService;
import com.tigrisdata.db.type.TigrisDocumentCollectionType;
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
import java.util.function.Predicate;

public class StandardTigrisDatabaseTest {
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
                .intercept(new ContextSettingServerInterceptor())
                .build())
        .start();
  }

  @After
  public void reset() {
    TEST_USER_SERVICE.reset();
  }

  @Test
  public void testListCollections() throws TigrisException {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    List<CollectionInfo> collections = db1.listCollections();
    Assert.assertEquals(5, collections.size());
    MatcherAssert.assertThat(
        collections,
        Matchers.containsInAnyOrder(
            new CollectionInfo("db1_c0"),
            new CollectionInfo("db1_c1"),
            new CollectionInfo("db1_c2"),
            new CollectionInfo("db1_c3"),
            new CollectionInfo("db1_c4")));
  }

  @Test
  public void testCreateOrCollectionsFromModel() throws TigrisException {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    CreateOrUpdateCollectionsResponse response =
        db1.createOrUpdateCollections(User.class, DB1_C5.class);
    Assert.assertEquals("Collections created successfully", response.getMessage());
    MatcherAssert.assertThat(
        db1.listCollections(),
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
  public void testCreateOrCollectionsFromModelsUsingClasspathScan() throws TigrisException {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    CreateOrUpdateCollectionsResponse response =
        db1.createOrUpdateCollections(
            new String[] {"com.tigrisdata.db.client.collection"}, Optional.empty());
    Assert.assertEquals("Collections created successfully", response.getMessage());
    MatcherAssert.assertThat(
        db1.listCollections(),
        Matchers.containsInAnyOrder(
            new CollectionInfo("db1_c0"),
            new CollectionInfo("db1_c1"),
            new CollectionInfo("db1_c2"),
            new CollectionInfo("db1_c3"),
            new CollectionInfo("db1_c4"),
            new CollectionInfo("db1_c5"),
            new CollectionInfo("db1_c6"),
            new CollectionInfo("users"),
            new CollectionInfo("auto_generating_p_keys_models")));
  }

  @Test
  public void testCreateOrCollectionsFromModelsUsingClasspathScanWithFilter()
      throws TigrisException {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    Predicate<Class<? extends TigrisDocumentCollectionType>> filter =
        clazz -> clazz.getSimpleName().startsWith("DB1");
    CreateOrUpdateCollectionsResponse response =
        db1.createOrUpdateCollections(
            new String[] {"com.tigrisdata.db.client.collection"}, Optional.of(filter));
    Assert.assertEquals("Collections created successfully", response.getMessage());
    MatcherAssert.assertThat(
        db1.listCollections(),
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
  public void testCreateOrUpdateTopicsFromModel() throws TigrisException {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    CreateOrUpdateTopicResponse response = db1.createOrUpdateTopics(ChatMessage.class);
    Assert.assertEquals("Topics created successfully", response.getMessage());
  }

  @Test
  public void testDropCollection() throws TigrisException {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    DropCollectionResponse response = db1.dropCollection(DB1_C3.class);
    Assert.assertEquals("db1_c3 dropped", response.getMessage());
    MatcherAssert.assertThat(
        db1.listCollections(),
        Matchers.containsInAnyOrder(
            new CollectionInfo("db1_c0"),
            new CollectionInfo("db1_c1"),
            new CollectionInfo("db1_c2"),
            new CollectionInfo("db1_c4")));
  }

  @Test
  public void testGetCollection() {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    TigrisCollection<DB1_C1> c1TigrisCollection = db1.getCollection(DB1_C1.class);
    Assert.assertEquals("db1_c1", c1TigrisCollection.name());
  }

  @Test
  public void testBeginTransaction() throws TigrisException {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    TransactionSession transactionSession =
        db1.beginTransaction(TransactionOptions.DEFAULT_INSTANCE);
    transactionSession.commit();
  }

  @Test
  public void testCommitTransaction() throws TigrisException {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    TransactionSession transactionSession =
        db1.beginTransaction(TransactionOptions.DEFAULT_INSTANCE);
    transactionSession.commit();
  }

  @Test
  public void testRollbackTransaction() throws TigrisException {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    TransactionSession transactionSession =
        db1.beginTransaction(TransactionOptions.DEFAULT_INSTANCE);
    transactionSession.rollback();
  }

  @Test
  public void testDescribe() throws TigrisException, IOException {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    DatabaseDescription databaseDescription = db1.describe();
    Assert.assertEquals("db1", databaseDescription.getName());
    Assert.assertEquals(
        "{\"title\":\"db1_c5\",\"description\":\"This document records the details of user for tigris store\","
            + "\"properties\":{\"id\":{\"description\":\"A unique identifier for the user\","
            + "\"type\":\"int\"},"
            + "\"name\":{\"description\":\"Name of the user\",\"type\":\"string\"},"
            + "\"balance\":{\"description\":\"user balance in USD\",\"type\":\"double\"}},"
            + "\"primary_key\":[\"id\"]}",
        databaseDescription.getCollectionsDescription().get(0).getSchema().getSchemaContent());
    Assert.assertNotNull(databaseDescription.getMetadata());
    Assert.assertNotNull(databaseDescription.getMetadata());
  }

  @Test
  public void testName() {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    Assert.assertEquals("db1", db1.name());
  }

  @Test
  public void testTransactionalFunction() throws TigrisException {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    Assert.assertEquals(
        "db1_c1_d1", db1.getCollection(DB1_C1.class).readOne(Filters.eq("id", 1)).get().getName());
    db1.transact(
        tx -> {
          try {
            // insert
            TigrisCollection<DB1_C1> collection = db1.getCollection(DB1_C1.class);
            collection.insert(tx, new DB1_C1(100, "db1_c1_d100"));
            collection.update(
                tx, Filters.eq("id", 1), UpdateFields.newBuilder().set("name", "new name").build());
          } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail("must not fail");
          }
        });
    // now the updates must be made from transaction
    Assert.assertTrue(db1.getCollection(DB1_C1.class).readOne(Filters.eq("id", 100)).isPresent());
    Assert.assertEquals(
        "new name", db1.getCollection(DB1_C1.class).readOne(Filters.eq("id", 1)).get().getName());
  }

  @Test
  public void testToString() {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    Assert.assertEquals("StandardTigrisDatabase{db='db1'}", db1.toString());
  }

  @Test
  public void testHashcode() {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db11 = client.getDatabase("db1");
    TigrisDatabase db12 = client.getDatabase("db1");
    Assert.assertEquals(db11.hashCode(), db12.hashCode());

    // null dbName resolves to 0 hashcode
    Assert.assertEquals(
        0, new StandardTigrisDatabase(null, null, null, null, null, null).hashCode());
  }

  @Test
  public void testEquals() {
    TigrisDatabase db1 = new StandardTigrisDatabase("db1", null, null, null, null, null);
    TigrisDatabase db2 = new StandardTigrisDatabase("db1", null, null, null, null, null);
    Assert.assertEquals(db1, db2);
    Assert.assertEquals(db1, db1);

    Assert.assertFalse(db1.equals(null));
    Assert.assertFalse(db1.equals("string-obj"));
  }
}
