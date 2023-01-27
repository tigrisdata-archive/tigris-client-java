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
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.tigrisdata.db.jackson.TigrisAnnotationIntrospector;

import java.time.Duration;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

/** Tigris client configuration */
public class TigrisConfiguration {
  private final String serverURL;
  private final String projectName;
  private final TigrisConfiguration.NetworkConfig network;
  private final TigrisConfiguration.AuthConfig authConfig;
  private final ObjectMapper objectMapper;

  private TigrisConfiguration(Builder builder) {
    this.serverURL = builder.baseURL;
    this.projectName = builder.projectName;
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
  public static Builder newBuilder(final String baseURL, final String proejctName) {
    return new Builder(baseURL, proejctName);
  }

  public String getServerURL() {
    return this.serverURL;
  }

  public String getProjectName() {
    return projectName;
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
    private String projectName;
    private TigrisConfiguration.NetworkConfig network;
    private TigrisConfiguration.AuthConfig authConfig;
    private boolean keepIsPrefixForBooleanFields;
    private ObjectMapper objectMapper;

    private Builder(String baseURL, String projectName) {
      this.baseURL = baseURL;
      this.projectName = projectName;
      this.network = NetworkConfig.newBuilder().build();
      // configure ObjectMapper to work with immutable objects
      this.objectMapper =
          new ObjectMapper()
              .setAnnotationIntrospector(new TigrisAnnotationIntrospector())
              .registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES))
              .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
              .setDateFormat(
                  new StdDateFormat()
                      .withColonInTimeZone(true)
                      .withLocale(Locale.US)
                      .withTimeZone(TimeZone.getTimeZone("UTC")))
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
     * When the collection model contains the boolean field with is prefix. For example <code>
     *     public class Employee {
     *         private boolean isActive;
     *
     *     }
     * </code> For Java standard accessor will generate like this <code>
     *     public class Employee {
     *         private boolean isActive;
     *
     *         public boolean isActive(){ return this.isActive;}
     *         public void setActive(boolean active){ this.isActive = active;}
     *     }
     * </code> This makes the JSON serialization of document of collection Employee as follows
     * <code>{"active": false}</code> The intention is to keep the name `isActive`. This is common
     * issue when Kotlin data classes are in use and default getters/setter are generated by
     * compiler.
     *
     * <p>To keep `is` prefix intact for such case use this property and the JSON serde will contain
     * `is` prefix for field For above example the JSON document will be like this <code>
     * {"isActive": false}</code>
     *
     * @return ongoing builder
     */
    public Builder keepIsPrefixForBooleanFields() {
      this.keepIsPrefixForBooleanFields = true;
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
      if (this.keepIsPrefixForBooleanFields) {
        this.objectMapper.setPropertyNamingStrategy(
            new PropertyNamingStrategy() {
              @Override
              public String nameForGetterMethod(
                  MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
                if (method.hasReturnType()
                    && (method.getRawReturnType() == Boolean.class
                        || method.getRawReturnType() == boolean.class)
                    && method.getName().startsWith("is")) {
                  return method.getName();
                }
                return super.nameForGetterMethod(config, method, defaultName);
              }
            });
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
    private final boolean disablePing;
    private final long pingIntervalMs;

    public static Builder newBuilder() {
      return new Builder();
    }

    private NetworkConfig(Builder builder) {
      this.deadline = builder.deadline;
      this.usePlainText = builder.usePlainText;
      this.disablePing = builder.disablePing;
      this.pingIntervalMs = builder.pingIntervalMs;
    }

    public boolean isDisablePing() {
      return disablePing;
    }

    public long getPingIntervalMs() {
      return pingIntervalMs;
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
      public static final boolean DEFAULT_DISABLE_PING = false;
      public static final long DEFAULT_PING_INTERVAL_MS = 300_000l;
      public static final boolean DEFAULT_USE_PLAIN_TEXT = false;

      private Duration deadline;
      private boolean usePlainText;

      private boolean disablePing;
      private long pingIntervalMs;

      public Builder() {
        this.deadline = DEFAULT_DEADLINE;
        this.usePlainText = DEFAULT_USE_PLAIN_TEXT;
        this.disablePing = DEFAULT_DISABLE_PING;
        this.pingIntervalMs = DEFAULT_PING_INTERVAL_MS;
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

      /**
       * Tigris SDK will periodically ping Tigris server. This is required to keep the connection
       * active in case of the user workload is heavy pub/sub dependent with no messages exchanged
       * for long time.
       *
       * <p>This method will help user disable these pings.
       *
       * @return ongoing builder
       */
      public Builder disablePing() {
        this.disablePing = true;
        return this;
      }

      /**
       * Allows user to customize ping interval. defaults to 300_000 (i.e. 5min)
       *
       * @return ongoing builder
       */
      public Builder withPingIntervalMs(long pingIntervalMs) {
        this.pingIntervalMs = pingIntervalMs;
        return this;
      }

      public NetworkConfig build() {
        return new NetworkConfig(this);
      }
    }
  }
}
