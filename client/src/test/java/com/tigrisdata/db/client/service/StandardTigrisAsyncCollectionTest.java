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
import com.tigrisdata.db.client.model.DeleteRequestOptions;
import com.tigrisdata.db.client.model.DeleteResponse;
import com.tigrisdata.db.client.model.Filters;
import com.tigrisdata.db.client.model.InsertOrReplaceRequestOptions;
import com.tigrisdata.db.client.model.InsertOrReplaceResponse;
import com.tigrisdata.db.client.model.InsertRequestOptions;
import com.tigrisdata.db.client.model.InsertResponse;
import com.tigrisdata.db.client.model.ReadFields;
import com.tigrisdata.db.client.model.UpdateFields;
import com.tigrisdata.db.client.model.UpdateResponse;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class StandardTigrisAsyncCollectionTest {

  private static String SERVER_NAME;
  private static final TestUserService TEST_USER_SERVICE = new TestUserService();
  @ClassRule public static final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

  @BeforeClass
  public static void setup() throws Exception {
    SERVER_NAME = InProcessServerBuilder.generateName();

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
  public void testRead() {
    TigrisDBAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
    TigrisAsyncDatabase db1 = asyncClient.getDatabase("db1");
    inspectDocs(
        db1,
        new DB1_C1(0L, "db1_c1_d0"),
        new DB1_C1(1L, "db1_c1_d1"),
        new DB1_C1(2L, "db1_c1_d2"),
        new DB1_C1(3L, "db1_c1_d3"),
        new DB1_C1(4L, "db1_c1_d4"));
  }

  @Test
  public void testReadOne() {
    TigrisDBAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
    TigrisAsyncDatabase db1 = asyncClient.getDatabase("db1");
    CompletableFuture<Optional<DB1_C1>> result =
        db1.getCollection(DB1_C1.class).readOne(Filters.eq("id", 1L));
    Optional<DB1_C1> db1_c1 = result.join();
    //noinspection OptionalGetWithoutIsPresent
    Assert.assertEquals(1L, db1_c1.get().getId());
    Assert.assertEquals("db1_c1_d1", db1_c1.get().getName());
  }

  @Test
  public void testInsert1() throws TigrisDBException, ExecutionException, InterruptedException {
    TigrisDBAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
    TigrisAsyncDatabase db1 = asyncClient.getDatabase("db1");
    CompletableFuture<InsertResponse> response =
        db1.getCollection(DB1_C1.class)
            .insert(
                Collections.singletonList(new DB1_C1(5L, "db1_c1_test-inserted")),
                new InsertRequestOptions());
    response.get();
    inspectDocs(
        db1,
        new DB1_C1(0L, "db1_c1_d0"),
        new DB1_C1(1L, "db1_c1_d1"),
        new DB1_C1(2L, "db1_c1_d2"),
        new DB1_C1(3L, "db1_c1_d3"),
        new DB1_C1(4L, "db1_c1_d4"),
        new DB1_C1(5L, "db1_c1_test-inserted"));
  }

  @Test
  public void testInsert2() throws TigrisDBException, ExecutionException, InterruptedException {
    TigrisDBAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
    TigrisAsyncDatabase db1 = asyncClient.getDatabase("db1");
    CompletableFuture<InsertResponse> response =
        db1.getCollection(DB1_C1.class).insert(new DB1_C1(5L, "db1_c1_test-inserted"));
    response.get();
    inspectDocs(
        db1,
        new DB1_C1(0L, "db1_c1_d0"),
        new DB1_C1(1L, "db1_c1_d1"),
        new DB1_C1(2L, "db1_c1_d2"),
        new DB1_C1(3L, "db1_c1_d3"),
        new DB1_C1(4L, "db1_c1_d4"),
        new DB1_C1(5L, "db1_c1_test-inserted"));
  }

  @Test
  public void testInsert3() throws TigrisDBException, ExecutionException, InterruptedException {
    TigrisDBAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
    TigrisAsyncDatabase db1 = asyncClient.getDatabase("db1");
    CompletableFuture<InsertResponse> response =
        db1.getCollection(DB1_C1.class)
            .insert(Collections.singletonList(new DB1_C1(5L, "db1_c1_test-inserted")));
    response.get();
    inspectDocs(
        db1,
        new DB1_C1(0L, "db1_c1_d0"),
        new DB1_C1(1L, "db1_c1_d1"),
        new DB1_C1(2L, "db1_c1_d2"),
        new DB1_C1(3L, "db1_c1_d3"),
        new DB1_C1(4L, "db1_c1_d4"),
        new DB1_C1(5L, "db1_c1_test-inserted"));
  }

  @Test
  public void testReplace() throws TigrisDBException, ExecutionException, InterruptedException {
    TigrisDBAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
    TigrisAsyncDatabase db1 = asyncClient.getDatabase("db1");
    List<DB1_C1> replacePayload = new ArrayList<>();
    replacePayload.add(new DB1_C1(1L, "testReplace1"));
    replacePayload.add(new DB1_C1(3L, "testReplace3"));
    replacePayload.add(new DB1_C1(4L, "testReplace4"));
    CompletableFuture<InsertOrReplaceResponse> response =
        db1.getCollection(DB1_C1.class)
            .insertOrReplace(replacePayload, new InsertOrReplaceRequestOptions());
    response.get();
    inspectDocs(
        db1,
        new DB1_C1(0L, "db1_c1_d0"),
        new DB1_C1(1L, "testReplace1"),
        new DB1_C1(2L, "db1_c1_d2"),
        new DB1_C1(3L, "testReplace3"),
        new DB1_C1(4L, "testReplace4"));
    Assert.assertNotNull(response);
  }

  @Test
  public void testDelete1() throws ExecutionException, InterruptedException {
    TigrisDBAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
    TigrisAsyncDatabase db1 = asyncClient.getDatabase("db1");
    CompletableFuture<DeleteResponse> response =
        db1.getCollection(DB1_C1.class).delete(Filters.eq("id", 3), new DeleteRequestOptions());
    response.get();
    inspectDocs(
        db1,
        new DB1_C1(0L, "db1_c1_d0"),
        new DB1_C1(1L, "db1_c1_d1"),
        new DB1_C1(2L, "db1_c1_d2"),
        new DB1_C1(4L, "db1_c1_d4"));

    response =
        db1.getCollection(DB1_C1.class).delete(Filters.eq("id", 1), new DeleteRequestOptions());
    response.get();
    inspectDocs(
        db1, new DB1_C1(0L, "db1_c1_d0"), new DB1_C1(2L, "db1_c1_d2"), new DB1_C1(4L, "db1_c1_d4"));
  }

  @Test
  public void testDelete2() throws ExecutionException, InterruptedException {
    TigrisDBAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
    TigrisAsyncDatabase db1 = asyncClient.getDatabase("db1");
    CompletableFuture<DeleteResponse> response =
        db1.getCollection(DB1_C1.class).delete(Filters.eq("id", 3), new DeleteRequestOptions());
    response.get();
    inspectDocs(
        db1,
        new DB1_C1(0L, "db1_c1_d0"),
        new DB1_C1(1L, "db1_c1_d1"),
        new DB1_C1(2L, "db1_c1_d2"),
        new DB1_C1(4L, "db1_c1_d4"));

    response = db1.getCollection(DB1_C1.class).delete(Filters.eq("id", 1));
    response.get();
    inspectDocs(
        db1, new DB1_C1(0L, "db1_c1_d0"), new DB1_C1(2L, "db1_c1_d2"), new DB1_C1(4L, "db1_c1_d4"));
  }

  @Test
  public void testUpdate() throws TigrisDBException, ExecutionException, InterruptedException {
    TigrisDBAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
    TigrisAsyncDatabase db1 = asyncClient.getDatabase("db1");
    CompletableFuture<UpdateResponse> response =
        db1.getCollection(DB1_C1.class)
            .update(
                Filters.eq("id", 1),
                UpdateFields.newBuilder()
                    .set(UpdateFields.SetFields.newBuilder().set("name", "new name 1").build())
                    .build());
    response.get();
    inspectDocs(
        db1,
        new DB1_C1(0L, "db1_c1_d0"),
        new DB1_C1(1L, "new name 1"),
        new DB1_C1(2L, "db1_c1_d2"),
        new DB1_C1(3L, "db1_c1_d3"),
        new DB1_C1(4L, "db1_c1_d4"));
  }

  @Test
  public void testName() {
    TigrisDBAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
    TigrisAsyncDatabase db1 = asyncClient.getDatabase("db1");
    TigrisAsyncCollection<DB1_C1> collection = db1.getCollection(DB1_C1.class);
    Assert.assertEquals("db1_c1", collection.name());
  }

  private static void inspectDocs(TigrisAsyncDatabase db1, DB1_C1... expectedDocs) {
    Map<Long, DB1_C1> expectedDocsMap = new HashMap<>();
    Map<Long, Boolean> seenDocsMap = new HashMap<>();

    AtomicInteger errorCount = new AtomicInteger(0);
    AtomicBoolean completed = new AtomicBoolean(false);
    for (DB1_C1 expectedDoc : expectedDocs) {
      expectedDocsMap.put(expectedDoc.getId(), expectedDoc);
      seenDocsMap.put(expectedDoc.getId(), false);
    }

    db1.getCollection(DB1_C1.class)
        .read(
            Filters.eq("ignore", "ignore"),
            ReadFields.empty(),
            new TigrisDBAsyncReader<DB1_C1>() {
              @Override
              public void onNext(DB1_C1 document) {
                if (expectedDocsMap.get(document.getId()).getName().equals(document.getName())) {
                  seenDocsMap.put(document.getId(), true);
                }
              }

              @Override
              public void onError(Throwable t) {
                errorCount.incrementAndGet();
              }

              @Override
              public void onCompleted() {
                completed.set(true);
              }
            });
    int timeout = 0;
    while (!completed.get() && timeout < 20) {
      timeout++;
      try {
        //noinspection BusyWait
        Thread.sleep(100);
      } catch (InterruptedException ignore) {
      }
    }
    // all must be seen
    boolean result = true;
    for (Boolean value : seenDocsMap.values()) {
      result &= value;
    }
    Assert.assertTrue(result);

    // there must not be any errors
    Assert.assertEquals(0, errorCount.get());
  }
}
