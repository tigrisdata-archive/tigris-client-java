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
package com.tigrisdata.db.client;

import java.util.UUID;

/** Helper class to help construct Filters */
public final class Filters {

  private Filters() {}

  /**
   * Creates equals filter for given key and value
   *
   * @param key field key
   * @param value field value
   * @return constructed {@link SelectorFilter} of type {@link Integer}
   */
  public static SelectorFilter<Integer> eq(String key, int value) {
    return new SelectorFilter<>(ComparisonOperator.EQUALS, key, value);
  }

  /**
   * Creates equals filter for given key and value
   *
   * @param key field key
   * @param value field value
   * @return constructed {@link SelectorFilter} of type {@link Boolean}
   */
  public static SelectorFilter<Boolean> eq(String key, boolean value) {
    return new SelectorFilter<>(ComparisonOperator.EQUALS, key, value);
  }

  /**
   * Creates equals filter for given key and value
   *
   * @param key field key
   * @param value field value
   * @return constructed {@link SelectorFilter} of type {@link Long}
   */
  public static SelectorFilter<Long> eq(String key, long value) {
    return new SelectorFilter<>(ComparisonOperator.EQUALS, key, value);
  }

  /**
   * Creates equals filter for given key and value
   *
   * @param key field key
   * @param value field value
   * @return constructed {@link SelectorFilter} of type {@link String}
   */
  public static SelectorFilter<String> eq(String key, String value) {
    return new SelectorFilter<>(ComparisonOperator.EQUALS, key, value);
  }

  /**
   * Creates equals filter for given key and value
   *
   * @param key field key
   * @param value field value
   * @return constructed {@link SelectorFilter} of type {@link UUID}
   */
  public static SelectorFilter<UUID> eq(String key, UUID value) {
    return new SelectorFilter<>(ComparisonOperator.EQUALS, key, value);
  }

  /**
   * Creates a composite logical OR filter from input filters
   *
   * @param filters one or more filters
   * @return constructed {@link LogicalFilter}
   */
  public static LogicalFilter or(TigrisFilter... filters) {
    return new LogicalFilter(LogicalFilterOperator.OR, filters);
  }

  /**
   * Creates a composite logical AND filter from input filters
   *
   * @param filters one or more filters
   * @return constructed {@link LogicalFilter}
   */
  public static LogicalFilter and(TigrisFilter... filters) {
    return new LogicalFilter(LogicalFilterOperator.AND, filters);
  }

  /**
   * Filters nothing, this is useful to read all the data
   *
   * @return a special filter that filters nothing.
   */
  public static SelectorFilter<String> nothing() {
    return new SelectorFilter<>(ComparisonOperator.NONE, "", "");
  }
}
