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
import com.tigrisdata.db.client.grpc.AuthEnabledTestTigrisService;
import com.tigrisdata.db.client.grpc.ContextSettingServerInterceptor;
import com.tigrisdata.db.client.grpc.FailingTestTigrisService;
import com.tigrisdata.db.client.grpc.TestAuthService;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.UUID;

public class AuthorizationTigrisClientTest {
  private static String SERVER_NAME;
  @ClassRule public static final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();
  private static AuthEnabledTestTigrisService TEST_USER_SERVICE;

  @BeforeClass
  public static void setup() throws Exception {
    SERVER_NAME = InProcessServerBuilder.generateName();
    TEST_USER_SERVICE = new AuthEnabledTestTigrisService();
    grpcCleanup
        .register(
            InProcessServerBuilder.forName(SERVER_NAME)
                .directExecutor()
                .intercept(new ContextSettingServerInterceptor())
                .addService(TEST_USER_SERVICE)
                .addService(new TestAuthService())
                .build())
        .start();
  }

  @After
  public void reset() {
    TEST_USER_SERVICE.reset();
  }

  @Test
  public void testListCollections() throws Exception {
    TigrisClient client = TestUtils.getTestAuthEnabledClient(SERVER_NAME, grpcCleanup, "db1");
    TigrisDatabase db1 = client.getDatabase();
    db1.listCollections();
  }

  @Test
  public void testDropCollection() throws Exception {
    TigrisClient client = TestUtils.getTestAuthEnabledClient(SERVER_NAME, grpcCleanup, "db1");
    TigrisDatabase db1 = client.getDatabase();
    db1.dropCollection(DB1_C3.class);
  }

  @Test
  public void testBeginTransaction() throws Exception {
    String dbName = UUID.randomUUID().toString();

    TigrisClient client = TestUtils.getTestAuthEnabledClient(SERVER_NAME, grpcCleanup, "db1");
    TigrisDatabase db = client.getDatabase();
    db.beginTransaction(TransactionOptions.DEFAULT_INSTANCE);
  }

  @Test
  public void testCommitTransaction() throws Exception {
    String dbName = FailingTestTigrisService.ALLOW_BEGIN_TRANSACTION_DB_NAME;
    TigrisClient client = TestUtils.getTestAuthEnabledClient(SERVER_NAME, grpcCleanup, "db1");
    TigrisDatabase db = client.getDatabase();
    TransactionSession transactionSession =
        db.beginTransaction(TransactionOptions.DEFAULT_INSTANCE);
    Assert.assertNotNull(transactionSession);
    transactionSession.commit();
  }

  @Test
  public void testRollbackTransaction() throws Exception {
    String dbName = FailingTestTigrisService.ALLOW_BEGIN_TRANSACTION_DB_NAME;
    TigrisClient client = TestUtils.getTestAuthEnabledClient(SERVER_NAME, grpcCleanup, "db1");
    TigrisDatabase db = client.getDatabase();
    TransactionSession transactionSession =
        db.beginTransaction(TransactionOptions.DEFAULT_INSTANCE);
    Assert.assertNotNull(transactionSession);
    transactionSession.rollback();
  }
}
