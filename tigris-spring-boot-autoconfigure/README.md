# Tigris Spring Autoconfiguration

This module provides Tigris autoconfiguration for your spring project. 

# Configurations

```yaml
tigris:
  # by default tigris.sync-client.enabled is true  
  sync-client:
    enabled: true
    initializer:
      # by default tigris.sync-client.initializer.enabled is true
      enabled: true 
      
  # by default tigris-async-client.enabled is false  
  async-client:
    enabled: false
    initializer:
      # by default tigris.async-client.initializer.enabled is true
      enabled: true
  server-url: <YOUR_TIGRIS_SERVER_URL>
  network:
    # by default tigris.network.usePlainText is set to false
    usePlainText: false s
  auth:
    clientId: <YOUR_TIGRIS_APP_CLIENT_ID>
    clientSecret: <YOUR_TIGRIS_APP_CLIENT_SECRET>
```

Note: Make sure you [sanitize](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html#appendix.application-properties) the `clientSecret` from actuator endpoints if 
you use spring boot actuator in your app.