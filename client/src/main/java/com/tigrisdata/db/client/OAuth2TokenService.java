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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tigrisdata.db.api.v1.grpc.AuthGrpc;
import com.tigrisdata.db.api.v1.grpc.AuthOuterClass;
import com.tigrisdata.db.client.config.TigrisConfiguration;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

class OAuth2TokenService implements TokenService {
  private final AtomicReference<String> accessToken;
  private final AtomicReference<String> refreshToken;
  private final AtomicLong lastRefreshed;
  private final AtomicLong nextRefreshTime;
  private final Object lock;
  private final AuthGrpc.AuthBlockingStub authBlockingStub;

  private static final Logger log = LoggerFactory.getLogger(OAuth2TokenService.class);

  OAuth2TokenService(TigrisConfiguration.OAuth2Config oAuth2Config, ManagedChannel channel) {
    this.authBlockingStub = AuthGrpc.newBlockingStub(channel);
    this.refreshToken = new AtomicReference<>(oAuth2Config.getRefreshToken());
    this.accessToken = new AtomicReference<>("");
    this.lock = new Object();
    this.lastRefreshed = new AtomicLong(0L);
    this.nextRefreshTime = new AtomicLong(0L);
  }

  @Override
  public String getAccessToken() {
    if (shouldRefresh()) {
      // refresh
      refresh();
    }
    return accessToken.get();
  }

  private boolean shouldRefresh() {
    return (accessToken.get() == null || accessToken.get().isEmpty())
        || (nextRefreshTime.get() != 0L && nextRefreshTime.get() <= System.currentTimeMillis());
  }

  private void refresh() {
    synchronized (lock) {
      // if it was never refreshed OR nextRefreshTime has arrived
      if (shouldRefresh()) {
        try {
          AuthOuterClass.GetAccessTokenResponse response =
              authBlockingStub.getAccessToken(
                  AuthOuterClass.GetAccessTokenRequest.newBuilder()
                      .setRefreshToken(refreshToken.get())
                      .build());

          accessToken.set(response.getAccessToken());
          refreshToken.set(response.getRefreshToken());
          lastRefreshed.set(System.currentTimeMillis());
          // refresh before 5 minute of expiry
          nextRefreshTime.set(
              getExpirationTimeInMillis(accessToken.get()) - TimeUnit.MINUTES.toMillis(5));
        } catch (IOException ioException) {
          log.error("Failed to refresh access token", ioException);
        }
      }
    }
  }

  private static long getExpirationTimeInMillis(String accessToken) throws JsonProcessingException {
    String[] parts = accessToken.split("\\.");
    // decode the payload
    String decodedPayload = new String(Base64.getDecoder().decode(parts[1]));
    JsonNode jsonNode = new ObjectMapper().readTree(decodedPayload);
    return jsonNode.get("exp").asLong() * 1000L;
  }
}
