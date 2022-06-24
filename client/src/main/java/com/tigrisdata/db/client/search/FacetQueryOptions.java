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
  VALUE("value");
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

  private static final long DEFAULT_SIZE = 10;
  private static final FacetFieldType DEFAULT_TYPE = FacetFieldType.VALUE;
  private static final FacetQueryOptions DEFAULT_INSTANCE = newBuilder().build();

  private final FacetFieldType type;
  private final long size;

  private FacetQueryOptions(Builder builder) {
    this.type = builder.type;
    this.size = builder.size;
  }

  /**
   * Default options for a facet query type: value, size: 10
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
  public long getSize() {
    return size;
  }

  @Override
  public String toJSON(ObjectMapper objectMapper) {
    Map<String, String> map =
        new HashMap<String, String>() {
          {
            put("type", type.toString());
            put("size", String.valueOf(size));
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

    if (size != that.size) {
      return false;
    }
    return type == that.type;
  }

  @Override
  public int hashCode() {
    int result = type != null ? type.hashCode() : 0;
    result = 31 * result + (int) (size ^ (size >>> 32));
    return result;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {

    private FacetFieldType type;

    private long size;

    private Builder() {
      this.type = DEFAULT_TYPE;
      this.size = DEFAULT_SIZE;
    }

    /** Type of schema field */
    public Builder withType(FacetFieldType type) {
      this.type = type;
      return this;
    }

    /** Maximum number facet results to include in /search response */
    public Builder withSize(long size) {
      this.size = size;
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
