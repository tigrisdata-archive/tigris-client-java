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
import com.tigrisdata.db.client.grpc.FailingTestTigrisService;
import com.tigrisdata.db.client.search.SearchRequest;
import com.tigrisdata.db.client.search.SearchResult;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

public class StandardTigrisAsyncCollectionFailureTest {

  private static String SERVER_NAME;
  @ClassRule public static final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

  @BeforeClass
  public static void setup() throws Exception {
    SERVER_NAME = InProcessServerBuilder.generateName();

    grpcCleanup
        .register(
            InProcessServerBuilder.forName(SERVER_NAME)
                .directExecutor()
                .addService(new FailingTestTigrisService())
                .build())
        .start();
  }

  @Test
  public void testRead() throws ExecutionException, InterruptedException {
    TigrisAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup, "db1");
    readAndExpectError(asyncClient.getDatabase());
  }

  @Test
  public void testSearch() throws ExecutionException, InterruptedException {
    TigrisAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup, "db1");
    searchAndExpectError(asyncClient.getDatabase());
  }

  @Test
  public void testReadOne() throws ExecutionException, InterruptedException {
    String dbName = UUID.randomUUID().toString();

    TigrisAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup, dbName);
    TigrisAsyncDatabase db1 = asyncClient.getDatabase();
    CompletableFuture<Optional<DB1_C1>> result =
        db1.getCollection(DB1_C1.class).readOne(Filters.eq("id", 1L));
    try {
      result.join();
      Assert.fail("This must fail");
    } catch (Exception ex) {
      Assert.assertEquals(
          "com.tigrisdata.db.client.error.TigrisException: Failed to read Cause: FAILED_PRECONDITION: Test failure "
              + dbName,
          ex.getMessage());
    }
    Assert.assertTrue(result.isCompletedExceptionally());
  }

  @Test
  public void testInsert() throws TigrisException, ExecutionException, InterruptedException {
    String dbName = UUID.randomUUID().toString();

    TigrisAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup, dbName);
    CompletableFuture<InsertResponse<DB1_C1>> result =
        asyncClient.getDatabase().getCollection(DB1_C1.class).insert(new DB1_C1(1, "msg"));
    try {
      result.join();
      Assert.fail("This must fail");
    } catch (Exception ex) {
      Assert.assertEquals(
          "com.tigrisdata.db.client.error.TigrisException: Failed to insert Cause: FAILED_PRECONDITION: Test "
              + "failure "
              + dbName,
          ex.getMessage());
    }
    Assert.assertTrue(result.isCompletedExceptionally());
  }

  @Test
  public void testInsertAndReplace()
      throws TigrisException, ExecutionException, InterruptedException {
    String dbName = UUID.randomUUID().toString();
    TigrisAsyncClient asyncClient = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup, dbName);
    CompletableFuture<InsertOrReplaceResponse<DB1_C1>> result =
        asyncClient
            .getDatabase()
            .getCollection(DB1_C1.class)
            .insertOrReplace(Collections.singletonList(new DB1_C1(1, "msg")));
    try {
      result.join();
      Assert.fail("This must fail");
    } catch (Exception ex) {
      Assert.assertEquals(
          "com.tigrisdata.db.client.error.TigrisException: Failed to insertOrReplace Cause: FAILED_PRECONDITION: "
              + "Test failure "
              + dbName,
          ex.getMessage());
    }
    Assert.assertTrue(result.isCompletedExceptionally());
  }

  private static void readAndExpectError(TigrisAsyncDatabase db1) {
    AtomicInteger errorCount = new AtomicInteger();
    AtomicBoolean completed = new AtomicBoolean(false);
    db1.getCollection(DB1_C1.class)
        .read(
            Filters.eq("ignore", "ignore"),
            ReadFields.all(),
            new TigrisAsyncReader<DB1_C1>() {
              @Override
              public void onNext(DB1_C1 document) {
                Assert.fail("This must fail");
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

    // there must not be any errors
    Assert.assertEquals(1, errorCount.get());
  }

  private static void searchAndExpectError(TigrisAsyncDatabase db1) {
    AtomicInteger errorCount = new AtomicInteger();
    AtomicBoolean completed = new AtomicBoolean(false);
    db1.getCollection(DB1_C1.class)
        .search(
            SearchRequest.newBuilder().build(),
            new TigrisAsyncSearchReader<DB1_C1>() {
              @Override
              public void onNext(SearchResult<DB1_C1> result) {
                Assert.fail("This must fail");
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

    Assert.assertEquals(1, errorCount.get());
  }
}
