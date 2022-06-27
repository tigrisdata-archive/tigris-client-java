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

import com.tigrisdata.db.client.collection.AutoGeneratingPKeysModel;
import com.tigrisdata.db.client.collection.DB1_C1;
import com.tigrisdata.db.client.collection.DB1_C5;
import com.tigrisdata.db.client.error.TigrisException;
import com.tigrisdata.db.client.grpc.TestUserService;
import com.tigrisdata.db.client.search.FacetCountDistribution;
import com.tigrisdata.db.client.search.SearchRequest;
import com.tigrisdata.db.client.search.SearchResult;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

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
    TigrisAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
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
    TigrisAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
    TigrisAsyncDatabase db1 = asyncClient.getDatabase("db1");
    CompletableFuture<Optional<DB1_C1>> result =
        db1.getCollection(DB1_C1.class).readOne(Filters.eq("id", 1L));
    Optional<DB1_C1> db1_c1 = result.join();
    //noinspection OptionalGetWithoutIsPresent
    Assert.assertEquals(1L, db1_c1.get().getId());
    Assert.assertEquals("db1_c1_d1", db1_c1.get().getName());
  }

  @Test
  public void testSearch() {
    TigrisAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
    TigrisAsyncDatabase db1 = asyncClient.getDatabase("db1");
    inspectSearchDocs(
        db1,
        Arrays.asList(
            new DB1_C1(0L, "db1_c1_d0"),
            new DB1_C1(1L, "db1_c1_d1"),
            new DB1_C1(2L, "db1_c1_d2"),
            new DB1_C1(3L, "db1_c1_d3"),
            new DB1_C1(4L, "db1_c1_d4")));
  }

  @Test
  public void testInsert1() throws TigrisException, ExecutionException, InterruptedException {
    TigrisAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
    TigrisAsyncDatabase db1 = asyncClient.getDatabase("db1");
    CompletableFuture<InsertResponse<DB1_C1>> response =
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
  public void testInsert2() throws TigrisException, ExecutionException, InterruptedException {
    TigrisAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
    TigrisAsyncDatabase db1 = asyncClient.getDatabase("db1");
    CompletableFuture<InsertResponse<DB1_C1>> response =
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
  public void testInsert3() throws TigrisException, ExecutionException, InterruptedException {
    TigrisAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
    TigrisAsyncDatabase db1 = asyncClient.getDatabase("db1");
    CompletableFuture<InsertResponse<DB1_C1>> response =
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
  public void testInsertWithAutoGenerateId()
      throws TigrisException, ExecutionException, InterruptedException {
    TigrisAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
    TigrisAsyncDatabase db1 = asyncClient.getDatabase("autoGenerateTestDB");
    AutoGeneratingPKeysModel input = new AutoGeneratingPKeysModel("name-without-id");
    CompletableFuture<InsertResponse<AutoGeneratingPKeysModel>> responseCompletableFuture =
        db1.getCollection(AutoGeneratingPKeysModel.class).insert(Collections.singletonList(input));
    InsertResponse<AutoGeneratingPKeysModel> response = responseCompletableFuture.get();
    Assert.assertNotNull(response);
    Assert.assertEquals(5, response.getKeys().length);
    Assert.assertEquals(1, response.getKeys()[0].get("intPKey"));
    Assert.assertEquals(2, response.getKeys()[1].get("intPKey"));
    Assert.assertEquals(3, response.getKeys()[2].get("intPKey"));
    Assert.assertEquals(4, response.getKeys()[3].get("intPKey"));
    Assert.assertEquals(5, response.getKeys()[4].get("intPKey"));

    Assert.assertEquals(Long.MAX_VALUE - 1, response.getKeys()[0].get("longPKey"));
    Assert.assertEquals(Long.MAX_VALUE - 2, response.getKeys()[1].get("longPKey"));
    Assert.assertEquals(Long.MAX_VALUE - 3, response.getKeys()[2].get("longPKey"));
    Assert.assertEquals(Long.MAX_VALUE - 4, response.getKeys()[3].get("longPKey"));
    Assert.assertEquals(Long.MAX_VALUE - 5, response.getKeys()[4].get("longPKey"));

    Assert.assertEquals("a", response.getKeys()[0].get("strPKey"));
    Assert.assertEquals("b", response.getKeys()[1].get("strPKey"));
    Assert.assertEquals("c", response.getKeys()[2].get("strPKey"));
    Assert.assertEquals("d", response.getKeys()[3].get("strPKey"));
    Assert.assertEquals("e", response.getKeys()[4].get("strPKey"));

    Assert.assertNotNull(UUID.fromString(response.getKeys()[0].get("uuidPKey").toString()));
    Assert.assertNotNull(UUID.fromString(response.getKeys()[1].get("uuidPKey").toString()));
    Assert.assertNotNull(UUID.fromString(response.getKeys()[2].get("uuidPKey").toString()));
    Assert.assertNotNull(UUID.fromString(response.getKeys()[3].get("uuidPKey").toString()));
    Assert.assertNotNull(UUID.fromString(response.getKeys()[4].get("uuidPKey").toString()));

    Assert.assertEquals("a", input.getStrPKey());
    Assert.assertEquals(1, input.getIntPKey());
    Assert.assertEquals(9223372036854775806L, input.getLongPKey());
    Assert.assertNotNull(input.getUuidPKey());
  }

  @Test
  public void testReplace() throws TigrisException, ExecutionException, InterruptedException {
    TigrisAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
    TigrisAsyncDatabase db1 = asyncClient.getDatabase("db1");
    List<DB1_C1> replacePayload = new ArrayList<>();
    replacePayload.add(new DB1_C1(1L, "testReplace1"));
    replacePayload.add(new DB1_C1(3L, "testReplace3"));
    replacePayload.add(new DB1_C1(4L, "testReplace4"));
    CompletableFuture<InsertOrReplaceResponse<DB1_C1>> response =
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
  public void testReplaceWithAutoGenerateId()
      throws TigrisException, ExecutionException, InterruptedException {
    TigrisAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
    TigrisAsyncDatabase db1 = asyncClient.getDatabase("autoGenerateTestDB");
    CompletableFuture<InsertOrReplaceResponse<AutoGeneratingPKeysModel>> responseCompletableFuture =
        db1.getCollection(AutoGeneratingPKeysModel.class)
            .insertOrReplace(
                Collections.singletonList(new AutoGeneratingPKeysModel("name-without-id")));
    InsertOrReplaceResponse<AutoGeneratingPKeysModel> response = responseCompletableFuture.get();
    Assert.assertNotNull(response);
    Assert.assertEquals(5, response.getGeneratedKeys().length);
    Assert.assertEquals(1, response.getGeneratedKeys()[0].get("intPKey"));
    Assert.assertEquals(2, response.getGeneratedKeys()[1].get("intPKey"));
    Assert.assertEquals(3, response.getGeneratedKeys()[2].get("intPKey"));
    Assert.assertEquals(4, response.getGeneratedKeys()[3].get("intPKey"));
    Assert.assertEquals(5, response.getGeneratedKeys()[4].get("intPKey"));

    Assert.assertEquals(Long.MAX_VALUE - 1, response.getGeneratedKeys()[0].get("longPKey"));
    Assert.assertEquals(Long.MAX_VALUE - 2, response.getGeneratedKeys()[1].get("longPKey"));
    Assert.assertEquals(Long.MAX_VALUE - 3, response.getGeneratedKeys()[2].get("longPKey"));
    Assert.assertEquals(Long.MAX_VALUE - 4, response.getGeneratedKeys()[3].get("longPKey"));
    Assert.assertEquals(Long.MAX_VALUE - 5, response.getGeneratedKeys()[4].get("longPKey"));

    Assert.assertEquals("a", response.getGeneratedKeys()[0].get("strPKey"));
    Assert.assertEquals("b", response.getGeneratedKeys()[1].get("strPKey"));
    Assert.assertEquals("c", response.getGeneratedKeys()[2].get("strPKey"));
    Assert.assertEquals("d", response.getGeneratedKeys()[3].get("strPKey"));
    Assert.assertEquals("e", response.getGeneratedKeys()[4].get("strPKey"));

    Assert.assertNotNull(
        UUID.fromString(response.getGeneratedKeys()[0].get("uuidPKey").toString()));
    Assert.assertNotNull(
        UUID.fromString(response.getGeneratedKeys()[1].get("uuidPKey").toString()));
    Assert.assertNotNull(
        UUID.fromString(response.getGeneratedKeys()[2].get("uuidPKey").toString()));
    Assert.assertNotNull(
        UUID.fromString(response.getGeneratedKeys()[3].get("uuidPKey").toString()));
    Assert.assertNotNull(
        UUID.fromString(response.getGeneratedKeys()[4].get("uuidPKey").toString()));

    Assert.assertEquals(1, response.getDocs().get(0).getIntPKey());
    Assert.assertEquals(9223372036854775806L, response.getDocs().get(0).getLongPKey());
    Assert.assertEquals("a", response.getDocs().get(0).getStrPKey());
    Assert.assertNotNull(response.getDocs().get(0).getUuidPKey());
  }

  @Test
  public void testDelete1() throws ExecutionException, InterruptedException {
    TigrisAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
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
    TigrisAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
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
  public void testUpdate() throws TigrisException, ExecutionException, InterruptedException {
    TigrisAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
    TigrisAsyncDatabase db1 = asyncClient.getDatabase("db1");
    CompletableFuture<UpdateResponse> response =
        db1.getCollection(DB1_C1.class)
            .update(
                Filters.eq("id", 1), UpdateFields.newBuilder().set("name", "new name 1").build());
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
  public void testDescribe()
      throws TigrisException, ExecutionException, InterruptedException, IOException {
    TigrisAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
    TigrisAsyncCollection<DB1_C5> coll = asyncClient.getDatabase("db1").getCollection(DB1_C5.class);
    Assert.assertEquals(
        "db1_c5", coll.describe(CollectionOptions.DEFAULT_INSTANCE).get().getName());
    Assert.assertEquals(
        "db1_c5", coll.describe(CollectionOptions.DEFAULT_INSTANCE).get().getSchema().getName());
    Assert.assertEquals(
        "{\"title\":\"db1_c5\",\"description\":\"This document records the details of user for tigris "
            + "store\",\"properties\":{\"id\":{\"description\":\"A unique identifier for the user\","
            + "\"type\":\"int\"},\"name\":{\"description\":\"Name of the user\",\"type\":\"string\"},"
            + "\"balance\":{\"description\":\"user balance in USD\",\"type\":\"double\"}},"
            + "\"primary_key\":[\"id\"]}",
        coll.describe(CollectionOptions.DEFAULT_INSTANCE).get().getSchema().getSchemaContent());
  }

  @Test
  public void testName() {
    TigrisAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
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
            new TigrisAsyncReader<DB1_C1>() {
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

  private static void inspectSearchDocs(TigrisAsyncDatabase db1, List<DB1_C1> expectedDocs) {
    Map<Long, DB1_C1> seenDocsMap = new HashMap<>();

    AtomicInteger errorCount = new AtomicInteger(0);
    AtomicBoolean completed = new AtomicBoolean(false);

    db1.getCollection(DB1_C1.class)
        .search(
            SearchRequest.newBuilder("name").build(),
            new TigrisAsyncSearchReader<DB1_C1>() {
              @Override
              public void onNext(SearchResult<DB1_C1> result) {
                result
                    .getHits()
                    .forEach(h -> seenDocsMap.put(h.getDocument().getId(), h.getDocument()));
                Assert.assertTrue(result.getFacets().containsKey("name"));
                FacetCountDistribution facet = result.getFacets().get("name");
                Assert.assertNotNull(facet.getCounts());
                Assert.assertEquals(expectedDocs.size(), facet.getStats().getCount());
                Assert.assertEquals(expectedDocs.size(), result.getMeta().getFound());
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
    Assert.assertTrue(completed.get());
    Assert.assertEquals(0, errorCount.get());
    expectedDocs.forEach(
        e -> {
          Assert.assertTrue(seenDocsMap.containsKey(e.getId()));
          Assert.assertEquals(e.getName(), seenDocsMap.get(e.getId()).getName());
        });
  }
}
