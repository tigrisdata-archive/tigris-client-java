package com.tigrisdata.db.client.model;

public class ReadRequestOptions {
  // TODO: add offset
  private ReadOptions readOptions;
  private long skip;
  private long limit;

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

  public long getSkip() {
    return skip;
  }

  public void setSkip(long skip) {
    this.skip = skip;
  }

  public long getLimit() {
    return limit;
  }

  public void setLimit(long limit) {
    this.limit = limit;
  }
}
