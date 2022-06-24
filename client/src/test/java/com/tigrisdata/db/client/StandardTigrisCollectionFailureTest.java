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
import com.tigrisdata.db.client.grpc.FailingTestUserService;
import io.grpc.Status;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import java.util.Collections;
import java.util.UUID;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

public class StandardTigrisCollectionFailureTest {

  private static String SERVER_NAME;
  @ClassRule public static final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

  @BeforeClass
  public static void setup() throws Exception {
    SERVER_NAME = InProcessServerBuilder.generateName();

    grpcCleanup
        .register(
            InProcessServerBuilder.forName(SERVER_NAME)
                .directExecutor()
                .addService(new FailingTestUserService())
                .build())
        .start();
  }

  @Test
  public void testRead() {
    String dbName = UUID.randomUUID().toString();
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db = client.getDatabase(dbName);
    try {
      db.getCollection(DB1_C1.class).readOne(Filters.eq("id", 0));
      Assert.fail("This must fail");
    } catch (TigrisException tigrisException) {
      Assert.assertEquals(
          "Failed to read Cause: FAILED_PRECONDITION: Test failure " + dbName,
          tigrisException.getMessage());
      Assert.assertEquals(
          Status.fromThrowable(tigrisException.getCause()).getCode(),
          Status.FAILED_PRECONDITION.getCode());
    }
  }

  @Test
  public void testInsert() {
    String dbName = UUID.randomUUID().toString();
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db = client.getDatabase(dbName);
    try {
      db.getCollection(DB1_C1.class).insert(new DB1_C1(6, "name6"));
      Assert.fail("This must fail");
    } catch (TigrisException tigrisException) {
      Assert.assertEquals(
          "Failed to insert Cause: FAILED_PRECONDITION: Test failure " + dbName,
          tigrisException.getMessage());
      Assert.assertEquals(
          Status.fromThrowable(tigrisException.getCause()).getCode(),
          Status.FAILED_PRECONDITION.getCode());
    }
  }

  @Test
  public void testInsertOrReplace() {
    String dbName = UUID.randomUUID().toString();
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db = client.getDatabase(dbName);
    try {
      db.getCollection(DB1_C1.class)
          .insertOrReplace(Collections.singletonList(new DB1_C1(6, "name6")));
      Assert.fail("This must fail");
    } catch (TigrisException tigrisException) {
      Assert.assertEquals(
          "Failed to insertOrReplace Cause: FAILED_PRECONDITION: Test failure " + dbName,
          tigrisException.getMessage());
      Assert.assertEquals(
          Status.fromThrowable(tigrisException.getCause()).getCode(),
          Status.FAILED_PRECONDITION.getCode());
    }
  }

  @Test
  public void testDelete() {
    String dbName = UUID.randomUUID().toString();
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db = client.getDatabase(dbName);
    try {
      db.getCollection(DB1_C1.class).delete(Filters.eq("id", 0));
      Assert.fail("This must fail");
    } catch (TigrisException tigrisException) {
      Assert.assertEquals(
          "Failed to delete Cause: FAILED_PRECONDITION: Test failure " + dbName,
          tigrisException.getMessage());
      Assert.assertEquals(
          Status.fromThrowable(tigrisException.getCause()).getCode(),
          Status.FAILED_PRECONDITION.getCode());
    }
  }

  @Test
  public void testUpdate() {
    String dbName = UUID.randomUUID().toString();
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db = client.getDatabase(dbName);
    try {
      db.getCollection(DB1_C1.class)
          .update(
              Filters.eq("id", 0), UpdateFields.newBuilder().set("name", "updated_name").build());
      Assert.fail("This must fail");
    } catch (TigrisException tigrisException) {
      Assert.assertEquals(
          "Failed to update Cause: FAILED_PRECONDITION: Test failure " + dbName,
          tigrisException.getMessage());
      Assert.assertEquals(
          Status.fromThrowable(tigrisException.getCause()).getCode(),
          Status.FAILED_PRECONDITION.getCode());
    }
  }
}
