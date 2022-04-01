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
import com.tigrisdata.db.client.model.Fields;
import com.tigrisdata.db.client.model.InsertOrReplaceRequestOptions;
import com.tigrisdata.db.client.model.InsertOrReplaceResponse;
import com.tigrisdata.db.client.model.InsertRequestOptions;
import com.tigrisdata.db.client.model.InsertResponse;
import com.tigrisdata.db.client.model.TigrisFilter;
import com.tigrisdata.db.client.model.UpdateResponse;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Collections;
import java.util.Iterator;

@FixMethodOrder(MethodSorters.JVM)
public class StandardTigrisCollectionTest {

  private static String serverName;
  @ClassRule public static final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

  @BeforeClass
  public static void setup() throws Exception {
    serverName = InProcessServerBuilder.generateName();
    grpcCleanup
        .register(
            InProcessServerBuilder.forName(serverName)
                .directExecutor()
                .addService(new TestUserService())
                .build())
        .start();
  }

  @Test
  public void testRead() throws TigrisDBException {
    TigrisDBClient client = TestUtils.getTestClient(serverName, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    Iterator<C1> c1s =
        db1.getCollection(C1.class)
            .read(
                new TigrisFilter() {
                  @Override
                  public String toString() {
                    return "read-filter";
                  }
                },
                Collections.emptyList());
    Assert.assertTrue(c1s.hasNext());
    Assert.assertEquals("read-filter", c1s.next().getMsg());
    Assert.assertFalse(c1s.hasNext());
  }

  @Test
  public void testInsert() throws TigrisDBException {
    TigrisDBClient client = TestUtils.getTestClient(serverName, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    InsertResponse response =
        db1.getCollection(C1.class)
            .insert(Collections.singletonList(new C1("testInsert")), new InsertRequestOptions());
    Assert.assertNotNull(response);
  }

  @Test
  public void testReplace() throws TigrisDBException {
    TigrisDBClient client = TestUtils.getTestClient(serverName, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    InsertOrReplaceResponse response =
        db1.getCollection(C1.class)
            .insertOrReplace(
                Collections.singletonList(new C1("testReplace")),
                new InsertOrReplaceRequestOptions());
    Assert.assertNotNull(response);
  }

  @Test
  public void testDelete() throws TigrisDBException {
    TigrisDBClient client = TestUtils.getTestClient(serverName, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    DeleteResponse response =
        db1.getCollection(C1.class)
            .delete(
                new TigrisFilter() {
                  @Override
                  public String toString() {
                    return "delete-filter";
                  }
                },
                new DeleteRequestOptions());
    Assert.assertNotNull(response);
  }

  @Test
  public void testUpdate() throws TigrisDBException {
    TigrisDBClient client = TestUtils.getTestClient(serverName, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    UpdateResponse updateResponse =
        db1.getCollection(C1.class)
            .update(
                new TigrisFilter() {
                  @Override
                  public String toString() {
                    return "delete-filter";
                  }
                },
                Collections.singletonList(Fields.integerField("intfield", 456)));
    Assert.assertEquals(123, updateResponse.getUpdatedRecordCount());
  }

  @Test
  public void testName() {
    TigrisDBClient client = TestUtils.getTestClient(serverName, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    TigrisCollection<C1> collection = db1.getCollection(C1.class);
    Assert.assertEquals("c1", collection.name());
  }
}
