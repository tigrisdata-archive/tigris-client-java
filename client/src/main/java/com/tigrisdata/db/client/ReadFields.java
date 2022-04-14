package com.tigrisdata.db.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents Fields to read from server. This is useful in cases when you want to read fields
 * selectively instead of reading entire document.
 */
public final class ReadFields {
  private final Map<String, Object> internalMap;
  private static final ReadFields EMPTY = new ReadFields(Collections.emptyMap());

  private ReadFields(ReadFieldsBuilder builder) {
    this.internalMap = Collections.unmodifiableMap(builder.map);
  }

  private ReadFields(Map<String, Object> internalMap) {
    this.internalMap = internalMap;
  }

  /**
   * This represents empty selection of field, server will return entire document.
   *
   * @return an empty {@link ReadFields}.
   */
  public static ReadFields empty() {
    return EMPTY;
  }

  String toJSON(ObjectMapper objectMapper) {
    try {
      return objectMapper.writeValueAsString(internalMap);
    } catch (JsonProcessingException ex) {
      // this is never expected when fields are constructed with using ReadFields
      throw new IllegalStateException(
          "This is caused because JSON serialization of ReadFields was not successful", ex);
    }
  }

  public boolean isEmpty() {
    return internalMap.isEmpty();
  }

  /**
   * Creates a new builder
   *
   * @return new builder
   */
  public static ReadFieldsBuilder newBuilder() {
    return new ReadFieldsBuilder();
  }

  /** Builder for {@link ReadFields} */
  public static class ReadFieldsBuilder {
    private final Map<String, Object> map;

    private ReadFieldsBuilder() {
      this.map = new LinkedHashMap<>();
    }

    /**
     * Fields to include while reading
     *
     * @param fieldName field name
     * @return ongoing builder
     */
    public ReadFieldsBuilder includeField(String fieldName) {
      this.map.put(fieldName, true);
      return this;
    }

    /**
     * Fields to exclude while reading
     *
     * @param fieldName field name
     * @return ongoing builder
     */
    public ReadFieldsBuilder excludeField(String fieldName) {
      this.map.put(fieldName, false);
      return this;
    }

    /**
     * Builds the instance of @{@link ReadFields}
     *
     * @return new instance of {@link ReadFields}
     */
    public ReadFields build() {
      return new ReadFields(this);
    }
  }
}
