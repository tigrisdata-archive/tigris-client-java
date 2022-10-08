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
package com.tigrisdata.spring.autoconfigure;

import com.tigrisdata.db.client.TigrisAsyncClient;
import com.tigrisdata.db.client.TigrisClient;
import com.tigrisdata.db.client.config.TigrisConfiguration;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

public class TigrisSpringAutoConfigurationTest {

  @Test
  public void testSyncConfiguration() {
    new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(TigrisSpringAutoConfiguration.class))
        .withPropertyValues("tigris.server.url=localhost:8081")
        .run(
            context -> {
              Assertions.assertThat(context).hasSingleBean(TigrisClient.class);
              Assertions.assertThat(context).hasSingleBean(TigrisConfiguration.class);
            });
  }

  @Test
  public void testAsyncConfiguration() {
    new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(TigrisAsyncSpringAutoConfiguration.class))
        .withPropertyValues(
            "tigris.server.url=localhost:8081",
            "tigris.sync-client.enabled=false",
            "tigris.async-client.enabled=true")
        .run(
            context -> {
              Assertions.assertThat(context).hasSingleBean(TigrisAsyncClient.class);
              Assertions.assertThat(context).hasSingleBean(TigrisConfiguration.class);
            });
  }

  @Test
  public void testTigrisConfiguration() {
    new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(TigrisSpringAutoConfiguration.class))
        .withPropertyValues(
            "tigris.server.url=localhost:8081",
            "tigris.network.usePlainText=false",
            "tigris.auth.clientId=testClientId",
            "tigris.auth.clientSecret=testClientSecret")
        .run(
            context -> {
              Assertions.assertThat(context).hasSingleBean(TigrisConfiguration.class);
              TigrisConfiguration config = context.getBean(TigrisConfiguration.class);
              Assertions.assertThat(config.getServerURL()).isEqualTo("localhost:8081");
              Assertions.assertThat(config.getNetwork().isUsePlainText()).isEqualTo(false);
              Assertions.assertThat(config.getAuthConfig().getClientId()).isEqualTo("testClientId");
              Assertions.assertThat(config.getAuthConfig().getClientSecret())
                  .isEqualTo("testClientSecret".toCharArray());
            });
  }

  @Test
  public void testTigrisConfiguration1() {
    new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(TigrisSpringAutoConfiguration.class))
        .withPropertyValues("tigris.server.url=localhost:8081", "tigris.network.usePlainText=true")
        .run(
            context -> {
              Assertions.assertThat(context).hasSingleBean(TigrisConfiguration.class);
              TigrisConfiguration config = context.getBean(TigrisConfiguration.class);
              Assertions.assertThat(config.getServerURL()).isEqualTo("localhost:8081");
              Assertions.assertThat(config.getNetwork().isUsePlainText()).isEqualTo(true);
            });
  }

  @Test
  public void testAsyncTigrisConfiguration() {
    new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(TigrisAsyncSpringAutoConfiguration.class))
        .withPropertyValues(
            "tigris.async-client.enabled=true",
            "tigris.server.url=localhost:8081",
            "tigris.network.usePlainText=false",
            "tigris.auth.clientId=testClientId",
            "tigris.auth.clientSecret=testClientSecret")
        .run(
            context -> {
              Assertions.assertThat(context).hasSingleBean(TigrisConfiguration.class);
              TigrisConfiguration config = context.getBean(TigrisConfiguration.class);
              Assertions.assertThat(config.getServerURL()).isEqualTo("localhost:8081");
              Assertions.assertThat(config.getNetwork().isUsePlainText()).isEqualTo(false);
              Assertions.assertThat(config.getAuthConfig().getClientId()).isEqualTo("testClientId");
              Assertions.assertThat(config.getAuthConfig().getClientSecret())
                  .isEqualTo("testClientSecret".toCharArray());
            });
  }

  @Test
  public void testAsyncTigrisConfiguration1() {
    new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(TigrisAsyncSpringAutoConfiguration.class))
        .withPropertyValues(
            "tigris.async-client.enabled=true",
            "tigris.server.url=localhost:8081",
            "tigris.network.usePlainText=true")
        .run(
            context -> {
              Assertions.assertThat(context).hasSingleBean(TigrisConfiguration.class);
              TigrisConfiguration config = context.getBean(TigrisConfiguration.class);
              Assertions.assertThat(config.getServerURL()).isEqualTo("localhost:8081");
              Assertions.assertThat(config.getNetwork().isUsePlainText()).isEqualTo(true);
            });
  }
}
