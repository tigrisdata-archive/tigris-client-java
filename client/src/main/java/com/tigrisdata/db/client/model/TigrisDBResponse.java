package com.tigrisdata.db.client.model;

public class TigrisDBResponse {
  private final String message;

  public TigrisDBResponse(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
