package com.tigrisdata.db.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents structure to update what and how to update fields of certain filtered documents in
 * collection.
 */
public final class UpdateFields {
  private final Map<String, Object> internalMap;
  private static final String SET_OPERATOR = "$set";

  private UpdateFields(UpdateFieldsBuilder builder) {
    this.internalMap = builder.map;
  }

  public String toJSON(ObjectMapper objectMapper) {
    try {
      return objectMapper.writeValueAsString(internalMap);
    } catch (JsonProcessingException jsonProcessingException) {
      // this is never expected when UpdateFields is constructed using the API.
      throw new IllegalStateException(
          "This is raised because the JSON serialization of UpdateFields failed",
          jsonProcessingException);
    }
  }

  public static UpdateFieldsBuilder newBuilder() {
    return new UpdateFieldsBuilder();
  }

  /** Builder for {@link UpdateFields} */
  public static class UpdateFieldsBuilder {
    private final Map<String, Object> map;
    private final Map<String, Object> setMap;

    private UpdateFieldsBuilder() {
      this.map = new LinkedHashMap<>();
      this.setMap = new LinkedHashMap<>();
    }

    public UpdateFieldsBuilder set(String fieldName, int newValue) {
      setMap.put(fieldName, newValue);
      return this;
    }

    public UpdateFieldsBuilder set(String fieldName, long newValue) {
      setMap.put(fieldName, newValue);
      return this;
    }

    public UpdateFieldsBuilder set(String fieldName, String newValue) {
      setMap.put(fieldName, newValue);
      return this;
    }

    public UpdateFieldsBuilder set(String fieldName, boolean newValue) {
      setMap.put(fieldName, newValue);
      return this;
    }

    public UpdateFieldsBuilder set(String fieldName, byte[] newValue) {
      setMap.put(fieldName, newValue);
      return this;
    }

    public UpdateFieldsBuilder set(String fieldName, double newValue) {
      setMap.put(fieldName, newValue);
      return this;
    }

    public UpdateFieldsBuilder set(String fieldName, UUID newValue) {
      setMap.put(fieldName, newValue);
      return this;
    }

    public UpdateFields build() {
      if (!setMap.isEmpty()) {
        this.map.put(SET_OPERATOR, setMap);
      } else {
        throw new IllegalStateException("empty update fields");
      }
      return new UpdateFields(this);
    }
  }
}
