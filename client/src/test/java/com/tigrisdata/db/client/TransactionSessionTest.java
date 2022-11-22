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
import com.tigrisdata.db.client.grpc.CookieSetterServerInterceptor;
import com.tigrisdata.db.client.grpc.TransactionTestTigrisService;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.Collections;

public class TransactionSessionTest {
  private static String SERVER_NAME;
  @ClassRule public static final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

  @BeforeClass
  public static void setup() throws Exception {
    SERVER_NAME = InProcessServerBuilder.generateName();
    grpcCleanup
        .register(
            InProcessServerBuilder.forName(SERVER_NAME)
                .directExecutor()
                .intercept(new CookieSetterServerInterceptor())
                .intercept(new ContextSettingServerInterceptor())
                .addService(new TransactionTestTigrisService())
                .build())
        .start();
  }

  @Test
  public void testValidSequences() throws TigrisException {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup, "db1");
    TigrisDatabase db1 = client.getDatabase();
    TransactionSession transactionSession = db1.beginTransaction(new TransactionOptions());
    transactionSession.commit();

    transactionSession = db1.beginTransaction(new TransactionOptions());
    transactionSession.rollback();
  }

  @Test
  public void testInvalidSequences1() throws TigrisException {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup, "db1");
    TigrisDatabase db1 = client.getDatabase();
    TransactionSession transactionSession = db1.beginTransaction(new TransactionOptions());
    transactionSession.commit();
    try {
      transactionSession.commit();
      Assert.fail("above is expected to fail");
    } catch (TigrisException ignore) {

    }
  }

  @Test
  public void testInvalidSequences2() throws TigrisException {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup, "db1");
    TigrisDatabase db1 = client.getDatabase();
    TransactionSession transactionSession = db1.beginTransaction(new TransactionOptions());
    transactionSession.rollback();
    try {
      transactionSession.rollback();
      Assert.fail("above is expected to fail");
    } catch (TigrisException ignore) {

    }
  }

  @Test
  public void testHeadersOnServer() throws TigrisException {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup, "db1");
    TigrisDatabase db1 = client.getDatabase();
    TransactionSession transactionSession = db1.beginTransaction(new TransactionOptions());
    TigrisCollection<DB1_C1> c1TigrisCollection = db1.getCollection(DB1_C1.class);

    c1TigrisCollection.insert(
        transactionSession, Collections.singletonList(new DB1_C1(1, "hello")));

    c1TigrisCollection.delete(transactionSession, Filters.eq("id", 0L));

    c1TigrisCollection.insert(transactionSession, Collections.singletonList(new DB1_C1(5, "foo")));

    c1TigrisCollection.update(
        transactionSession,
        Filters.eq("id", 5),
        UpdateFields.newBuilder().set("name", "new name").build());

    transactionSession.commit();
  }
}
