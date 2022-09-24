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
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.tigrisdata.db.jackson.TigrisAnnotationIntrospector;

import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;

/** Tigris client configuration */
public class TigrisConfiguration {
  private final String serverURL;
  private final TigrisConfiguration.NetworkConfig network;
  private final TigrisConfiguration.AuthConfig authConfig;
  private final ObjectMapper objectMapper;

  private TigrisConfiguration(Builder builder) {
    this.serverURL = builder.baseURL;
    this.network = builder.network;
    this.objectMapper = builder.objectMapper;
    this.authConfig = builder.authConfig;
  }

  /**
   * Get the builder
   *
   * @param baseURL server base URL
   * @return an instance of {@link Builder}
   */
  public static Builder newBuilder(final String baseURL) {
    return new Builder(baseURL);
  }

  public String getServerURL() {
    return this.serverURL;
  }

  public TigrisConfiguration.NetworkConfig getNetwork() {
    return this.network;
  }

  public ObjectMapper getObjectMapper() {
    return objectMapper;
  }

  public AuthConfig getAuthConfig() {
    return authConfig;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TigrisConfiguration that = (TigrisConfiguration) o;
    return Objects.equals(serverURL, that.serverURL)
        && Objects.equals(network, that.network)
        && Objects.equals(authConfig, that.authConfig)
        && Objects.equals(objectMapper, that.objectMapper);
  }

  @Override
  public int hashCode() {
    return Objects.hash(serverURL, network, authConfig, objectMapper);
  }

  /** Builder class for {@link TigrisConfiguration} */
  public static final class Builder {

    private static final int DEFAULT_GRPC_PORT = 443;
    private String baseURL;
    private TigrisConfiguration.NetworkConfig network;
    private TigrisConfiguration.AuthConfig authConfig;
    private ObjectMapper objectMapper;

    private Builder(String baseURL) {
      this.baseURL = baseURL;
      this.network = NetworkConfig.newBuilder().build();
      // configure ObjectMapper to work with immutable objects
      this.objectMapper =
          new ObjectMapper()
              .setAnnotationIntrospector(new TigrisAnnotationIntrospector())
              .registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES))
              .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      this.authConfig = null;
    }

    /**
     * This will customize {@link NetworkConfig}
     *
     * @param network network config
     * @return ongoing builder
     */
    public Builder withNetwork(TigrisConfiguration.NetworkConfig network) {
      this.network = network;
      return this;
    }

    /**
     * This will customize {@link ObjectMapper} instance used internally. It is highly recommended
     * customizing this instance with extra care
     *
     * @param objectMapper customized object mapper
     * @return ongoing builder
     */
    public Builder withObjectMapper(ObjectMapper objectMapper) {
      this.objectMapper = objectMapper;
      return this;
    }

    /**
     * This will enable and customize {@link AuthConfig}.
     *
     * @param authConfig auth config
     * @return ongoing builder
     */
    public Builder withAuthConfig(AuthConfig authConfig) {
      this.authConfig = authConfig;
      return this;
    }

    public TigrisConfiguration build() {
      if (!this.baseURL.contains(":")) {
        this.baseURL = this.baseURL + ":" + DEFAULT_GRPC_PORT;
      }
      return new TigrisConfiguration(this);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Builder builder = (Builder) o;
      return Objects.equals(baseURL, builder.baseURL)
          && Objects.equals(network, builder.network)
          && Objects.equals(authConfig, builder.authConfig)
          && Objects.equals(objectMapper, builder.objectMapper);
    }

    @Override
    public int hashCode() {
      return Objects.hash(baseURL, network, authConfig, objectMapper);
    }
  }

  public static class AuthConfig {
    private final String clientId;
    private final char[] clientSecret;

    public AuthConfig(String clientId, String clientSecret) {
      this.clientId = clientId;
      this.clientSecret = clientSecret.toCharArray();
    }

    public String getClientId() {
      return clientId;
    }

    public char[] getClientSecret() {
      return clientSecret;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      AuthConfig that = (AuthConfig) o;
      return Objects.equals(clientId, that.clientId)
          && Arrays.equals(clientSecret, that.clientSecret);
    }

    @Override
    public int hashCode() {
      int result = Objects.hash(clientId);
      result = 31 * result + Arrays.hashCode(clientSecret);
      return result;
    }
  }

  /** Tigris network related configuration */
  public static class NetworkConfig {

    private final Duration deadline;
    private final boolean usePlainText;

    public static Builder newBuilder() {
      return new Builder();
    }

    private NetworkConfig(Builder builder) {
      this.deadline = builder.deadline;
      this.usePlainText = builder.usePlainText;
    }

    public Duration getDeadline() {
      return this.deadline;
    }

    public boolean isUsePlainText() {
      return usePlainText;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      NetworkConfig that = (NetworkConfig) o;
      return usePlainText == that.usePlainText && Objects.equals(deadline, that.deadline);
    }

    @Override
    public int hashCode() {
      return Objects.hash(deadline, usePlainText);
    }

    /** Builder class for {@link NetworkConfig} */
    public static class Builder {

      public static final Duration DEFAULT_DEADLINE = Duration.ofSeconds(5);

      private Duration deadline;
      private boolean usePlainText;

      public Builder() {
        this.deadline = DEFAULT_DEADLINE;
        this.usePlainText = false;
      }

      /**
       * Specifies deadline for server calls
       *
       * @param deadline duration of time
       * @return ongoing builder
       */
      public Builder withDeadline(Duration deadline) {
        this.deadline = deadline;
        return this;
      }

      /**
       * Enables the plain text communication. It is highly recommended not to use this in
       * production.
       *
       * @return ongoing builder
       */
      public Builder usePlainText() {
        this.usePlainText = true;
        return this;
      }

      public NetworkConfig build() {
        return new NetworkConfig(this);
      }
    }
  }
}
