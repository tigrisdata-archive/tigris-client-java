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
import com.tigrisdata.db.client.JSONSerializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/** Container for schema fields to project the search query */
public final class SearchFields implements JSONSerializable {

  private static final SearchFields EMPTY = newBuilder().build();
  private final List<String> fields;

  private SearchFields(Builder builder) {
    this.fields = Collections.unmodifiableList(builder.fields);
  }

  /**
   * Represents empty Search Field projection, server will project search query against all
   * searchable fields
   *
   * @return an empty {@link SearchFields}
   */
  public static SearchFields empty() {
    return EMPTY;
  }

  /** @return non-null immutable {@link List} of fields in this object */
  public List<String> getFields() {
    return this.fields;
  }

  @Override
  public String toJSON(ObjectMapper objectMapper) {
    try {
      return objectMapper.writeValueAsString(fields);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(
          "This was caused because the SearchFields's JSON serialization raised errors", e);
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

    SearchFields that = (SearchFields) o;
    return Objects.equals(fields, that.getFields());
  }

  @Override
  public int hashCode() {
    return Objects.hash(fields);
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {

    private List<String> fields;

    private Builder() {}

    /**
     * Field to project search query against
     *
     * @param field schema field name as string
     */
    public Builder withField(String field) {
      if (this.fields == null) {
        this.fields = new ArrayList<>();
      }
      this.fields.add(field);
      return this;
    }

    /**
     * Schema fields to project search query against
     *
     * @param fields list of schema field names as string
     */
    public Builder withFields(Collection<? extends String> fields) {
      if (this.fields == null) {
        this.fields = new ArrayList<>();
      }
      this.fields.addAll(fields);
      return this;
    }

    /** Builds a {@link SearchFields} */
    public SearchFields build() {
      if (this.fields == null) {
        this.fields = Collections.emptyList();
      }
      return new SearchFields(this);
    }
  }
}
