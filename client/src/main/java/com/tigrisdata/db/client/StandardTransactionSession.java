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
import com.tigrisdata.db.client.error.TigrisException;
import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.MetadataUtils;

/** Transactional session implementation */
class StandardTransactionSession implements TransactionSession {
  private final Api.TransactionCtx transactionCtx;
  private final String databaseName;
  private final TigrisGrpc.TigrisBlockingStub stub;

  StandardTransactionSession(
      String databaseName, Api.TransactionCtx transactionCtx, ManagedChannel managedChannel) {
    this.databaseName = databaseName;
    this.transactionCtx = transactionCtx;

    // prepare headers
    Metadata transactionHeaders = new Metadata();
    transactionHeaders.put(
        Metadata.Key.of(Constants.TRANSACTION_HEADER_ORIGIN_KEY, Metadata.ASCII_STRING_MARSHALLER),
        transactionCtx.getOrigin());
    transactionHeaders.put(
        Metadata.Key.of(Constants.TRANSACTION_HEADER_ID_KEY, Metadata.ASCII_STRING_MARSHALLER),
        transactionCtx.getId());
    // attach headers
    this.stub =
        TigrisGrpc.newBlockingStub(managedChannel)
            .withInterceptors(MetadataUtils.newAttachHeadersInterceptor(transactionHeaders));
  }

  @Override
  public CommitTransactionResponse commit() throws TigrisException {
    try {
      Api.CommitTransactionRequest commitTransactionRequest =
          Api.CommitTransactionRequest.newBuilder().setDb(databaseName).build();
      Api.CommitTransactionResponse response =
          TypeConverter.transactionAwareStub(stub, transactionCtx)
              .commitTransaction(commitTransactionRequest);
      return new CommitTransactionResponse(response.getStatus());
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisException(
          Constants.COMMIT_TRANSACTION_FAILED,
          TypeConverter.extractTigrisError(statusRuntimeException),
          statusRuntimeException);
    }
  }

  @Override
  public RollbackTransactionResponse rollback() throws TigrisException {
    try {
      Api.RollbackTransactionRequest rollbackTransactionRequest =
          Api.RollbackTransactionRequest.newBuilder().setDb(databaseName).build();
      Api.RollbackTransactionResponse response =
          TypeConverter.transactionAwareStub(stub, transactionCtx)
              .rollbackTransaction(rollbackTransactionRequest);
      return new RollbackTransactionResponse(response.getStatus());
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisException(
          Constants.ROLLBACK_TRANSACTION_FAILED,
          TypeConverter.extractTigrisError(statusRuntimeException),
          statusRuntimeException);
    }
  }

  Api.TransactionCtx getTransactionCtx() {
    return transactionCtx;
  }
}
