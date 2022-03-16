package com.tigrisdata.db.client.auth;

public class JWT {
  private final String token;

  // TODO parse payload and extract expiry
  public JWT(String token) {
    this.token = token;
  }
}
