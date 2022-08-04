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

import com.tigrisdata.db.client.error.TigrisException;
import com.tigrisdata.db.client.grpc.FailingTestTigrisService;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.UUID;

public class StandardTigrisClientFailureTest {

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
  public void testListDatabases() {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    try {
      client.listDatabases(DatabaseOptions.DEFAULT_INSTANCE);
      Assert.fail("This must fail");
    } catch (TigrisException tigrisException) {
      Assert.assertEquals(
          "Failed to list database(s) Cause: FAILED_PRECONDITION: Test failure listDatabases",
          tigrisException.getMessage());
    }
  }

  @Test
  public void testCreateDatabase() {
    String dbName = UUID.randomUUID().toString();
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    try {
      client.createDatabaseIfNotExists(dbName);
      Assert.fail("This must fail");
    } catch (TigrisException tigrisException) {
      Assert.assertEquals(
          "Failed to create database Cause: FAILED_PRECONDITION: Test failure " + dbName,
          tigrisException.getMessage());
    }
  }

  @Test
  public void testDropDatabase() {
    String dbName = UUID.randomUUID().toString();
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    try {
      client.dropDatabase(dbName);
      Assert.fail("This must fail");
    } catch (TigrisException tigrisException) {
      Assert.assertEquals(
          "Failed to drop database Cause: FAILED_PRECONDITION: Test failure " + dbName,
          tigrisException.getMessage());
    }
  }
}
