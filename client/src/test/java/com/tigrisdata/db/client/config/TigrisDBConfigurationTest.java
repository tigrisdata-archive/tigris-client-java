package com.tigrisdata.db.client.config;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.time.Duration;

public class TigrisDBConfigurationTest {
  @Test
  public void testDefault() {
    TigrisDBConfiguration defaultConfiguration = TigrisDBConfiguration.newBuilder().build();
    assertEquals("https://dev.tigrisdata.cloud/api/", defaultConfiguration.getBaseURL());

    assertEquals(Duration.ofSeconds(5), defaultConfiguration.getNetwork().getDeadline());
  }

  @Test
  public void testCustomization() {
    TigrisDBConfiguration customConfiguration =
        TigrisDBConfiguration.newBuilder()
            .withBaseURL("https://foo.bar")
            .withNetwork(
                TigrisDBConfiguration.NetworkConfig.newBuilder()
                    .withDeadline(Duration.ofSeconds(50))
                    .build())
            .build();

    assertEquals("https://foo.bar", customConfiguration.getBaseURL());
    assertEquals(Duration.ofSeconds(50), customConfiguration.getNetwork().getDeadline());
  }
}
