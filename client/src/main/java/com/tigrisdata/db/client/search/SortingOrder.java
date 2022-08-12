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
import com.tigrisdata.db.client.TigrisSort;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Interface to construct sorting order for search results */
public final class SortingOrder implements JSONSerializable {

  private final List<TigrisSort> orders;
  private String cachedJSON;

  private SortingOrder(Builder builder) {
    orders = new ArrayList<>();
    orders.addAll(builder.sortOrders.values());
  }

  /**
   * Gets sorting order of fields
   *
   * @return non-null immutable {@link List}
   */
  public List<TigrisSort> get() {
    return Collections.unmodifiableList(orders);
  }

  /** {@inheritDoc} */
  @Override
  public String toJSON(ObjectMapper objectMapper) {
    if (Objects.nonNull(cachedJSON)) {
      return cachedJSON;
    }

    List<Map<String, Object>> list = new ArrayList<>();
    for (TigrisSort sort : orders) {
      list.add(sort.toMap());
    }
    try {
      cachedJSON = objectMapper.writeValueAsString(list);
      return cachedJSON;
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(
          "This was caused because the SortingOrder's JSON serialization raised errors", e);
    }
  }

  /**
   * Builder API for {@link SortingOrder}
   *
   * @return {@link SortingOrder.Builder}
   */
  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {
    private final LinkedHashMap<String, TigrisSort> sortOrders;

    private Builder() {
      sortOrders = new LinkedHashMap<>();
    }

    /**
     * Adds field names and their corresponding sort orders to the sequence
     *
     * @param orders {@link TigrisSort}
     * @return {@link SortingOrder.Builder}
     */
    public Builder withOrder(TigrisSort... orders) {
      for (TigrisSort o : orders) {
        sortOrders.put(o.getFieldName(), o);
      }
      return this;
    }

    /**
     * Constructs a {@link SortingOrder}
     *
     * @return {@link SortingOrder}
     */
    public SortingOrder build() {
      return new SortingOrder(this);
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

    SortingOrder that = (SortingOrder) o;

    return Objects.equals(orders, that.orders);
  }

  @Override
  public int hashCode() {
    return orders != null ? orders.hashCode() : 0;
  }
}
