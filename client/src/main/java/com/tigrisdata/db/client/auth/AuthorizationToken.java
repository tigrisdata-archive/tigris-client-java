package com.tigrisdata.db.client.auth;

public interface AuthorizationToken extends Authentication {
  String getAuthorizationToken();

  String getRefreshToken();
}
