package com.tigrisdata.db.client.model;

public class InsertRequestOptions {
  private WriteOptions writeOptions;
  private boolean mustNotExist;

  public InsertRequestOptions() {}

  public InsertRequestOptions(WriteOptions writeOptions) {
    this.writeOptions = writeOptions;
  }

  public InsertRequestOptions(WriteOptions writeOptions, boolean mustNotExist) {
    this.writeOptions = writeOptions;
    this.mustNotExist = mustNotExist;
  }

  public boolean isMustNotExist() {
    return mustNotExist;
  }

  public void setMustNotExist(boolean mustNotExist) {
    this.mustNotExist = mustNotExist;
  }

  public WriteOptions getWriteOptions() {
    return writeOptions;
  }

  public void setWriteOptions(WriteOptions writeOptions) {
    this.writeOptions = writeOptions;
  }
}
