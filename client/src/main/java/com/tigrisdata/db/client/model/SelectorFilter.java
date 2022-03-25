package com.tigrisdata.db.client.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tigrisdata.db.client.utils.Utilities;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class SelectorFilter<T> implements TigrisFilter {

  private final ComparisonOperator comparisonOperator;
  private final String key;
  private final T val;

  public SelectorFilter(ComparisonOperator comparisonOperator, String key, T val) {
    this.comparisonOperator = comparisonOperator;
    this.key = key;
    this.val = val;
  }

  @Override
  public String toString() {
    Map<String, Map<String, Object>> outerMap = new HashMap<>();
    Map<String, Object> innerMap = new LinkedHashMap<>();
    outerMap.put(comparisonOperator.getOperator(), innerMap);
    innerMap.put(key, val);
    try {
      return Utilities.OBJECT_MAPPER.writeValueAsString(outerMap);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(e);
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
