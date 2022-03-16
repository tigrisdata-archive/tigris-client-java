package com.tigrisdata.db.client.model;

public class InsertRequestOptions {
  private WriteOptions writeOptions;

  public InsertRequestOptions() {}

  public InsertRequestOptions(WriteOptions writeOptions) {
    this.writeOptions = writeOptions;
  }

  public WriteOptions getWriteOptions() {
    return writeOptions;
  }

  public void setWriteOptions(WriteOptions writeOptions) {
    this.writeOptions = writeOptions;
  }
}
