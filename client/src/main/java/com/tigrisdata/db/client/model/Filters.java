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
package com.tigrisdata.db.client.model;

public final class Filters {

  private Filters() {}

  public static SelectorFilter<Integer> eq(String key, int value) {
    return new SelectorFilter<>(ComparisonOperator.EQUALS, key, value);
  }

  public static SelectorFilter<Boolean> eq(String key, boolean value) {
    return new SelectorFilter<>(ComparisonOperator.EQUALS, key, value);
  }

  public static SelectorFilter<Long> eq(String key, long value) {
    return new SelectorFilter<>(ComparisonOperator.EQUALS, key, value);
  }

  public static SelectorFilter<String> eq(String key, String value) {
    return new SelectorFilter<>(ComparisonOperator.EQUALS, key, value);
  }

  public static LogicalFilter or(TigrisFilter... filters) {
    return new LogicalFilter(LogicalFilterOperator.OR, filters);
  }

  public static LogicalFilter and(TigrisFilter... filters) {
    return new LogicalFilter(LogicalFilterOperator.AND, filters);
  }
}
