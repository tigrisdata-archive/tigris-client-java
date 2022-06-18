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

import com.tigrisdata.db.api.v1.grpc.Api;
import com.tigrisdata.db.api.v1.grpc.TigrisGrpc;
import static com.tigrisdata.db.client.TypeConverter.toCreateCollectionRequest;
import com.tigrisdata.db.client.error.TigrisException;
import io.grpc.StatusRuntimeException;

abstract class AbstractTigrisDatabase {
  protected final String db;
  protected final TigrisGrpc.TigrisBlockingStub blockingStub;

  public AbstractTigrisDatabase(String db, TigrisGrpc.TigrisBlockingStub blockingStub) {
    this.db = db;
    this.blockingStub = blockingStub;
  }

  protected CreateOrUpdateCollectionsResponse createOrUpdateCollections(
      TransactionSession session, TigrisSchema schema, CollectionOptions collectionOptions)
      throws TigrisException {
    try {
      Api.CreateOrUpdateCollectionResponse response =
          TypeConverter.transactionAwareStub(
                  blockingStub, ((StandardTransactionSession) session).getTransactionCtx())
              .createOrUpdateCollection(toCreateCollectionRequest(db, schema, collectionOptions));
      return new CreateOrUpdateCollectionsResponse(response.getStatus(), response.getMessage());
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisException(
          Constants.CREATE_COLLECTIONS_FAILED,
          TypeConverter.extractTigrisError(statusRuntimeException),
          statusRuntimeException);
    }
  }
}
