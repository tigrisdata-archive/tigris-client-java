package com.tigrisdata.db.client.model;

public class ReadRequestOptions {
  ReadOptions readOptions;

  public ReadRequestOptions() {}

  public ReadRequestOptions(ReadOptions readOptions) {
    this.readOptions = readOptions;
  }

  public ReadOptions getReadOptions() {
    return readOptions;
  }

  public void setReadOptions(ReadOptions readOptions) {
    this.readOptions = readOptions;
  }
}
