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
import com.tigrisdata.db.client.grpc.TestTigrisService;
import com.tigrisdata.db.client.search.FacetCountDistribution;
import com.tigrisdata.db.client.search.FacetFieldsQuery;
import com.tigrisdata.db.client.search.SearchRequest;
import com.tigrisdata.db.client.search.SearchRequestOptions;
import com.tigrisdata.db.client.search.SearchResult;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

public class StandardTigrisCollectionTest {

  private static String SERVER_NAME;
  private static final TestTigrisService TEST_USER_SERVICE = new TestTigrisService();
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
  public void testRead() throws TigrisException {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup, "db1");
    TigrisDatabase db1 = client.getDatabase();
    inspectDocs(
        db1,
        new DB1_C1(0L, "db1_c1_d0"),
        new DB1_C1(1L, "db1_c1_d1"),
        new DB1_C1(2L, "db1_c1_d2"),
        new DB1_C1(3L, "db1_c1_d3"),
        new DB1_C1(4L, "db1_c1_d4"));
  }

  @Test
  public void testReadAll() throws TigrisException {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup, "db1");
    TigrisDatabase db1 = client.getDatabase();
    inspectDocs(
        true,
        db1,
        new DB1_C1(0L, "db1_c1_d0"),
        new DB1_C1(1L, "db1_c1_d1"),
        new DB1_C1(2L, "db1_c1_d2"),
        new DB1_C1(3L, "db1_c1_d3"),
        new DB1_C1(4L, "db1_c1_d4"));
  }

  /*
   * Test exercises the flow of reading specific fields. The test gRPC service doesn't implement
   * full logic of filtering out fields. So the response is not inspected.
   *
   * <p>TODO: add inspection that request makes to server in right form
   */
  @Test
  public void testReadSpecificFields() throws TigrisException {

    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup, "db1");
    TigrisDatabase db1 = client.getDatabase();
    Iterator<DB1_C1> c1Iterator =
        db1.getCollection(DB1_C1.class)
            .read(Filters.eq("id", 0L), ReadFields.newBuilder().includeField("name").build());

    Assert.assertTrue(c1Iterator.hasNext());
    Assert.assertEquals(new DB1_C1(0, "db1_c1_d0"), c1Iterator.next());
    Assert.assertFalse(c1Iterator.hasNext());
  }

  @Test
  public void testReadOne() throws TigrisException {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup, "db1");
    TigrisDatabase db1 = client.getDatabase();
    Optional<DB1_C1> result = db1.getCollection(DB1_C1.class).readOne(Filters.eq("id", 0));
    Assert.assertTrue(result.isPresent());
    Assert.assertEquals(0L, result.get().getId());
    Assert.assertEquals("db1_c1_d0", result.get().getName());
  }

  @Test
  public void testReadOneEmpty() throws TigrisException {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup, "db1");
    TigrisDatabase db1 = client.getDatabase();
    Optional<DB1_C1> result = db1.getCollection(DB1_C1.class).readOne(Filters.eq("id", 100));
    Assert.assertFalse(result.isPresent());
  }

  @Test
  public void testSearch() throws TigrisException {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup, "db1");
    TigrisDatabase db1 = client.getDatabase();
    TigrisCollection<DB1_C1> collection = db1.getCollection(DB1_C1.class);
    SearchRequest searchRequest =
        SearchRequest.newBuilder()
            .withQuery("my search string")
            .withFacetQuery(FacetFieldsQuery.newBuilder().withField("name").build())
            .withIncludeFields("other_field")
            .withFilter(
                Filters.and(
                    Filters.eq("first_key", "first_value"), Filters.eq("some_key", "some value")))
            .build();

    Iterator<SearchResult<DB1_C1>> resultIterator = collection.search(searchRequest);
    Assert.assertNotNull(resultIterator);

    // validate client receives all results
    long recvdHits = 0;
    long foundResults = 0;
    while (resultIterator.hasNext()) {
      SearchResult<DB1_C1> result = resultIterator.next();
      validateSearchResult(result);
      recvdHits += result.getHits().size();
      foundResults = result.getMeta().getFound();
    }
    Assert.assertEquals(foundResults, recvdHits);
  }

  @Test
  public void testPaginatedSearch() throws TigrisException {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup, "db1");
    TigrisDatabase db1 = client.getDatabase();
    TigrisCollection<DB1_C1> collection = db1.getCollection(DB1_C1.class);
    SearchRequest searchRequest = SearchRequest.matchAll().build();
    SearchRequestOptions paginationParams = SearchRequestOptions.getDefault();

    Optional<SearchResult<DB1_C1>> result = collection.search(searchRequest, paginationParams);
    Assert.assertTrue(result.isPresent());
    validateSearchResult(result.get());
  }

  private void validateSearchResult(SearchResult<DB1_C1> result) {
    result
        .getHits()
        .forEach(
            hit -> {
              Assert.assertNotNull(hit.getDocument());
              Assert.assertEquals(DB1_C1.class, hit.getDocument().getClass());
            });
    Assert.assertTrue(result.getFacets().containsKey("name"));
    FacetCountDistribution facet = result.getFacets().get("name");
    Assert.assertNotNull(facet.getStats());
    Assert.assertNotNull(facet.getCounts());
  }

  @Test
  public void testInsert() throws TigrisException {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup, "db1");
    TigrisDatabase db1 = client.getDatabase();
    InsertResponse response =
        db1.getCollection(DB1_C1.class)
            .insert(
                Collections.singletonList(new DB1_C1(5L, "db1_c1_test-inserted")),
                new InsertRequestOptions());
    Assert.assertNotNull(response);
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
  public void testInsertAutoGenerateKeys() throws TigrisException {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup, "autoGenerateTestDB");
    TigrisDatabase db1 = client.getDatabase();
    AutoGeneratingPKeysModel input = new AutoGeneratingPKeysModel("name-without-id");
    InsertResponse<AutoGeneratingPKeysModel> response =
        db1.getCollection(AutoGeneratingPKeysModel.class).insert(input);
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

    Assert.assertEquals("a", input.getStrPKey());
    Assert.assertEquals(1, input.getIntPKey());
    Assert.assertEquals(9223372036854775806L, input.getLongPKey());
    Assert.assertNotNull(input.getUuidPKey());
  }

  @Test
  public void testInsertOne() throws TigrisException {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup, "db1");
    TigrisDatabase db1 = client.getDatabase();
    InsertResponse response =
        db1.getCollection(DB1_C1.class).insert(new DB1_C1(5L, "db1_c1_test-inserted"));
    Assert.assertNotNull(response);
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
  public void testInsertOrReplace() throws TigrisException {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup, "db1");
    TigrisDatabase db1 = client.getDatabase();
    List<DB1_C1> replacePayload = new ArrayList<>();
    replacePayload.add(new DB1_C1(1L, "testReplace1"));
    replacePayload.add(new DB1_C1(3L, "testReplace3"));
    replacePayload.add(new DB1_C1(4L, "testReplace4"));
    InsertOrReplaceResponse response =
        db1.getCollection(DB1_C1.class)
            .insertOrReplace(replacePayload, new InsertOrReplaceRequestOptions());
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
  public void testInsertOrReplaceOverloaded() throws TigrisException {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup, "db1");
    TigrisDatabase db1 = client.getDatabase();
    List<DB1_C1> replacePayload = new ArrayList<>();
    replacePayload.add(new DB1_C1(1L, "testReplace1"));
    replacePayload.add(new DB1_C1(3L, "testReplace3"));
    replacePayload.add(new DB1_C1(4L, "testReplace4"));
    InsertOrReplaceResponse response =
        db1.getCollection(DB1_C1.class).insertOrReplace(replacePayload);
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
  public void testInsertOrReplaceAutoGenerate() throws TigrisException {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup, "autoGenerateTestDB");
    TigrisDatabase db1 = client.getDatabase();
    InsertOrReplaceResponse<AutoGeneratingPKeysModel> response =
        db1.getCollection(AutoGeneratingPKeysModel.class)
            .insertOrReplace(
                Collections.singletonList(new AutoGeneratingPKeysModel("name-without-keys")));
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
    Assert.assertEquals("a", response.getDocs().get(0).getStrPKey());
    Assert.assertEquals(9223372036854775806L, response.getDocs().get(0).getLongPKey());
    Assert.assertNotNull(response.getDocs().get(0).getUuidPKey());
  }

  @Test
  public void testDelete() throws TigrisException {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup, "db1");
    TigrisDatabase db1 = client.getDatabase();
    DeleteResponse response =
        db1.getCollection(DB1_C1.class).delete(Filters.eq("id", 3), new DeleteRequestOptions());
    Assert.assertNotNull(response);
    inspectDocs(
        db1,
        new DB1_C1(0L, "db1_c1_d0"),
        new DB1_C1(1L, "db1_c1_d1"),
        new DB1_C1(2L, "db1_c1_d2"),
        new DB1_C1(4L, "db1_c1_d4"));

    response =
        db1.getCollection(DB1_C1.class).delete(Filters.eq("id", 1), new DeleteRequestOptions());
    Assert.assertNotNull(response);
    inspectDocs(
        db1, new DB1_C1(0L, "db1_c1_d0"), new DB1_C1(2L, "db1_c1_d2"), new DB1_C1(4L, "db1_c1_d4"));
  }

  @Test
  public void testDeleteOverloaded() throws TigrisException {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup, "db1");
    TigrisDatabase db1 = client.getDatabase();
    DeleteResponse response =
        db1.getCollection(DB1_C1.class).delete(Filters.eq("id", 3), new DeleteRequestOptions());
    Assert.assertNotNull(response);
    inspectDocs(
        db1,
        new DB1_C1(0L, "db1_c1_d0"),
        new DB1_C1(1L, "db1_c1_d1"),
        new DB1_C1(2L, "db1_c1_d2"),
        new DB1_C1(4L, "db1_c1_d4"));

    response = db1.getCollection(DB1_C1.class).delete(Filters.eq("id", 1));
    Assert.assertNotNull(response);
    inspectDocs(
        db1, new DB1_C1(0L, "db1_c1_d0"), new DB1_C1(2L, "db1_c1_d2"), new DB1_C1(4L, "db1_c1_d4"));
  }

  @Test
  public void testUpdate() throws TigrisException {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup, "db1");
    TigrisDatabase db1 = client.getDatabase();
    UpdateResponse response =
        db1.getCollection(DB1_C1.class)
            .update(
                Filters.eq("id", 1), UpdateFields.newBuilder().set("name", "new name 1").build());
    Assert.assertNotNull(response);

    inspectDocs(
        db1,
        new DB1_C1(0L, "db1_c1_d0"),
        new DB1_C1(1L, "new name 1"),
        new DB1_C1(2L, "db1_c1_d2"),
        new DB1_C1(3L, "db1_c1_d3"),
        new DB1_C1(4L, "db1_c1_d4"));
  }

  @Test
  public void testName() throws TigrisException {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup, "db1");
    TigrisDatabase db1 = client.getDatabase();
    TigrisCollection<DB1_C1> collection = db1.getCollection(DB1_C1.class);
    Assert.assertEquals("db1_c1", collection.name());
  }

  @Test
  public void testDescribe() throws TigrisException, IOException {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup, "db1");
    TigrisCollection<DB1_C5> collection = client.getDatabase().getCollection(DB1_C5.class);
    CollectionDescription description = collection.describe(CollectionOptions.DEFAULT_INSTANCE);
    Assert.assertEquals("db1_c5", description.getName());
    Assert.assertEquals(
        "{\"title\":\"db1_c5\",\"description\":\"This document records the details of user for tigris "
            + "store\",\"properties\":{\"id\":{\"description\":\"A unique identifier for the user\","
            + "\"type\":\"int\"},\"name\":{\"description\":\"Name of the user\",\"type\":\"string\"},"
            + "\"balance\":{\"description\":\"user balance in USD\",\"type\":\"double\"}},"
            + "\"primary_key\":[\"id\"]}",
        description.getSchema().getSchemaContent());
    Assert.assertNotNull(description.getMetadata());
  }

  private static void inspectDocs(boolean readAll, TigrisDatabase db1, DB1_C1... expectedDocs)
      throws TigrisException {
    Iterator<DB1_C1> c1Iterator = null;
    if (readAll) {
      c1Iterator = db1.getCollection(DB1_C1.class).readAll();
    } else {
      c1Iterator = db1.getCollection(DB1_C1.class).read(Filters.eq("ignore", "ignore"));
    }
    Assert.assertTrue(c1Iterator.hasNext());
    for (DB1_C1 expectedDoc : expectedDocs) {
      DB1_C1 db1_c1 = c1Iterator.next();
      Assert.assertEquals(expectedDoc.getId(), db1_c1.getId());
      Assert.assertEquals(expectedDoc.getName(), db1_c1.getName());
    }
    Assert.assertFalse(c1Iterator.hasNext());
  }

  private static void inspectDocs(TigrisDatabase db1, DB1_C1... expectedDocs)
      throws TigrisException {
    inspectDocs(false, db1, expectedDocs);
  }
}
