package com.tigrisdata.db.client.model;

public class AlterCollectionResponse {
  private final TigrisDBResponse tigrisDBResponse;

  public AlterCollectionResponse(TigrisDBResponse tigrisDBResponse) {
    this.tigrisDBResponse = tigrisDBResponse;
  }

  public TigrisDBResponse getTigrisDBResponse() {
    return tigrisDBResponse;
  }
}
