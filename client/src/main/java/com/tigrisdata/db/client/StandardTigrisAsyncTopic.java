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
import com.google.common.util.concurrent.ListenableFuture;
import com.tigrisdata.db.api.v1.grpc.Api;
import com.tigrisdata.db.api.v1.grpc.TigrisGrpc;
import com.tigrisdata.db.client.config.TigrisConfiguration;
import com.tigrisdata.db.client.error.TigrisException;
import com.tigrisdata.db.type.TigrisMessageCollectionType;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * An async implementation of Tigris Topic
 *
 * @param <T> type of the Topic
 */
public class StandardTigrisAsyncTopic<T extends TigrisMessageCollectionType>
    extends AbstractTigrisTopic<T> implements TigrisAsyncTopic<T> {
  private final Executor executor;
  private final TigrisGrpc.TigrisStub stub;
  private final TigrisGrpc.TigrisFutureStub futureStub;

  public StandardTigrisAsyncTopic(
      String databaseName,
      Class<T> topicTypeClass,
      TigrisGrpc.TigrisBlockingStub blockingStub,
      ManagedChannel channel,
      ObjectMapper objectMapper,
      TigrisConfiguration configuration,
      Executor executor,
      TigrisGrpc.TigrisFutureStub futureStub) {
    super(databaseName, topicTypeClass, blockingStub, objectMapper, configuration);
    this.executor = executor;
    this.stub = Utilities.newStub(channel, configuration);
    this.futureStub = futureStub;
  }

  @Override
  public CompletableFuture<PublishResponse<T>> publish(List<T> messages) throws TigrisException {
    try {
      Api.PublishRequest publishRequest =
          TypeConverter.toPublishRequest(databaseName, collectionName, messages, objectMapper);
      ListenableFuture<Api.PublishResponse> publishResponseListenableFuture =
          futureStub.publish(publishRequest);
      return Utilities.transformFuture(
          publishResponseListenableFuture,
          input ->
              new PublishResponse<>(
                  input.getStatus(),
                  input.getMetadata().getCreatedAt(),
                  input.getMetadata().getUpdatedAt(),
                  new ArrayList<>(messages)),
          executor,
          Constants.PUBLISH_FAILED);
    } catch (JsonProcessingException jsonProcessingException) {
      throw new TigrisException(Constants.JSON_SER_DE_ERROR, jsonProcessingException);
    }
  }

  @Override
  public CompletableFuture<PublishResponse<T>> publish(T message) throws TigrisException {
    return this.publish(Collections.singletonList(message));
  }

  @Override
  public void subscribe(TigrisAsyncMessageReader<T> reader) {
    Api.SubscribeRequest subscribeRequest =
        TypeConverter.toSubscribeRequest(databaseName, collectionName);
    stub.subscribe(
        subscribeRequest,
        new SubscribeResponseObserverAdapter<>(
            reader, topicTypeClass, objectMapper, Constants.SUBSCRIBE_FAILED));
  }

  static class SubscribeResponseObserverAdapter<T extends TigrisMessageCollectionType>
      implements StreamObserver<Api.SubscribeResponse> {

    private final TigrisAsyncMessageReader<T> reader;
    private final Class<T> documentCollectionTypeClass;
    private final ObjectMapper objectMapper;
    private final String errorMessage;

    public SubscribeResponseObserverAdapter(
        TigrisAsyncMessageReader<T> reader,
        Class<T> documentCollectionTypeClass,
        ObjectMapper objectMapper,
        String errorMessage) {
      this.reader = reader;
      this.documentCollectionTypeClass = documentCollectionTypeClass;
      this.objectMapper = objectMapper;
      this.errorMessage = errorMessage;
    }

    @Override
    public void onNext(Api.SubscribeResponse subscribeResponse) {
      try {
        T doc =
            objectMapper.readValue(
                subscribeResponse.getMessage().toStringUtf8(), documentCollectionTypeClass);
        reader.onNext(doc);
      } catch (JsonProcessingException ex) {
        reader.onError(new TigrisException(Constants.JSON_SER_DE_ERROR, ex));
      }
    }

    @Override
    public void onError(Throwable throwable) {
      if (throwable instanceof StatusRuntimeException) {
        reader.onError(
            new TigrisException(
                errorMessage,
                TypeConverter.extractTigrisError((StatusRuntimeException) throwable),
                throwable));
      } else {
        reader.onError(new TigrisException(errorMessage, throwable));
      }
    }

    @Override
    public void onCompleted() {
      reader.onCompleted();
    }
  }
}
