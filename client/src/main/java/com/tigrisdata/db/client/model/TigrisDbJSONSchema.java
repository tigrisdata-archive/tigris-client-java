package com.tigrisdata.db.client.model;

public class TigrisDBJSONSchema implements TigrisDBSchema {
  @Override
  public boolean equals(Object obj) {
    // intentionally it is matching true always
    return true;
  }
}
