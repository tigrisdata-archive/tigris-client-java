package com.tigrisdata.db.client.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tigrisdata.db.client.utils.Utilities;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class UpdateFields {
  private final Map<String, Object> internalMap;
  private static final String SET_OPERATOR = "$set";

  private UpdateFields(UpdateFieldsBuilder builder) {
    this.internalMap = builder.map;
  }

  public String toJSON() throws JsonProcessingException {
    return Utilities.OBJECT_MAPPER.writeValueAsString(internalMap);
  }

  public static UpdateFieldsBuilder newBuilder() {
    return new UpdateFieldsBuilder();
  }

  public static class UpdateFieldsBuilder {
    private final Map<String, Object> map;

    private UpdateFieldsBuilder() {
      this.map = new LinkedHashMap<>();
    }

    public UpdateFieldsBuilder set(SetFields setFields) {
      this.map.put(SET_OPERATOR, setFields.getInternalMap());
      return this;
    }

    public UpdateFields build() {
      return new UpdateFields(this);
    }
  }

  public static class SetFields {
    private final Map<String, Object> internalMap;

    private SetFields(SetFieldsBuilder builder) {
      this.internalMap = builder.internalMap;
    }

    private Map<String, Object> getInternalMap() {
      return Collections.unmodifiableMap(internalMap);
    }

    public static SetFieldsBuilder newBuilder() {
      return new SetFieldsBuilder();
    }
  }

  public static class SetFieldsBuilder {
    private final Map<String, Object> internalMap;

    private SetFieldsBuilder() {
      this.internalMap = new LinkedHashMap<>();
    }

    public SetFieldsBuilder set(String fieldName, int newValue) {
      internalMap.put(fieldName, newValue);
      return this;
    }

    public SetFieldsBuilder set(String fieldName, long newValue) {
      internalMap.put(fieldName, newValue);
      return this;
    }

    public SetFieldsBuilder set(String fieldName, String newValue) {
      internalMap.put(fieldName, newValue);
      return this;
    }

    public SetFieldsBuilder set(String fieldName, boolean newValue) {
      internalMap.put(fieldName, newValue);
      return this;
    }

    public SetFieldsBuilder set(String fieldName, byte[] newValue) {
      internalMap.put(fieldName, newValue);
      return this;
    }

    public SetFields build() {
      if (internalMap.isEmpty()) {
        throw new IllegalStateException("No fields are set");
      }
      return new SetFields(this);
    }
  }
}
