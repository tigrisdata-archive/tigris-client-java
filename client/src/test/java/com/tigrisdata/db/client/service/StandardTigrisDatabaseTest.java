package com.tigrisdata.db.client.service;

import com.tigrisdata.db.client.error.TigrisDBException;
import com.tigrisdata.db.client.grpc.TestUserService;
import com.tigrisdata.db.client.model.AlterCollectionResponse;
import com.tigrisdata.db.client.model.CollectionOptions;
import com.tigrisdata.db.client.model.CreateCollectionResponse;
import com.tigrisdata.db.client.model.DropCollectionResponse;
import com.tigrisdata.db.client.model.TigrisDBJSONSchema;
import com.tigrisdata.db.client.model.TransactionOptions;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.List;

public class StandardTigrisDatabaseTest {
  private static String SERVER_NAME;
  private static TestUserService TEST_USER_SERVICE;
  @ClassRule public static final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

  @BeforeClass
  public static void setup() throws Exception {
    SERVER_NAME = InProcessServerBuilder.generateName();
    TEST_USER_SERVICE = new TestUserService();
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
  public void testListCollections() throws TigrisDBException {
    TigrisDBClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    List<String> collections = db1.listCollections();
    Assert.assertEquals(5, collections.size());
    MatcherAssert.assertThat(
        collections, Matchers.containsInAnyOrder("db1_c0", "db1_c1", "db1_c2", "db1_c3", "db1_c4"));
  }

  @Test
  public void testCreateCollection() throws TigrisDBException {
    TigrisDBClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    CreateCollectionResponse response =
        db1.createCollection(
            "db1_c5",
            new TigrisDBJSONSchema("src/test/resources/test-schema.json"),
            new CollectionOptions());
    Assert.assertEquals("db1_c5 created", response.getTigrisDBResponse().getMessage());
    MatcherAssert.assertThat(
        db1.listCollections(),
        Matchers.containsInAnyOrder("db1_c0", "db1_c1", "db1_c2", "db1_c3", "db1_c4", "db1_c5"));
  }

  @Test
  public void testAlterCollection() throws TigrisDBException {
    TigrisDBClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    AlterCollectionResponse response =
        db1.alterCollection(
            "db1_c4",
            new TigrisDBJSONSchema("src/test/resources/test-schema.json"),
            new CollectionOptions());
    Assert.assertEquals("db1_c4 altered", response.getTigrisDBResponse().getMessage());
  }

  @Test
  public void testDropCollection() throws TigrisDBException {
    TigrisDBClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    DropCollectionResponse response = db1.dropCollection("db1_c3");
    Assert.assertEquals("db1_c3 dropped", response.getTigrisDBResponse().getMessage());
    MatcherAssert.assertThat(
        db1.listCollections(), Matchers.containsInAnyOrder("db1_c0", "db1_c1", "db1_c2", "db1_c4"));
  }

  @Test
  public void testGetCollection() {
    TigrisDBClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    TigrisCollection<C1> c1TigrisCollection = db1.getCollection(C1.class);
    Assert.assertEquals("c1", c1TigrisCollection.name());
  }

  @Test
  public void testBeginTransaction() throws TigrisDBException {
    TigrisDBClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    TransactionSession transactionSession = db1.beginTransaction(new TransactionOptions());
    transactionSession.commit();
  }

  @Test
  public void testCommitTransaction() throws TigrisDBException {
    TigrisDBClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    TransactionSession transactionSession = db1.beginTransaction(new TransactionOptions());
    transactionSession.commit();
  }

  @Test
  public void testRollbackTransaction() throws TigrisDBException {
    TigrisDBClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    TransactionSession transactionSession = db1.beginTransaction(new TransactionOptions());
    transactionSession.rollback();
  }

  @Test
  public void testName() {
    TigrisDBClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    Assert.assertEquals("db1", db1.name());
  }
}
