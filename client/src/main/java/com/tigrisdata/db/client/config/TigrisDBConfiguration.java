package com.tigrisdata.db.client.config;

import java.time.Duration;

public class TigrisDBConfiguration {
  private final String baseURL;
  private final TigrisDBConfiguration.NetworkConfig network;

  public TigrisDBConfiguration(TigrisDBConfiguration.TigrisDBClientConfigurationBuilder builder) {
    this.baseURL = builder.baseURL;
    this.network = builder.network;
  }

  public static TigrisDBConfiguration.TigrisDBClientConfigurationBuilder newBuilder() {
    return new TigrisDBConfiguration.TigrisDBClientConfigurationBuilder();
  }

  public String getBaseURL() {
    return this.baseURL;
  }

  public TigrisDBConfiguration.NetworkConfig getNetwork() {
    return this.network;
  }

  public static final class TigrisDBClientConfigurationBuilder {

    private String baseURL;
    private TigrisDBConfiguration.NetworkConfig network;
    public static final String DEFAULT_BASE_URL = "https://dev.tigrisdata.cloud/api/";

    private TigrisDBClientConfigurationBuilder() {
      this.baseURL = DEFAULT_BASE_URL;
      this.network = NetworkConfig.newBuilder().build();
    }

    public TigrisDBConfiguration.TigrisDBClientConfigurationBuilder withBaseURL(String baseURL) {
      this.baseURL = baseURL;
      return this;
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

    public static NetworkConfigBuilder newBuilder() {
      return new NetworkConfigBuilder();
    }

    private NetworkConfig(NetworkConfigBuilder networkConfigBuilder) {
      this.deadline = networkConfigBuilder.deadline;
    }

    public Duration getDeadline() {
      return this.deadline;
    }

    public static class NetworkConfigBuilder {
      public static final Duration DEFAULT_DEADLINE = Duration.ofSeconds(5);

      private Duration deadline;

      public NetworkConfigBuilder() {
        this.deadline = DEFAULT_DEADLINE;
      }

      public NetworkConfigBuilder withDeadline(Duration deadline) {
        this.deadline = deadline;
        return this;
      }

      public NetworkConfig build() {
        return new NetworkConfig(this);
      }
    }
  }
}
