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

import com.tigrisdata.db.client.StandardTigrisAsyncClient;
import com.tigrisdata.db.client.TigrisAsyncClient;
import com.tigrisdata.db.client.TigrisAsyncDatabase;
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
@ConditionalOnProperty(prefix = "tigris.async-client", name = "enabled", havingValue = "true")
@ConditionalOnClass(TigrisAsyncClient.class)
public class TigrisAsyncSpringAutoConfiguration {
  private static final Logger log =
      LoggerFactory.getLogger(TigrisAsyncSpringAutoConfiguration.class);

  @Bean
  @ConditionalOnMissingBean(TigrisConfiguration.class)
  public TigrisConfiguration tigrisConfiguration(
      @Value("${tigris.server.url}") String serverURL,
      @Value("${tigris.network.usePlainText:false}") boolean usePlainText,
      @Value("${tigris.auth.clientId:#{null}}") Optional<String> clientId,
      @Value("${tigris.auth.clientSecret:#{null}}") Optional<String> clientSecret) {
    log.debug("Initializing Tigris async client configuration");
    return TigrisUtilities.tigrisConfiguration(serverURL, usePlainText, clientId, clientSecret);
  }

  @Bean
  public TigrisAsyncClient tigrisAsyncClient(TigrisConfiguration configuration) {
    log.debug("Initializing Tigris async client");
    return StandardTigrisAsyncClient.getInstance(configuration);
  }

  @Bean
  public TigrisAsyncDatabase tigrisPrimaryAsyncDatabase(
      TigrisAsyncClient tigrisAsyncClient, @Value("${tigris.db.name}") String dbName) {
    return tigrisAsyncClient.getDatabase(dbName);
  }

  @Bean
  @ConditionalOnProperty(
      prefix = "tigris.aync-client.initializer",
      name = "enabled",
      havingValue = "true",
      matchIfMissing = true)
  public TigrisAsyncInitializer tigrisAsyncInitializer(
      @Value("${tigris.db.name}") String dbName,
      @Value("${tigris.db.collectionClasses}") String collectionClasses,
      TigrisAsyncClient tigrisAsyncClient) {
    return new TigrisAsyncInitializer(dbName, collectionClasses, tigrisAsyncClient);
  }
}
