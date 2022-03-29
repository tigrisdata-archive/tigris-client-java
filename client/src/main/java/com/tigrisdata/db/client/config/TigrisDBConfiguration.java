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

import java.time.Duration;

public class TigrisDBConfiguration {
  private final String baseURL;
  private final TigrisDBConfiguration.NetworkConfig network;

  private TigrisDBConfiguration(TigrisDBConfiguration.TigrisDBClientConfigurationBuilder builder) {
    this.baseURL = builder.baseURL;
    this.network = builder.network;
  }

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

  public static final class TigrisDBClientConfigurationBuilder {

    private final String baseURL;
    private TigrisDBConfiguration.NetworkConfig network;

    private TigrisDBClientConfigurationBuilder(String baseURL) {
      this.baseURL = baseURL;
      this.network = NetworkConfig.newBuilder().build();
    }

    public TigrisDBConfiguration.TigrisDBClientConfigurationBuilder withNetwork(
        TigrisDBConfiguration.NetworkConfig network) {
      this.network = network;
      return this;
    }

    public TigrisDBConfiguration build() {
      return new TigrisDBConfiguration(this);
    }
  }

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

    public static class NetworkConfigBuilder {
      public static final Duration DEFAULT_DEADLINE = Duration.ofSeconds(5);

      private Duration deadline;
      private boolean usePlainText;

      public NetworkConfigBuilder() {
        this.deadline = DEFAULT_DEADLINE;
        this.usePlainText = false;
      }

      public NetworkConfigBuilder withDeadline(Duration deadline) {
        this.deadline = deadline;
        return this;
      }

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
