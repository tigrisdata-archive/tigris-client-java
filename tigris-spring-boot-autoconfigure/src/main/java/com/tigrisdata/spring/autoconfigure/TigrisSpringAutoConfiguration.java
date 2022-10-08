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

import com.tigrisdata.db.client.StandardTigrisClient;
import com.tigrisdata.db.client.TigrisClient;
import com.tigrisdata.db.client.config.TigrisConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
@ConditionalOnProperty(
    prefix = "tigris.sync-client",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true)
@ConditionalOnClass(TigrisClient.class)
public class TigrisSpringAutoConfiguration {
  private static final Logger log = LoggerFactory.getLogger(TigrisSpringAutoConfiguration.class);

  @Bean
  @ConditionalOnMissingBean(TigrisConfiguration.class)
  public TigrisConfiguration tigrisConfiguration(
      @Value("${tigris.server.url}") String serverURL,
      @Value("${tigris.network.usePlainText:false}") boolean usePlainText,
      @Value("${tigris.auth.clientId:#{null}}") Optional<String> clientId,
      @Value("${tigris.auth.clientSecret:#{null}}") Optional<String> clientSecret) {
    log.debug("Initializing Tigris sync client configuration");
    return TigrisUtilities.tigrisConfiguration(serverURL, usePlainText, clientId, clientSecret);
  }

  @Bean
  public TigrisClient tigrisClient(TigrisConfiguration configuration) {
    log.debug("Initializing Tigris sync client");
    return StandardTigrisClient.getInstance(configuration);
  }
}
