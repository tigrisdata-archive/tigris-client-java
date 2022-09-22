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
package com.tigrisdata.db.client.config;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import org.junit.Test;

public class TigrisConfigurationTest {
  @Test
  public void testDefault() {
    TigrisConfiguration defaultConfiguration =
        TigrisConfiguration.newBuilder("some-host:443").build();
    assertEquals("some-host:443", defaultConfiguration.getServerURL());
    assertNotNull(defaultConfiguration.getObjectMapper());

    assertEquals(Duration.ofSeconds(5), defaultConfiguration.getNetwork().getDeadline());
    assertNull(defaultConfiguration.getAuthConfig());
  }

  @Test
  public void testCustomization() {
    ObjectMapper objectMapper = new ObjectMapper();
    TigrisConfiguration customConfiguration =
        TigrisConfiguration.newBuilder("some-host:443")
            .withNetwork(
                TigrisConfiguration.NetworkConfig.newBuilder()
                    .usePlainText()
                    .withDeadline(Duration.ofSeconds(50))
                    .build())
            .withAuthConfig(new TigrisConfiguration.AuthConfig("test-app-id", "test-app-secret"))
            .withObjectMapper(objectMapper)
            .build();

    assertEquals("some-host:443", customConfiguration.getServerURL());
    assertTrue(objectMapper == customConfiguration.getObjectMapper());

    assertTrue(customConfiguration.getNetwork().isUsePlainText());

    assertEquals(Duration.ofSeconds(50), customConfiguration.getNetwork().getDeadline());

    assertEquals("test-app-id", customConfiguration.getAuthConfig().getClientId());
    assertArrayEquals(
        "test-app-secret".toCharArray(), customConfiguration.getAuthConfig().getClientSecret());
  }

  @Test
  public void testEqualsAndHashCode() {
    ObjectMapper objectMapper = new ObjectMapper();
    TigrisConfiguration ob1 =
        TigrisConfiguration.newBuilder("some-host:443")
            .withNetwork(
                TigrisConfiguration.NetworkConfig.newBuilder()
                    .usePlainText()
                    .withDeadline(Duration.ofSeconds(50))
                    .build())
            .withAuthConfig(new TigrisConfiguration.AuthConfig("test-app-id", "test-app-secret"))
            .withObjectMapper(objectMapper)
            .build();
    TigrisConfiguration ob2 =
        TigrisConfiguration.newBuilder("some-host:443")
            .withNetwork(
                TigrisConfiguration.NetworkConfig.newBuilder()
                    .usePlainText()
                    .withDeadline(Duration.ofSeconds(50))
                    .build())
            .withAuthConfig(new TigrisConfiguration.AuthConfig("test-app-id", "test-app-secret"))
            .withObjectMapper(objectMapper)
            .build();
    assertEquals(ob1, ob2);
    assertEquals(ob2, ob1);
    assertEquals(ob1.hashCode(), ob2.hashCode());
  }
}
