package com.tigrisdata.db.client.model;

public class UpdateResponse {
  private final int updatedRecordCount;

  public UpdateResponse(int updatedRecordCount) {
    this.updatedRecordCount = updatedRecordCount;
  }

  public int getUpdatedRecordCount() {
    return updatedRecordCount;
  }
}
