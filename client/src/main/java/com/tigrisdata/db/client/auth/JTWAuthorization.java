package com.tigrisdata.db.client.auth;

public class JTWAuthorization implements AuthorizationToken {

  private final String refreshToken;
  private final String authorizationToken;

  public JTWAuthorization(String tigrisdbApiToken) {
    // TODO finalize the encoding mechanism
    if (tigrisdbApiToken.contains(":")) {
      String[] tokenParts = tigrisdbApiToken.split(":");
      this.refreshToken = tokenParts[0];
      this.authorizationToken = tokenParts[1];
    } else {
      this.authorizationToken = tigrisdbApiToken;
      this.refreshToken = "";
    }
  }

  @Override
  public String getAuthorizationToken() {
    return authorizationToken;
  }

  @Override
  public String getRefreshToken() {
    return refreshToken;
  }
}
