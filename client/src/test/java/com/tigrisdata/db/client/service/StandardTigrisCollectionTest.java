package com.tigrisdata.db.client.service;

import com.tigrisdata.db.client.error.TigrisDBException;
import com.tigrisdata.db.client.grpc.TestUserService;
import com.tigrisdata.db.client.model.DeleteRequestOptions;
import com.tigrisdata.db.client.model.DeleteResponse;
import com.tigrisdata.db.client.model.InsertRequestOptions;
import com.tigrisdata.db.client.model.InsertResponse;
import com.tigrisdata.db.client.model.ReadRequestOptions;
import com.tigrisdata.db.client.model.ReplaceRequestOptions;
import com.tigrisdata.db.client.model.ReplaceResponse;
import com.tigrisdata.db.client.model.TigrisFilter;
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
                new ReadRequestOptions());
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
    ReplaceResponse response =
        db1.getCollection(C1.class)
            .replace(Collections.singletonList(new C1("testInsert")), new ReplaceRequestOptions());
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
  public void testName() {
    TigrisDBClient client = TestUtils.getTestClient(serverName, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    TigrisCollection<C1> collection = db1.getCollection(C1.class);
    Assert.assertEquals("C1", collection.name());
  }
}
