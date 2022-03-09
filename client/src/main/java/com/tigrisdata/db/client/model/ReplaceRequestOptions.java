package com.tigrisdata.db.client.model;

public class ReplaceRequestOptions {
  private final WriteOption writeOption;

  public ReplaceRequestOptions(WriteOption writeOption) {
    this.writeOption = writeOption;
  }
}
