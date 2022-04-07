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
package com.tigrisdata.db.client.service;

import com.tigrisdata.db.api.v1.grpc.Api;
import com.tigrisdata.db.api.v1.grpc.TigrisDBGrpc;
import com.tigrisdata.db.client.error.TigrisDBException;
import com.tigrisdata.db.client.model.CollectionOptions;
import com.tigrisdata.db.client.model.CreateCollectionResponse;
import com.tigrisdata.db.client.model.TigrisCollectionType;
import com.tigrisdata.db.client.model.TigrisDBResponse;
import com.tigrisdata.db.client.model.TigrisDBSchema;
import static com.tigrisdata.db.client.model.TypeConverter.toCreateCollectionRequest;
import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.MetadataUtils;

import java.util.Optional;

public class StandardTransactionSession implements TransactionSession {
  private final Api.TransactionCtx transactionCtx;
  private final String databaseName;
  private final TigrisDBGrpc.TigrisDBBlockingStub stub;

  private static final String TRANSACTION_HEADER_ORIGIN_KEY = "tx-origin";
  private static final String TRANSACTION_HEADER_ID_KEY = "tx-id";

  StandardTransactionSession(
      String databaseName, Api.TransactionCtx transactionCtx, ManagedChannel managedChannel) {
    this.databaseName = databaseName;
    this.transactionCtx = transactionCtx;

    // prepare headers
    Metadata transactionHeaders = new Metadata();
    transactionHeaders.put(
        Metadata.Key.of(TRANSACTION_HEADER_ORIGIN_KEY, Metadata.ASCII_STRING_MARSHALLER),
        transactionCtx.getOrigin());
    transactionHeaders.put(
        Metadata.Key.of(TRANSACTION_HEADER_ID_KEY, Metadata.ASCII_STRING_MARSHALLER),
        transactionCtx.getId());
    // attach headers
    this.stub =
        TigrisDBGrpc.newBlockingStub(managedChannel)
            .withInterceptors(MetadataUtils.newAttachHeadersInterceptor(transactionHeaders));
  }

  @Override
  public <C extends TigrisCollectionType> TransactionTigrisCollection<C> getCollection(
      Class<C> collectionTypeClass) throws TigrisDBException {
    return new TransactionalTigrisCollection<>(
        databaseName, collectionTypeClass, stub, transactionCtx);
  }

  @Override
  public CreateCollectionResponse createCollection(
      TigrisDBSchema schema, CollectionOptions collectionOptions) throws TigrisDBException {
    try {
      Api.CreateCollectionResponse createCollectionResponse =
          stub.createCollection(
              toCreateCollectionRequest(
                  databaseName, schema, collectionOptions, Optional.of(transactionCtx)));
      return new CreateCollectionResponse(new TigrisDBResponse(createCollectionResponse.getMsg()));
    } catch (StatusRuntimeException ex) {
      throw new TigrisDBException("Failed to create collection in transactional session", ex);
    }
  }

  @Override
  public TigrisDBResponse commit() throws TigrisDBException {
    try {
      Api.CommitTransactionRequest commitTransactionRequest =
          Api.CommitTransactionRequest.newBuilder()
              .setDb(databaseName)
              .setTxCtx(transactionCtx)
              .build();
      stub.commitTransaction(commitTransactionRequest);
      // TODO actual status back
      return new TigrisDBResponse("committed");
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisDBException("Failed to commit transaction", statusRuntimeException);
    }
  }

  @Override
  public TigrisDBResponse rollback() throws TigrisDBException {
    try {
      Api.RollbackTransactionRequest rollbackTransactionRequest =
          Api.RollbackTransactionRequest.newBuilder()
              .setDb(databaseName)
              .setTxCtx(transactionCtx)
              .build();
      stub.rollbackTransaction(rollbackTransactionRequest);
      // TODO actual status back
      return new TigrisDBResponse("rolled back");
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisDBException("Failed to rollback transaction", statusRuntimeException);
    }
  }
}
