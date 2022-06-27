/*
 * Copyright 2022 Tigris Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tigrisdata.db.client.search;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Data class to construct facet query for /search */
public final class FacetFieldsQuery implements FacetQuery {

  private static final FacetFieldsQuery EMPTY = newBuilder().build();

  private final Map<String, FacetQueryOptions> internalMap;

  private FacetFieldsQuery(Builder builder) {
    this.internalMap = Collections.unmodifiableMap(builder.fieldMap);
  }

  /**
   * Represents empty facet query, server will not return any facets
   *
   * @return an empty {@link FacetFieldsQuery}
   */
  public static FacetFieldsQuery empty() {
    return EMPTY;
  }

  /**
   * Gets underlying map of facet field query
   *
   * @return non-null immutable {@link Map} of facet fields and respective query options
   */
  public Map<String, FacetQueryOptions> getFacetFields() {
    return this.internalMap;
  }

  /**
   * Returns {@code true} if there are no fields in facet query
   *
   * @return {@code true} if there are no fields in facet query
   */
  public boolean isEmpty() {
    return internalMap.isEmpty();
  }

  @Override
  public String toJSON(ObjectMapper objectMapper) {
    Function<FacetQueryOptions, Map<String, String>> toOptionsMap =
        o ->
            new HashMap<String, String>() {
              {
                put("size", String.valueOf(o.getSize()));
                put("type", o.getType().toString());
              }
            };

    Map<String, Map<String, String>> fieldOptionsMap =
        internalMap.entrySet().stream()
            .collect(Collectors.toMap(Entry::getKey, e -> toOptionsMap.apply(e.getValue())));
    try {
      return objectMapper.writeValueAsString(fieldOptionsMap);
    } catch (JsonProcessingException ex) {
      throw new IllegalArgumentException("Failed to serialize FacetFields to JSON", ex);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    FacetFieldsQuery fields = (FacetFieldsQuery) o;

    return Objects.equals(internalMap, fields.internalMap);
  }

  @Override
  public int hashCode() {
    return internalMap != null ? internalMap.hashCode() : 0;
  }

  /**
   * Builder API for {@link FacetFieldsQuery}
   *
   * @return {@link FacetFieldsQuery.Builder}
   * @see #empty()
   */
  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {

    private Map<String, FacetQueryOptions> fieldMap;

    private Builder() {}

    /**
     * Sets a collection field name to include facets in search results
     *
     * <p>Uses default {@link FacetQueryOptions} options
     *
     * @param field collection field name as string
     * @return {@link FacetFieldsQuery.Builder}
     */
    public Builder withField(String field) {
      if (fieldMap == null) {
        fieldMap = new HashMap<>();
      }
      fieldMap.put(field, FacetQueryOptions.getDefault());
      return this;
    }

    /**
     * Sets collection field names to include in facets in search results
     *
     * <p>Uses default {@link FacetQueryOptions} options
     *
     * @param fields list of field names
     * @return {@link FacetFieldsQuery.Builder}
     */
    public Builder withFields(Collection<String> fields) {
      if (fieldMap == null) {
        fieldMap = new HashMap<>();
      }
      fields.forEach(f -> fieldMap.put(f, FacetQueryOptions.getDefault()));
      return this;
    }

    /**
     * Sets a collection field name and query options to include facet results in search response
     *
     * @param field collection field name as string
     * @param options optional information to generate facet results
     * @return {@link FacetFieldsQuery.Builder}
     * @see #withField(String)
     */
    public Builder withFieldOptions(String field, FacetQueryOptions options) {
      if (fieldMap == null) {
        fieldMap = new HashMap<>();
      }
      fieldMap.put(field, options);
      return this;
    }

    /**
     * Add multiple fields to facet query
     *
     * @param fieldOptions Map of collection field names and query options
     * @return {@link FacetFieldsQuery.Builder}
     */
    public Builder addAll(Map<String, FacetQueryOptions> fieldOptions) {
      if (fieldMap == null) {
        fieldMap = new HashMap<>();
      }
      fieldMap.putAll(fieldOptions);
      return this;
    }

    /**
     * Constructs a {@link FacetFieldsQuery}
     *
     * @return {@link FacetFieldsQuery} object
     */
    public FacetFieldsQuery build() {
      if (fieldMap == null) {
        fieldMap = Collections.emptyMap();
      }
      return new FacetFieldsQuery(this);
    }
  }
}
