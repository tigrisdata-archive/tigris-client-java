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

import com.tigrisdata.db.client.collection.collection2.DB1_C3;
import com.tigrisdata.db.client.error.TigrisException;
import com.tigrisdata.db.client.grpc.FailingTestTigrisService;
import io.grpc.Status;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.UUID;

public class StandardTigrisDatabaseFailureTest {
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
  public void testListCollections() {
    String dbName = UUID.randomUUID().toString();
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup, dbName);
    TigrisDatabase db1 = client.getDatabase();
    try {
      db1.listCollections();
      Assert.fail("This must fail");
    } catch (TigrisException tigrisException) {
      Assert.assertEquals(
          "Failed to list collection(s) Cause: FAILED_PRECONDITION: Test failure " + dbName,
          tigrisException.getMessage());
      Assert.assertEquals(
          Status.fromThrowable(tigrisException.getCause()).getCode(),
          Status.FAILED_PRECONDITION.getCode());
    }
  }

  @Test
  public void testDropCollection() {
    String dbName = UUID.randomUUID().toString();
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup, dbName);
    TigrisDatabase db1 = client.getDatabase();
    try {
      db1.dropCollection(DB1_C3.class);
      Assert.fail("This must fail");
    } catch (TigrisException tigrisException) {
      Assert.assertEquals(
          "Failed to drop collection Cause: FAILED_PRECONDITION: Test failure " + dbName,
          tigrisException.getMessage());
      Assert.assertEquals(
          Status.fromThrowable(tigrisException.getCause()).getCode(),
          Status.FAILED_PRECONDITION.getCode());
    }
  }

  @Test
  public void testBeginTransaction() {
    String dbName = UUID.randomUUID().toString();
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup, dbName);
    TigrisDatabase db = client.getDatabase();
    try {
      db.beginTransaction(TransactionOptions.DEFAULT_INSTANCE);
      Assert.fail("This must fail");
    } catch (TigrisException tigrisException) {
      Assert.assertEquals(
          "Failed to begin transaction Cause: FAILED_PRECONDITION: Test failure " + dbName,
          tigrisException.getMessage());
      Assert.assertEquals(
          Status.fromThrowable(tigrisException.getCause()).getCode(),
          Status.FAILED_PRECONDITION.getCode());
    }
  }

  @Test
  public void testCommitTransaction() {
    String dbName = FailingTestTigrisService.ALLOW_BEGIN_TRANSACTION_DB_NAME;
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup, dbName);
    TigrisDatabase db = client.getDatabase();
    try {
      TransactionSession transactionSession =
          db.beginTransaction(TransactionOptions.DEFAULT_INSTANCE);
      Assert.assertNotNull(transactionSession);
      transactionSession.commit();
      Assert.fail("commit() must fail");
    } catch (TigrisException tigrisException) {
      Assert.assertEquals(
          "Failed to commit transaction Cause: FAILED_PRECONDITION: Test failure " + dbName,
          tigrisException.getMessage());
      Assert.assertEquals(
          Status.fromThrowable(tigrisException.getCause()).getCode(),
          Status.FAILED_PRECONDITION.getCode());
    }
  }

  @Test
  public void testRollbackTransaction() throws TigrisException {
    String dbName = FailingTestTigrisService.ALLOW_BEGIN_TRANSACTION_DB_NAME;
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup, dbName);
    TigrisDatabase db = client.getDatabase();
    try {
      TransactionSession transactionSession =
          db.beginTransaction(TransactionOptions.DEFAULT_INSTANCE);
      Assert.assertNotNull(transactionSession);
      transactionSession.rollback();
      Assert.fail("rollback() must fail");
    } catch (TigrisException tigrisException) {
      Assert.assertEquals(
          "Failed to rollback transaction Cause: FAILED_PRECONDITION: Test failure " + dbName,
          tigrisException.getMessage());
      Assert.assertEquals(
          Status.fromThrowable(tigrisException.getCause()).getCode(),
          Status.FAILED_PRECONDITION.getCode());
    }
  }
}
