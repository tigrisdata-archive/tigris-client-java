package com.tigrisdata.db.client.service;

import com.tigrisdata.db.client.auth.JTWAuthorization;
import com.tigrisdata.db.client.config.TigrisDBConfiguration;
import com.tigrisdata.db.client.error.TigrisDBException;
import com.tigrisdata.db.client.grpc.TestUserService;
import com.tigrisdata.db.client.model.DatabaseOptions;
import com.tigrisdata.db.client.model.TigrisDBResponse;
import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.List;

public class StandardTigrisDBClientTest {

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
  public void testGetDatabase() {
    TigrisDBClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    Assert.assertEquals("db1", db1.name());
  }

  @Test
  public void testListDatabases() throws TigrisDBException {
    TigrisDBClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    List<TigrisDatabase> databases = client.listDatabases(new DatabaseOptions());

    Assert.assertEquals(3, databases.size());
    // note: equals ignores stub and channel so avoid passing them
    MatcherAssert.assertThat(
        databases,
        Matchers.containsInAnyOrder(
            new StandardTigrisDatabase("db1", null, null),
            new StandardTigrisDatabase("db2", null, null),
            new StandardTigrisDatabase("db3", null, null)));
  }

  @Test
  public void testCreateDatabase() throws TigrisDBException {
    TigrisDBClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDBResponse response = client.createDatabase("db4", new DatabaseOptions());
    Assert.assertEquals("db4 created", response.getMessage());
    // 4th db created
    Assert.assertEquals(4, client.listDatabases(new DatabaseOptions()).size());
  }

  @Test
  public void testDropDatabase() throws TigrisDBException {
    TigrisDBClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDBResponse response = client.dropDatabase("db2", new DatabaseOptions());
    Assert.assertEquals("db2 dropped", response.getMessage());
    // 4th db created
    Assert.assertEquals(2, client.listDatabases(new DatabaseOptions()).size());
    MatcherAssert.assertThat(
        client.listDatabases(new DatabaseOptions()),
        Matchers.containsInAnyOrder(
            new StandardTigrisDatabase("db1", null, null),
            new StandardTigrisDatabase("db3", null, null)));
  }

  @Test
  public void testClose() throws Exception {
    ManagedChannelBuilder mockedChannelBuilder = Mockito.mock(ManagedChannelBuilder.class);
    Mockito.when(mockedChannelBuilder.intercept(ArgumentMatchers.any(ClientInterceptor.class)))
        .thenReturn(mockedChannelBuilder);
    ManagedChannel mockedChannel = Mockito.mock(ManagedChannel.class);
    Mockito.when(mockedChannelBuilder.build()).thenReturn(mockedChannel);

    TigrisDBClient client =
        new StandardTigrisDBClient(
            TigrisDBConfiguration.newBuilder().build(),
            new JTWAuthorization(""),
            mockedChannelBuilder);
    client.close();
    Mockito.verify(mockedChannel, Mockito.times(1)).shutdown();
  }
}
