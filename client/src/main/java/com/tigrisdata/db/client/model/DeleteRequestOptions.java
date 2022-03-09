package com.tigrisdata.db.client.model;

public class DeleteRequestOptions {
  private final WriteOption writeOption;

  public DeleteRequestOptions(WriteOption writeOption) {
    this.writeOption = writeOption;
  }
}
