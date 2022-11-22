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
package com.tigrisdata.db.client;

import com.google.common.annotations.VisibleForTesting;
import com.tigrisdata.db.api.v1.grpc.ObservabilityGrpc;
import com.tigrisdata.db.api.v1.grpc.ObservabilityOuterClass;
import com.tigrisdata.db.api.v1.grpc.TigrisGrpc;
import static com.tigrisdata.db.client.Constants.DROP_DB_FAILED;
import static com.tigrisdata.db.client.TypeConverter.toServerMetadata;
import com.tigrisdata.db.client.config.TigrisConfiguration;
import com.tigrisdata.db.client.error.TigrisException;
import com.tigrisdata.tools.schema.core.StandardModelToTigrisJsonSchema;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

/** Client for Tigris */
public class StandardTigrisClient extends AbstractTigrisClient implements TigrisClient {

  private final TigrisGrpc.TigrisBlockingStub stub;
  private final ObservabilityGrpc.ObservabilityBlockingStub observabilityBlockingStub;

  private StandardTigrisClient(TigrisConfiguration configuration) {
    super(configuration, new StandardModelToTigrisJsonSchema());
    this.stub = Utilities.newBlockingStub(channel, configuration);
    this.observabilityBlockingStub = Utilities.newObservabilityBlockingStub(channel, configuration);
  }

  @VisibleForTesting
  StandardTigrisClient(
      TigrisConfiguration configuration,
      ManagedChannelBuilder<? extends ManagedChannelBuilder> managedChannelBuilder) {
    super(configuration, managedChannelBuilder, new StandardModelToTigrisJsonSchema());
    this.stub = Utilities.newBlockingStub(channel, configuration);
    this.observabilityBlockingStub = Utilities.newObservabilityBlockingStub(channel, configuration);
  }

  /**
   * Creates a new instance of @{@link StandardTigrisClient} with the given inputs
   *
   * @param tigrisConfiguration configuration
   * @return a new instance of @{@link StandardTigrisClient}
   */
  public static StandardTigrisClient getInstance(TigrisConfiguration tigrisConfiguration) {
    return new StandardTigrisClient(tigrisConfiguration);
  }

  @Override
  public TigrisDatabase getDatabase() {
    return new StandardTigrisDatabase(
        configuration.getProjectName(),
        stub,
        channel,
        objectMapper,
        modelToJsonSchema,
        configuration);
  }

  @Override
  public ServerMetadata getServerMetadata() throws TigrisException {
    try {
      ObservabilityOuterClass.GetInfoResponse apiResponse =
          observabilityBlockingStub.getInfo(
              ObservabilityOuterClass.GetInfoRequest.newBuilder().build());
      return toServerMetadata(apiResponse);
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisException(
          DROP_DB_FAILED,
          TypeConverter.extractTigrisError(statusRuntimeException),
          statusRuntimeException);
    }
  }

  @Override
  public void close() {
    channel.shutdown();
  }

  @VisibleForTesting
  ManagedChannel getChannel() {
    return channel;
  }
}
