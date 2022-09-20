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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tigrisdata.db.api.v1.grpc.Api;
import com.tigrisdata.db.api.v1.grpc.TigrisGrpc;
import static com.tigrisdata.db.client.Constants.JSON_SER_DE_ERROR;
import static com.tigrisdata.db.client.Constants.PUBLISH_FAILED;
import static com.tigrisdata.db.client.Constants.SUBSCRIBE_FAILED;
import static com.tigrisdata.db.client.TypeConverter.toPublishRequest;
import com.tigrisdata.db.client.config.TigrisConfiguration;
import com.tigrisdata.db.client.error.TigrisException;
import com.tigrisdata.db.type.TigrisMessageCollectionType;
import io.grpc.StatusRuntimeException;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

abstract class AbstractTigrisTopic<T extends TigrisMessageCollectionType> {

  protected final String databaseName;
  protected final String collectionName;
  protected final Class<T> topicTypeClass;
  protected final TigrisGrpc.TigrisBlockingStub blockingStub;
  protected final ObjectMapper objectMapper;
  protected final TigrisConfiguration configuration;

  public AbstractTigrisTopic(
      String databaseName,
      Class<T> topicTypeClass,
      TigrisGrpc.TigrisBlockingStub blockingStub,
      ObjectMapper objectMapper,
      TigrisConfiguration configuration) {
    this.databaseName = databaseName;
    this.topicTypeClass = topicTypeClass;
    this.collectionName = Utilities.getTopicName(topicTypeClass);
    this.blockingStub = blockingStub;
    this.objectMapper = objectMapper;
    this.configuration = configuration;
  }

  protected PublishResponse<T> publishInternal(List<T> messages) throws TigrisException {
    try {
      Api.PublishRequest publishRequest =
          toPublishRequest(databaseName, collectionName, messages, objectMapper);

      Api.PublishResponse response = blockingStub.publish(publishRequest);

      return new PublishResponse<>(
          response.getStatus(),
          response.getMetadata().getCreatedAt(),
          response.getMetadata().getUpdatedAt(),
          messages);
    } catch (JsonProcessingException ex) {
      throw new TigrisException(JSON_SER_DE_ERROR, ex);
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisException(
          PUBLISH_FAILED,
          TypeConverter.extractTigrisError(statusRuntimeException),
          statusRuntimeException);
    }
  }

  public Iterator<T> subscribeInternal() throws TigrisException {
    try {
      Api.SubscribeRequest subscribeRequest =
          Api.SubscribeRequest.newBuilder()
              .setDb(databaseName)
              .setCollection(collectionName)
              .build();
      Iterator<Api.SubscribeResponse> subscribeResponseIterator =
          blockingStub.subscribe(subscribeRequest);
      Function<Api.SubscribeResponse, T> converter =
          subscribeResponse -> {
            try {
              return objectMapper.readValue(
                  subscribeResponse.getMessage().toStringUtf8(), topicTypeClass);
            } catch (JsonProcessingException e) {
              throw new IllegalArgumentException("Failed to convert response to  the user type", e);
            }
          };
      return Utilities.transformIterator(subscribeResponseIterator, converter);
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisException(
          SUBSCRIBE_FAILED,
          TypeConverter.extractTigrisError(statusRuntimeException),
          statusRuntimeException);
    }
  }
}
