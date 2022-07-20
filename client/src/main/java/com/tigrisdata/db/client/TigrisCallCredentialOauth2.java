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
import com.tigrisdata.db.client.config.TigrisConfiguration;
import io.grpc.CallCredentials;
import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.Status;

import java.util.concurrent.Executor;

class TigrisCallCredentialOauth2 extends CallCredentials {
  static final Metadata.Key<String> AUTH_HEADER_KEY =
      Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER);

  private final TokenService tokenService;
  private static TigrisCallCredentialOauth2 INSTANCE;
  private static final Object lock = new Object();
  private static final String BEARER = "bearer";

  private TigrisCallCredentialOauth2(
      TigrisConfiguration tigrisConfiguration, ManagedChannel channel) {
    this.tokenService = new OAuth2TokenService(tigrisConfiguration.getoAuth2Config(), channel);
  }

  @VisibleForTesting
  TigrisCallCredentialOauth2(TokenService tokenService) {
    this.tokenService = tokenService;
  }

  /**
   * This method provides singleton/cached instance of {@link TigrisCallCredentialOauth2}. If you
   * call method with updated config values, you will still get the instance which was constructed
   * when first time this was constructed/called.
   *
   * @param configuration config
   * @return singleton instance of {@link TigrisCallCredentialOauth2}
   */
  static TigrisCallCredentialOauth2 getInstance(
      TigrisConfiguration configuration, ManagedChannel channel) {
    if (INSTANCE == null) {
      synchronized (lock) {
        if (INSTANCE == null) {
          INSTANCE = new TigrisCallCredentialOauth2(configuration, channel);
        }
      }
    }
    return INSTANCE;
  }

  @Override
  public void applyRequestMetadata(
      RequestInfo requestInfo, Executor executor, MetadataApplier metadataApplier) {
    executor.execute(
        () -> {
          try {
            Metadata headers = new Metadata();
            headers.put(AUTH_HEADER_KEY, BEARER + " " + tokenService.getAccessToken());
            metadataApplier.apply(headers);
          } catch (Throwable e) {
            metadataApplier.fail(Status.UNAUTHENTICATED.withCause(e));
          }
        });
  }

  @Override
  public void thisUsesUnstableApi() {
    // this is for gRPC's documentation purpose
  }
}
