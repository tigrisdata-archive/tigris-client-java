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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tigrisdata.db.api.v1.grpc.TigrisGrpc;
import com.tigrisdata.db.client.config.TigrisConfiguration;
import com.tigrisdata.db.client.error.TigrisException;
import com.tigrisdata.db.type.TigrisMessageCollectionType;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * An implementation of Tigris Topic
 *
 * @param <T> type of the Topic
 */
public class StandardTigrisTopic<T extends TigrisMessageCollectionType>
    extends AbstractTigrisTopic<T> implements TigrisTopic<T> {

  public StandardTigrisTopic(
      String databaseName,
      Class<T> topicTypeClass,
      TigrisGrpc.TigrisBlockingStub blockingStub,
      ObjectMapper objectMapper,
      TigrisConfiguration configuration) {
    super(databaseName, topicTypeClass, blockingStub, objectMapper, configuration);
  }

  @Override
  public PublishResponse<T> publish(T message) throws TigrisException {
    return this.publish(Collections.singletonList(message));
  }

  @Override
  public Iterator<T> subscribe() throws TigrisException {
    return super.subscribeInternal();
  }

  @Override
  public PublishResponse<T> publish(List<T> messages) throws TigrisException {
    return super.publishInternal(messages);
  }
}
