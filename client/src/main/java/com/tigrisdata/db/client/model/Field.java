package com.tigrisdata.db.client.model;

import java.util.Collections;
import java.util.Map;

public interface Field<T> {
  String name();

  T value();

  default Map<String, Object> metadata() {
    return Collections.emptyMap();
  }
}
