package com.tigrisdata.db.client.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tigrisdata.db.client.model.AuthorizationToken;
import com.tigrisdata.db.client.config.TigrisDBConfiguration;
import com.tigrisdata.db.client.interceptors.AuthHeaderInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractTigrisDBClient {
  protected final ManagedChannel channel;
  protected final ObjectMapper objectMapper;
  private static final Metadata.Key<String> USER_AGENT_KEY =
      Metadata.Key.of("user-agent", Metadata.ASCII_STRING_MARSHALLER);
  private static final Metadata.Key<String> CLIENT_VERSION_KEY =
      Metadata.Key.of("client-version", Metadata.ASCII_STRING_MARSHALLER);
  private static final Metadata.Key<String> INTENDED_DESTINATION_NAME =
      Metadata.Key.of("destination-name", Metadata.ASCII_STRING_MARSHALLER);
  private static final String USER_AGENT_VALUE = "tigrisdb-client-java.grpc";
  private static final String CLIENT_VERSION_VALUE = "1.0";
  private static final Logger log = LoggerFactory.getLogger(AbstractTigrisDBClient.class);

  protected AbstractTigrisDBClient(
      TigrisDBConfiguration configuration, AuthorizationToken authorizationToken) {

    ManagedChannelBuilder channelBuilder =
        ManagedChannelBuilder.forTarget(configuration.getBaseURL())
            .intercept(new AuthHeaderInterceptor(authorizationToken))
            .intercept(MetadataUtils.newAttachHeadersInterceptor(getDefaultHeaders(configuration)));
    if (configuration.getNetwork().isUsePlainText()) {
      log.warn(
          "Client is configured to use plaintext communication. It is advised to not use plaintext communication");
      channelBuilder.usePlaintext();
    }
    this.channel = channelBuilder.build();
    this.objectMapper = configuration.getObjectMapper();
  }

  protected AbstractTigrisDBClient(
      AuthorizationToken authorizationToken,
      TigrisDBConfiguration configuration,
      ManagedChannelBuilder<? extends ManagedChannelBuilder> managedChannelBuilder) {
    this.channel =
        managedChannelBuilder
            .intercept(new AuthHeaderInterceptor(authorizationToken))
            .intercept(MetadataUtils.newAttachHeadersInterceptor(getDefaultHeaders(configuration)))
            .build();
    this.objectMapper = configuration.getObjectMapper();
  }

  private static Metadata getDefaultHeaders(TigrisDBConfiguration configuration) {
    Metadata defaultHeaders = new Metadata();
    defaultHeaders.put(USER_AGENT_KEY, USER_AGENT_VALUE);
    defaultHeaders.put(CLIENT_VERSION_KEY, CLIENT_VERSION_VALUE);
    defaultHeaders.put(INTENDED_DESTINATION_NAME, configuration.getBaseURL());
    return defaultHeaders;
  }
}
