package com.tigrisdata.db.client.model;

public class UpdateRequestOptions {
  private final WriteOptions writeOptions;

  public UpdateRequestOptions(WriteOptions writeOptions) {
    this.writeOptions = writeOptions;
  }
}
