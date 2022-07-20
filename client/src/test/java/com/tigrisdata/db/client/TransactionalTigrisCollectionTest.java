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
import com.tigrisdata.db.client.error.TigrisException;
import com.tigrisdata.db.client.grpc.ContextSettingServerInterceptor;
import com.tigrisdata.db.client.grpc.TransactionTestTigrisService;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class TransactionalTigrisCollectionTest {

  private static String SERVER_NAME;
  private static final TransactionTestTigrisService TEST_TRANSACTION_USER_SERVICE =
      new TransactionTestTigrisService();
  @ClassRule public static final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

  @BeforeClass
  public static void setup() throws Exception {
    SERVER_NAME = InProcessServerBuilder.generateName();

    grpcCleanup
        .register(
            InProcessServerBuilder.forName(SERVER_NAME)
                .directExecutor()
                .intercept(new ContextSettingServerInterceptor())
                .addService(TEST_TRANSACTION_USER_SERVICE)
                .build())
        .start();
  }

  @After
  public void reset() {
    TEST_TRANSACTION_USER_SERVICE.reset();
  }

  @Test
  public void testRead() throws TigrisException {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    TransactionSession transactionSession =
        db1.beginTransaction(TransactionOptions.DEFAULT_INSTANCE);
    TigrisCollection<DB1_C1> collection = db1.getCollection(DB1_C1.class);
    inspectDocs(
        transactionSession,
        collection,
        new DB1_C1(0L, "db1_c1_d0"),
        new DB1_C1(1L, "db1_c1_d1"),
        new DB1_C1(2L, "db1_c1_d2"),
        new DB1_C1(3L, "db1_c1_d3"),
        new DB1_C1(4L, "db1_c1_d4"));
    transactionSession.commit();
  }

  @Test
  public void testReadOne() throws TigrisException {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    TransactionSession session = db1.beginTransaction(TransactionOptions.DEFAULT_INSTANCE);

    TigrisCollection<DB1_C1> collection = db1.getCollection(DB1_C1.class);

    Optional<DB1_C1> result = collection.readOne(session, Filters.eq("id", 0));
    Assert.assertTrue(result.isPresent());
    Assert.assertEquals(0L, result.get().getId());
    Assert.assertEquals("db1_c1_d0", result.get().getName());
    session.commit();
  }

  @Test
  public void testReadOneEmpty() throws TigrisException {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    TransactionSession session = db1.beginTransaction(TransactionOptions.DEFAULT_INSTANCE);

    TigrisCollection<DB1_C1> collection = db1.getCollection(DB1_C1.class);

    Optional<DB1_C1> result = collection.readOne(session, Filters.eq("id", 100));
    Assert.assertFalse(result.isPresent());
    session.commit();
  }

  @Test
  public void testInsert() throws TigrisException {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    TransactionSession transactionSession =
        db1.beginTransaction(TransactionOptions.DEFAULT_INSTANCE);
    TigrisCollection<DB1_C1> collection = db1.getCollection(DB1_C1.class);

    InsertResponse response =
        collection.insert(
            transactionSession,
            Collections.singletonList(new DB1_C1(5L, "db1_c1_test-inserted")),
            new InsertRequestOptions());
    Assert.assertNotNull(response);
    inspectDocs(
        transactionSession,
        collection,
        new DB1_C1(0L, "db1_c1_d0"),
        new DB1_C1(1L, "db1_c1_d1"),
        new DB1_C1(2L, "db1_c1_d2"),
        new DB1_C1(3L, "db1_c1_d3"),
        new DB1_C1(4L, "db1_c1_d4"),
        new DB1_C1(5L, "db1_c1_test-inserted"));
    transactionSession.commit();
  }

  @Test
  public void testInsertOne() throws TigrisException {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    TransactionSession transactionSession =
        db1.beginTransaction(TransactionOptions.DEFAULT_INSTANCE);
    TigrisCollection<DB1_C1> collection = db1.getCollection(DB1_C1.class);

    InsertResponse response =
        collection.insert(transactionSession, new DB1_C1(5L, "db1_c1_test-inserted"));
    Assert.assertNotNull(response);
    inspectDocs(
        transactionSession,
        collection,
        new DB1_C1(0L, "db1_c1_d0"),
        new DB1_C1(1L, "db1_c1_d1"),
        new DB1_C1(2L, "db1_c1_d2"),
        new DB1_C1(3L, "db1_c1_d3"),
        new DB1_C1(4L, "db1_c1_d4"),
        new DB1_C1(5L, "db1_c1_test-inserted"));
    transactionSession.commit();
  }

  @Test
  public void testinsertOrReplace() throws TigrisException {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    TransactionSession transactionSession =
        db1.beginTransaction(TransactionOptions.DEFAULT_INSTANCE);
    TigrisCollection<DB1_C1> collection = db1.getCollection(DB1_C1.class);

    List<DB1_C1> replacePayload = new ArrayList<>();
    replacePayload.add(new DB1_C1(1L, "testReplace1"));
    replacePayload.add(new DB1_C1(3L, "testReplace3"));
    replacePayload.add(new DB1_C1(4L, "testReplace4"));
    InsertOrReplaceResponse response =
        collection.insertOrReplace(
            transactionSession, replacePayload, new InsertOrReplaceRequestOptions());
    inspectDocs(
        transactionSession,
        collection,
        new DB1_C1(0L, "db1_c1_d0"),
        new DB1_C1(1L, "testReplace1"),
        new DB1_C1(2L, "db1_c1_d2"),
        new DB1_C1(3L, "testReplace3"),
        new DB1_C1(4L, "testReplace4"));
    transactionSession.commit();
    Assert.assertNotNull(response);
  }

  @Test
  public void testinsertOrReplaceOverloaded() throws TigrisException {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    TransactionSession transactionSession =
        db1.beginTransaction(TransactionOptions.DEFAULT_INSTANCE);

    TigrisCollection<DB1_C1> collection = db1.getCollection(DB1_C1.class);

    List<DB1_C1> replacePayload = new ArrayList<>();
    replacePayload.add(new DB1_C1(1L, "testReplace1"));
    replacePayload.add(new DB1_C1(3L, "testReplace3"));
    replacePayload.add(new DB1_C1(4L, "testReplace4"));
    InsertOrReplaceResponse response =
        collection.insertOrReplace(transactionSession, replacePayload);
    inspectDocs(
        transactionSession,
        collection,
        new DB1_C1(0L, "db1_c1_d0"),
        new DB1_C1(1L, "testReplace1"),
        new DB1_C1(2L, "db1_c1_d2"),
        new DB1_C1(3L, "testReplace3"),
        new DB1_C1(4L, "testReplace4"));
    transactionSession.commit();
    Assert.assertNotNull(response);
  }

  @Test
  public void testDelete() throws TigrisException {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    TransactionSession transactionSession =
        db1.beginTransaction(TransactionOptions.DEFAULT_INSTANCE);
    TigrisCollection<DB1_C1> collection = db1.getCollection(DB1_C1.class);

    DeleteResponse response =
        collection.delete(transactionSession, Filters.eq("id", 3), new DeleteRequestOptions());
    Assert.assertNotNull(response);
    inspectDocs(
        transactionSession,
        collection,
        new DB1_C1(0L, "db1_c1_d0"),
        new DB1_C1(1L, "db1_c1_d1"),
        new DB1_C1(2L, "db1_c1_d2"),
        new DB1_C1(4L, "db1_c1_d4"));

    response =
        collection.delete(transactionSession, Filters.eq("id", 1), new DeleteRequestOptions());
    Assert.assertNotNull(response);
    inspectDocs(
        transactionSession,
        collection,
        new DB1_C1(0L, "db1_c1_d0"),
        new DB1_C1(2L, "db1_c1_d2"),
        new DB1_C1(4L, "db1_c1_d4"));
    transactionSession.commit();
  }

  @Test
  public void testDeleteOverloaded() throws TigrisException {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    TransactionSession transactionSession =
        db1.beginTransaction(TransactionOptions.DEFAULT_INSTANCE);

    TigrisCollection<DB1_C1> collection = db1.getCollection(DB1_C1.class);

    DeleteResponse response =
        collection.delete(transactionSession, Filters.eq("id", 3), new DeleteRequestOptions());
    Assert.assertNotNull(response);
    inspectDocs(
        transactionSession,
        collection,
        new DB1_C1(0L, "db1_c1_d0"),
        new DB1_C1(1L, "db1_c1_d1"),
        new DB1_C1(2L, "db1_c1_d2"),
        new DB1_C1(4L, "db1_c1_d4"));

    response = collection.delete(transactionSession, Filters.eq("id", 1));
    Assert.assertNotNull(response);
    inspectDocs(
        transactionSession,
        collection,
        new DB1_C1(0L, "db1_c1_d0"),
        new DB1_C1(2L, "db1_c1_d2"),
        new DB1_C1(4L, "db1_c1_d4"));
    transactionSession.commit();
  }

  @Test
  public void testUpdate() throws TigrisException {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    TransactionSession transactionSession =
        db1.beginTransaction(TransactionOptions.DEFAULT_INSTANCE);

    TigrisCollection<DB1_C1> collection = db1.getCollection(DB1_C1.class);

    UpdateResponse response =
        collection.update(
            transactionSession,
            Filters.eq("id", 1),
            UpdateFields.newBuilder().set("name", "new name 1").build());
    Assert.assertNotNull(response);

    inspectDocs(
        transactionSession,
        collection,
        new DB1_C1(0L, "db1_c1_d0"),
        new DB1_C1(1L, "new name 1"),
        new DB1_C1(2L, "db1_c1_d2"),
        new DB1_C1(3L, "db1_c1_d3"),
        new DB1_C1(4L, "db1_c1_d4"));
    transactionSession.commit();
  }

  @Test
  public void testName() throws TigrisException {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    TransactionSession transactionSession =
        db1.beginTransaction(TransactionOptions.DEFAULT_INSTANCE);
    TigrisCollection<DB1_C1> collection = db1.getCollection(DB1_C1.class);

    Assert.assertEquals("db1_c1", collection.name());
    transactionSession.commit();
  }

  private static void inspectDocs(
      TransactionSession session,
      TigrisCollection<DB1_C1> transactionalTigrisCollection,
      DB1_C1... expectedDocs)
      throws TigrisException {
    Iterator<DB1_C1> c1Iterator =
        transactionalTigrisCollection.read(
            session, Filters.eq("ignore", "ignore"), ReadFields.all());
    Assert.assertTrue(c1Iterator.hasNext());
    for (DB1_C1 expectedDoc : expectedDocs) {
      DB1_C1 db1_c1 = c1Iterator.next();
      Assert.assertEquals(expectedDoc.getId(), db1_c1.getId());
      Assert.assertEquals(expectedDoc.getName(), db1_c1.getName());
    }
    Assert.assertFalse(c1Iterator.hasNext());
  }
}
