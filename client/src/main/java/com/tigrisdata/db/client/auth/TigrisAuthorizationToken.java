package com.tigrisdata.db.client.auth;

public class TigrisAuthorizationToken implements AuthorizationToken {
  private final String authorizationToken;

  public TigrisAuthorizationToken(String tigrisDbApiToken) {
    // shallow checks
    if (tigrisDbApiToken == null || tigrisDbApiToken.isEmpty()) {
      throw new IllegalArgumentException("Token is invalid");
    }
    this.authorizationToken = tigrisDbApiToken;
  }

  @Override
  public String getAuthorizationToken() {
    return authorizationToken;
  }
}
