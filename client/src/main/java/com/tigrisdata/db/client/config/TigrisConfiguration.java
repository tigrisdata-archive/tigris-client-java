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
import java.util.Objects;

/** Tigris client configuration */
public class TigrisConfiguration {

  private final String serverURL;
  private final TigrisConfiguration.NetworkConfig network;
  private final TigrisConfiguration.OAuth2Config oAuth2Config;
  private final ObjectMapper objectMapper;

  private TigrisConfiguration(Builder builder) {
    this.serverURL = builder.baseURL;
    this.network = builder.network;
    this.objectMapper = builder.objectMapper;
    this.oAuth2Config = builder.oAuth2Config;
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

  public OAuth2Config getoAuth2Config() {
    return oAuth2Config;
  }

  /** Builder class for {@link TigrisConfiguration} */
  public static final class Builder {

    private final String baseURL;
    private TigrisConfiguration.NetworkConfig network;
    private TigrisConfiguration.OAuth2Config oAuth2Config;
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
      this.oAuth2Config = null;
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
     * This will enable and customize {@link OAuth2Config}.
     *
     * @param oAuth2Config oauth2 config
     * @return ongoing builder
     */
    public Builder withOAuth2(OAuth2Config oAuth2Config) {
      this.oAuth2Config = oAuth2Config;
      return this;
    }

    public TigrisConfiguration build() {
      return new TigrisConfiguration(this);
    }
  }

  public static class OAuth2Config {
    private final String refreshToken;

    public OAuth2Config(String refreshToken) {
      this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
      return refreshToken;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      OAuth2Config that = (OAuth2Config) o;
      return Objects.equals(refreshToken, that.refreshToken);
    }

    @Override
    public int hashCode() {
      return Objects.hash(refreshToken);
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
