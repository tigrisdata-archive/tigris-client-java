package com.tigrisdata.db.client.model;

import java.io.IOException;

public interface TigrisDBSchema {
  String getSchemaContent() throws IOException;
}
