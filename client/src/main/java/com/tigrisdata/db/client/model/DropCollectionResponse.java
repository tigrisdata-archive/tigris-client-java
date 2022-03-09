package com.tigrisdata.db.client.model;

public class DropCollectionResponse {
  private final TigrisDBResponse tigrisDBResponse;

  public DropCollectionResponse(TigrisDBResponse tigrisDBResponse) {
    this.tigrisDBResponse = tigrisDBResponse;
  }

  public TigrisDBResponse getTigrisDBResponse() {
    return tigrisDBResponse;
  }
}
