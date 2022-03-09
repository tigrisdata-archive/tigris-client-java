package com.tigrisdata.db.client.model;

public class UpdateRequestOptions {
  private final WriteOption writeOption;

  public UpdateRequestOptions(WriteOption writeOption) {
    this.writeOption = writeOption;
  }
}
