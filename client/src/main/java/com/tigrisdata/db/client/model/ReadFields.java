package com.tigrisdata.db.client.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tigrisdata.db.client.utils.Utilities;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ReadFields {
  private final Map<String, Object> internalMap;
  private static final ReadFields EMPTY = new ReadFields(Collections.emptyMap());

  private ReadFields(ReadFieldsBuilder builder) {
    this.internalMap = Collections.unmodifiableMap(builder.map);
  }

  private ReadFields(Map<String, Object> internalMap) {
    this.internalMap = internalMap;
  }

  public static ReadFields empty() {
    return EMPTY;
  }

  public String toJSON() throws JsonProcessingException {
    return Utilities.OBJECT_MAPPER.writeValueAsString(internalMap);
  }

  public boolean isEmpty() {
    return internalMap.isEmpty();
  }

  public static ReadFieldsBuilder newBuilder() {
    return new ReadFieldsBuilder();
  }

  public static class ReadFieldsBuilder {
    private final Map<String, Object> map;

    private ReadFieldsBuilder() {
      this.map = new LinkedHashMap<>();
    }

    public ReadFieldsBuilder includeField(String fieldName) {
      this.map.put(fieldName, true);
      return this;
    }

    public ReadFieldsBuilder excludeField(String fieldName) {
      this.map.put(fieldName, false);
      return this;
    }

    public ReadFields build() {
      return new ReadFields(this);
    }
  }
}
