package com.tigrisdata.db.client.model;

public class InsertRequestOptions {
  private final WriteOption writeOption;

  public InsertRequestOptions(WriteOption writeOption) {
    this.writeOption = writeOption;
  }
}
