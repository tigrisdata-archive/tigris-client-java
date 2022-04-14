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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

class SelectorFilter<T> implements TigrisFilter {

  private final ComparisonOperator comparisonOperator;
  private final String key;
  private final T val;

  SelectorFilter(ComparisonOperator comparisonOperator, String key, T val) {
    this.comparisonOperator = comparisonOperator;
    this.key = key;
    this.val = val;
  }

  @Override
  public String toJSON(ObjectMapper objectMapper) {
    Map<String, Object> map = new LinkedHashMap<>();
    map.put(key, val);
    try {
      return objectMapper.writeValueAsString(map);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(
          "This was caused because the SelectorFilter's JSON serialization raised errors", e);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    SelectorFilter<?> that = (SelectorFilter<?>) o;

    if (comparisonOperator != that.comparisonOperator) return false;
    if (!Objects.equals(key, that.key)) return false;
    return Objects.equals(val, that.val);
  }

  @Override
  public int hashCode() {
    int result = comparisonOperator != null ? comparisonOperator.hashCode() : 0;
    result = 31 * result + (key != null ? key.hashCode() : 0);
    result = 31 * result + (val != null ? val.hashCode() : 0);
    return result;
  }
}
