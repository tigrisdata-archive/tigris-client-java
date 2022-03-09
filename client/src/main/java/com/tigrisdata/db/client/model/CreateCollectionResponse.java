package com.tigrisdata.db.client.model;

public class CreateCollectionResponse {
  private final TigrisDBResponse tigrisDBResponse;

  public CreateCollectionResponse(TigrisDBResponse tigrisDBResponse) {
    this.tigrisDBResponse = tigrisDBResponse;
  }

  public TigrisDBResponse getTigrisDBResponse() {
    return tigrisDBResponse;
  }
}
