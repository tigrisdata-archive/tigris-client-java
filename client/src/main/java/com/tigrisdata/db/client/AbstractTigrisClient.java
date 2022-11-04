package com.tigrisdata.db.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tigrisdata.db.api.v1.grpc.Health;
import com.tigrisdata.db.api.v1.grpc.HealthAPIGrpc;
import com.tigrisdata.db.client.config.TigrisConfiguration;
import com.tigrisdata.tools.schema.core.ModelToJsonSchema;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

abstract class AbstractTigrisClient {
  protected final ManagedChannel channel;
  protected final ObjectMapper objectMapper;
  protected final ModelToJsonSchema modelToJsonSchema;
  protected final TigrisConfiguration configuration;

  private final HealthAPIGrpc.HealthAPIBlockingStub healthAPIBlockingStub;
  private static final Metadata.Key<String> USER_AGENT_KEY =
      Metadata.Key.of("user-agent", Metadata.ASCII_STRING_MARSHALLER);
  private static final Metadata.Key<String> CLIENT_VERSION_KEY =
      Metadata.Key.of("client-version", Metadata.ASCII_STRING_MARSHALLER);
  private static final Metadata.Key<String> INTENDED_DESTINATION_NAME =
      Metadata.Key.of("destination-name", Metadata.ASCII_STRING_MARSHALLER);
  private static final String USER_AGENT_VALUE = "tigris-client-java.grpc";
  private static final String CLIENT_VERSION_VALUE = "1.0";
  private static final Logger log = LoggerFactory.getLogger(AbstractTigrisClient.class);

  protected AbstractTigrisClient(
      TigrisConfiguration configuration, ModelToJsonSchema modelToJsonSchema) {

    ManagedChannelBuilder channelBuilder =
        ManagedChannelBuilder.forTarget(configuration.getServerURL())
            .intercept(MetadataUtils.newAttachHeadersInterceptor(getDefaultHeaders(configuration)));
    if (configuration.getNetwork().isUsePlainText()) {
      log.warn(
          "Client is configured to use plaintext communication. It is advised to not use plaintext "
              + "communication");
      channelBuilder.usePlaintext();
    }
    this.channel = channelBuilder.build();
    this.objectMapper = configuration.getObjectMapper();
    this.modelToJsonSchema = modelToJsonSchema;
    this.configuration = configuration;
    this.healthAPIBlockingStub = HealthAPIGrpc.newBlockingStub(channel);

    if (!configuration.getNetwork().isDisablePing()) {
      // setup periodic ping
      ScheduledExecutorService scheduledExecutorService =
          Executors.newSingleThreadScheduledExecutor(
              r -> {
                Thread thread = new Thread(r);
                thread.setName("tigris-ping");
                return thread;
              });
      scheduledExecutorService.scheduleWithFixedDelay(
          () -> {
            try {
              Health.HealthCheckResponse healthCheckResponse =
                  this.healthAPIBlockingStub.health(Health.HealthCheckInput.newBuilder().build());
              log.debug(healthCheckResponse.getResponse());
            } catch (Exception ex) {
              log.warn("Failed to ping", ex);
            }
          },
          configuration.getNetwork().getPingIntervalMs(),
          configuration.getNetwork().getPingIntervalMs(),
          TimeUnit.MILLISECONDS);
      // close the ping on shutdown
      Runtime.getRuntime().addShutdownHook(new Thread(scheduledExecutorService::shutdown));
    }
  }

  protected AbstractTigrisClient(
      TigrisConfiguration configuration,
      ManagedChannelBuilder<? extends ManagedChannelBuilder> managedChannelBuilder,
      ModelToJsonSchema modelToJsonSchema) {
    this.channel =
        managedChannelBuilder
            .intercept(MetadataUtils.newAttachHeadersInterceptor(getDefaultHeaders(configuration)))
            .build();
    this.objectMapper = configuration.getObjectMapper();
    this.modelToJsonSchema = modelToJsonSchema;
    this.configuration = configuration;
    this.healthAPIBlockingStub = HealthAPIGrpc.newBlockingStub(channel);
  }

  private static Metadata getDefaultHeaders(TigrisConfiguration configuration) {
    Metadata defaultHeaders = new Metadata();
    defaultHeaders.put(USER_AGENT_KEY, USER_AGENT_VALUE);
    defaultHeaders.put(CLIENT_VERSION_KEY, CLIENT_VERSION_VALUE);
    defaultHeaders.put(INTENDED_DESTINATION_NAME, configuration.getServerURL());
    return defaultHeaders;
  }
}
