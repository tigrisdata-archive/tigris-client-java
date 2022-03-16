package com.tigrisdata.db.client.model;

import com.tigrisdata.db.api.v1.grpc.User;

public class WriteOptions {
  private User.TransactionCtx transactionCtx;

  public WriteOptions() {}

  public WriteOptions(User.TransactionCtx transactionCtx) {
    this.transactionCtx = transactionCtx;
  }

  public User.TransactionCtx getTransactionCtx() {
    return transactionCtx;
  }

  public void setTransactionCtx(User.TransactionCtx transactionCtx) {
    this.transactionCtx = transactionCtx;
  }
}
