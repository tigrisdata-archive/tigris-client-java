package com.tigrisdata.db.client.model;

import com.tigrisdata.db.api.v1.grpc.Api;

public class WriteOptions {
  private Api.TransactionCtx transactionCtx;

  public WriteOptions() {}

  public WriteOptions(Api.TransactionCtx transactionCtx) {
    this.transactionCtx = transactionCtx;
  }

  public Api.TransactionCtx getTransactionCtx() {
    return transactionCtx;
  }

  public void setTransactionCtx(Api.TransactionCtx transactionCtx) {
    this.transactionCtx = transactionCtx;
  }
}
