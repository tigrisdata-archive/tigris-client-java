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
import com.tigrisdata.db.type.TigrisCollectionType;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;

abstract class AbstractTigrisCollection<T extends TigrisCollectionType> {

  protected final String databaseName;
  protected final String collectionName;
  protected final Class<T> collectionTypeClass;
  protected final TigrisGrpc.TigrisBlockingStub blockingStub;
  protected final ObjectMapper objectMapper;

  public AbstractTigrisCollection(
      String databaseName,
      Class<T> collectionTypeClass,
      TigrisGrpc.TigrisBlockingStub blockingStub,
      ObjectMapper objectMapper) {
    this.databaseName = databaseName;
    this.collectionTypeClass = collectionTypeClass;
    this.collectionName = Utilities.getCollectionName(collectionTypeClass);
    this.blockingStub = blockingStub;
    this.objectMapper = objectMapper;
  }

  protected TigrisGrpc.TigrisBlockingStub transactionAwareStub(
      TigrisGrpc.TigrisBlockingStub blockingStub, TransactionCtx transactionCtx) {
    // prepare headers
    Metadata transactionHeaders = new Metadata();
    transactionHeaders.put(
        Metadata.Key.of(Constants.TRANSACTION_HEADER_ORIGIN_KEY, Metadata.ASCII_STRING_MARSHALLER),
        transactionCtx.getOrigin());
    transactionHeaders.put(
        Metadata.Key.of(Constants.TRANSACTION_HEADER_ID_KEY, Metadata.ASCII_STRING_MARSHALLER),
        transactionCtx.getId());
    // attach headers
    return blockingStub.withInterceptors(
        MetadataUtils.newAttachHeadersInterceptor(transactionHeaders));
  }
}
