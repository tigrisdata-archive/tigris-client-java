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

/** Represents optional information to build facets in search results */
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
   * Default options for a facet field query
   *
   * <p>type: value
   *
   * <p>size: 10
   *
   * @return {@link FacetQueryOptions}
   */
  public static FacetQueryOptions getDefault() {
    return DEFAULT_INSTANCE;
  }

  /**
   * Gets type of facets to build
   *
   * @return faceting option type
   */
  public FacetFieldType getType() {
    return type;
  }

  /**
   * Gets maximum number facets to include in results
   *
   * @return maximum number facets to include in results
   */
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

  /**
   * Builder API for{@link FacetQueryOptions}
   *
   * @return {@link FacetQueryOptions.Builder}
   * @see #getDefault()
   */
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

    /**
     * Sets type of facets to build
     *
     * @param type facet type to build
     * @return {@link FacetQueryOptions.Builder}
     */
    public Builder withType(FacetFieldType type) {
      this.type = type;
      return this;
    }

    /**
     * Sets maximum number facets to include in results
     *
     * @param size maximum number facets to include in results
     * @return {@link FacetQueryOptions.Builder}
     */
    public Builder withSize(long size) {
      this.size = size;
      return this;
    }

    /**
     * Constructs {@link FacetQueryOptions}
     *
     * @return {@link FacetQueryOptions}
     */
    public FacetQueryOptions build() {
      if (this.type == null) {
        this.type = DEFAULT_TYPE;
      }
      return new FacetQueryOptions(this);
    }
  }
}
