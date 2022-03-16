package com.tigrisdata.db.client.auth;

public class JTWAuthorization implements AuthorizationToken {

  private final String refreshToken;
  private final String authorizationToken;

  public JTWAuthorization(String tigrisDbApiToken) {
    // shallow checks
    if (tigrisDbApiToken == null || tigrisDbApiToken.isEmpty()) {
      throw new IllegalArgumentException("Token is invalid");
    }
    // TODO finalize the encoding mechanism
    if (tigrisDbApiToken.contains(":")) {
      String[] tokenParts = tigrisDbApiToken.split(":");
      this.refreshToken = tokenParts[0];
      this.authorizationToken = tokenParts[1];
    } else {
      this.authorizationToken = tigrisDbApiToken;
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
