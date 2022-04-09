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

import com.fasterxml.jackson.databind.ObjectMapper;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.time.Duration;

public class TigrisDBConfigurationTest {
  @Test
  public void testDefault() {
    TigrisDBConfiguration defaultConfiguration =
        TigrisDBConfiguration.newBuilder("some-host:443").build();
    assertEquals("some-host:443", defaultConfiguration.getBaseURL());
    assertNotNull(defaultConfiguration.getObjectMapper());

    assertEquals(Duration.ofSeconds(5), defaultConfiguration.getNetwork().getDeadline());
  }

  @Test
  public void testCustomization() {
    ObjectMapper objectMapper = new ObjectMapper();
    TigrisDBConfiguration customConfiguration =
        TigrisDBConfiguration.newBuilder("some-host:443")
            .withNetwork(
                TigrisDBConfiguration.NetworkConfig.newBuilder()
                    .usePlainText()
                    .withDeadline(Duration.ofSeconds(50))
                    .build())
            .withObjectMapper(objectMapper)
            .build();

    assertEquals("some-host:443", customConfiguration.getBaseURL());
    assertTrue(objectMapper == customConfiguration.getObjectMapper());

    assertTrue(customConfiguration.getNetwork().isUsePlainText());

    assertEquals(Duration.ofSeconds(50), customConfiguration.getNetwork().getDeadline());
  }
}
