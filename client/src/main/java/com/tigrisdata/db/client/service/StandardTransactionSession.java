package com.tigrisdata.db.client.service;

import com.tigrisdata.db.api.v1.grpc.Api;
import com.tigrisdata.db.api.v1.grpc.TigrisDBGrpc;
import com.tigrisdata.db.client.error.TigrisDBException;
import com.tigrisdata.db.client.model.TigrisCollectionType;
import com.tigrisdata.db.client.model.TigrisDBResponse;
import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.MetadataUtils;

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

  // visible for testing
  Api.TransactionCtx getTransactionCtx() {
    return transactionCtx;
  }
}
