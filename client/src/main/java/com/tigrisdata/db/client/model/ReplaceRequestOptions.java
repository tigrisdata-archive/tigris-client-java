package com.tigrisdata.db.client.model;

public class ReplaceRequestOptions {
  private WriteOptions writeOptions;

  public ReplaceRequestOptions() {}

  public ReplaceRequestOptions(WriteOptions writeOptions) {
    this.writeOptions = writeOptions;
  }

  public WriteOptions getWriteOptions() {
    return writeOptions;
  }

  public void setWriteOptions(WriteOptions writeOptions) {
    this.writeOptions = writeOptions;
  }
}
