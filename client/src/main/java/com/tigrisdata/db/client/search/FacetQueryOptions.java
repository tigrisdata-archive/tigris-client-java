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
import java.util.HashMap;
import java.util.Map;

enum FacetFieldType {
  value("value");
  private final String strVal;

  FacetFieldType(String v) {
    this.strVal = v;
  }

  @Override
  public String toString() {
    return this.strVal;
  }
}

/** Represents options related to Facet query for /search */
public final class FacetQueryOptions implements JSONSerializable {

  private static final long DEFAULT_LIMIT = 10;
  private static final FacetFieldType DEFAULT_TYPE = FacetFieldType.value;
  private static final FacetQueryOptions DEFAULT_INSTANCE = newBuilder().build();

  private final FacetFieldType type;
  private final long limit;

  private FacetQueryOptions(Builder builder) {
    this.type = builder.type;
    this.limit = builder.limit;
  }

  /**
   * Default options for a facet query type: value limit: 10
   *
   * @return {@link FacetQueryOptions}
   */
  public static FacetQueryOptions getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  /** Type of schema field */
  public FacetFieldType getType() {
    return type;
  }

  /** Maximum number facet results to include in /search response */
  public long getLimit() {
    return limit;
  }

  @Override
  public String toJSON(ObjectMapper objectMapper) {
    Map<String, String> map =
        new HashMap<String, String>() {
          {
            put("type", type.toString());
            put("limit", String.valueOf(limit));
          }
        };

    try {
      return objectMapper.writeValueAsString(map);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("Failed to serialize FacetFieldOption to JSON", e);
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

    FacetQueryOptions that = (FacetQueryOptions) o;

    if (limit != that.limit) {
      return false;
    }
    return type == that.type;
  }

  @Override
  public int hashCode() {
    int result = type != null ? type.hashCode() : 0;
    result = 31 * result + (int) (limit ^ (limit >>> 32));
    return result;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {

    private FacetFieldType type;

    private long limit;

    private Builder() {
      this.type = DEFAULT_TYPE;
      this.limit = DEFAULT_LIMIT;
    }

    /** Type of schema field */
    public Builder withType(FacetFieldType type) {
      this.type = type;
      return this;
    }

    /** Maximum number facet results to include in /search response */
    public Builder withLimit(long limit) {
      this.limit = limit;
      return this;
    }

    /** Builds {@link FacetQueryOptions} */
    public FacetQueryOptions build() {
      if (this.type == null) {
        this.type = DEFAULT_TYPE;
      }
      return new FacetQueryOptions(this);
    }
  }
}
