package com.tigrisdata.db.client.model;

public class TruncateCollectionResponse {
  private final TigrisDBResponse tigrisDBResponse;

  public TruncateCollectionResponse(TigrisDBResponse tigrisDBResponse) {
    this.tigrisDBResponse = tigrisDBResponse;
  }

  public TigrisDBResponse getTigrisDBResponse() {
    return tigrisDBResponse;
  }
}
