package com.tigrisdata.db.client.model;

public class DeleteRequestOptions {
  private WriteOptions writeOptions;

  public DeleteRequestOptions() {}

  public DeleteRequestOptions(WriteOptions writeOptions) {
    this.writeOptions = writeOptions;
  }

  public WriteOptions getWriteOptions() {
    return writeOptions;
  }

  public void setWriteOptions(WriteOptions writeOptions) {
    this.writeOptions = writeOptions;
  }
}
