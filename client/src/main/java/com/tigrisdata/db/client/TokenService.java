package com.tigrisdata.db.client;

interface TokenService {
  /** @return access token for the call to Tigris server */
  String getAccessToken();
}
