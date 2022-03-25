package com.tigrisdata.db.client.service;

import com.tigrisdata.db.client.error.TigrisDBException;
import com.tigrisdata.db.client.grpc.ContextSettingServerInterceptor;
import com.tigrisdata.db.client.grpc.TransactionTestUserService;
import com.tigrisdata.db.client.model.TigrisFilter;
import com.tigrisdata.db.client.model.TransactionOptions;
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
                .intercept(new ContextSettingServerInterceptor())
                .addService(new TransactionTestUserService())
                .build())
        .start();
  }

  @Test
  public void testValidSequences() throws TigrisDBException {
    TigrisDBClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    TransactionSession transactionSession = db1.beginTransaction(new TransactionOptions());
    transactionSession.commit();

    transactionSession = db1.beginTransaction(new TransactionOptions());
    transactionSession.rollback();
  }

  @Test
  public void testInvalidSequences1() throws TigrisDBException {
    TigrisDBClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    TransactionSession transactionSession = db1.beginTransaction(new TransactionOptions());
    transactionSession.commit();
    try {
      transactionSession.commit();
      Assert.fail("above is expected to fail");
    } catch (TigrisDBException ignore) {

    }
  }

  @Test
  public void testInvalidSequences2() throws TigrisDBException {
    TigrisDBClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    TransactionSession transactionSession = db1.beginTransaction(new TransactionOptions());
    transactionSession.rollback();
    try {
      transactionSession.rollback();
      Assert.fail("above is expected to fail");
    } catch (TigrisDBException ignore) {

    }
  }

  @Test
  public void testHeadersOnServer() throws TigrisDBException {
    TigrisDBClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    TransactionSession transactionSession = db1.beginTransaction(new TransactionOptions());
    TigrisCollection<C1> c1TigrisCollection = transactionSession.getCollection(C1.class);

    c1TigrisCollection.insert(Collections.singletonList(new C1("hello")));

    c1TigrisCollection.delete(
        new TigrisFilter() {
          @Override
          public String toString() {
            return "testHeadersOnServer";
          }
        });

    c1TigrisCollection.insert(Collections.singletonList(new C1("foo")));

    c1TigrisCollection.update(
        new TigrisFilter() {
          @Override
          public String toString() {
            return "testHeadersOnServer";
          }
        },
        Collections.emptyList());

    transactionSession.commit();
  }
}
