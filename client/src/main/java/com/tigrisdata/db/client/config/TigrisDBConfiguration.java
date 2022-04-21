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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import java.time.Duration;

/** TigrisDB client configuration */
public class TigrisDBConfiguration {
  private final String baseURL;
  private final TigrisDBConfiguration.NetworkConfig network;
  private final ObjectMapper objectMapper;

  private TigrisDBConfiguration(TigrisDBConfiguration.TigrisDBClientConfigurationBuilder builder) {
    this.baseURL = builder.baseURL;
    this.network = builder.network;
    this.objectMapper = builder.objectMapper;
  }

  /**
   * Get the builder
   *
   * @param baseURL server base URL
   * @return an instance of {@link TigrisDBClientConfigurationBuilder}
   */
  public static TigrisDBConfiguration.TigrisDBClientConfigurationBuilder newBuilder(
      final String baseURL) {
    return new TigrisDBConfiguration.TigrisDBClientConfigurationBuilder(baseURL);
  }

  public String getBaseURL() {
    return this.baseURL;
  }

  public TigrisDBConfiguration.NetworkConfig getNetwork() {
    return this.network;
  }

  public ObjectMapper getObjectMapper() {
    return objectMapper;
  }

  /** Builder class for {@link TigrisDBConfiguration} */
  public static final class TigrisDBClientConfigurationBuilder {

    private final String baseURL;
    private TigrisDBConfiguration.NetworkConfig network;
    private ObjectMapper objectMapper;

    private TigrisDBClientConfigurationBuilder(String baseURL) {
      this.baseURL = baseURL;
      this.network = NetworkConfig.newBuilder().build();
      // configure ObjectMapper to work with immutable objects
      this.objectMapper =
          new ObjectMapper().registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES));
    }

    /**
     * This will customize {@link NetworkConfig}
     *
     * @param network network config
     * @return ongoing builder
     */
    public TigrisDBConfiguration.TigrisDBClientConfigurationBuilder withNetwork(
        TigrisDBConfiguration.NetworkConfig network) {
      this.network = network;
      return this;
    }

    /**
     * This will customize {@link ObjectMapper} instance used internally. It is highly recommended
     * to customize this instance with extra care
     *
     * @param objectMapper customized object mapper
     * @return ongoing builder
     */
    public TigrisDBConfiguration.TigrisDBClientConfigurationBuilder withObjectMapper(
        ObjectMapper objectMapper) {
      this.objectMapper = objectMapper;
      return this;
    }

    public TigrisDBConfiguration build() {
      return new TigrisDBConfiguration(this);
    }
  }

  /** TigrisDB network related configuration */
  public static class NetworkConfig {
    private final Duration deadline;
    private final boolean usePlainText;

    public static NetworkConfigBuilder newBuilder() {
      return new NetworkConfigBuilder();
    }

    private NetworkConfig(NetworkConfigBuilder networkConfigBuilder) {
      this.deadline = networkConfigBuilder.deadline;
      this.usePlainText = networkConfigBuilder.usePlainText;
    }

    public Duration getDeadline() {
      return this.deadline;
    }

    public boolean isUsePlainText() {
      return usePlainText;
    }

    /** Builder class for {@link NetworkConfig} */
    public static class NetworkConfigBuilder {
      public static final Duration DEFAULT_DEADLINE = Duration.ofSeconds(5);

      private Duration deadline;
      private boolean usePlainText;

      public NetworkConfigBuilder() {
        this.deadline = DEFAULT_DEADLINE;
        this.usePlainText = false;
      }

      /**
       * Specifies deadline for server calls
       *
       * @param deadline duration of time
       * @return ongoing builder
       */
      public NetworkConfigBuilder withDeadline(Duration deadline) {
        this.deadline = deadline;
        return this;
      }

      /**
       * Enables the plain text communication. It is highly recommended not to use this in
       * production.
       *
       * @return ongoing builder
       */
      public NetworkConfigBuilder usePlainText() {
        this.usePlainText = true;
        return this;
      }

      public NetworkConfig build() {
        return new NetworkConfig(this);
      }
    }
  }
}
