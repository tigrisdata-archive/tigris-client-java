package com.tigrisdata.db.client.model;

public final class Filters {

  private Filters() {}

  public static SelectorFilter<Integer> eq(String key, int value) {
    return new SelectorFilter<>(ComparisonOperator.EQUALS, key, value);
  }

  public static SelectorFilter<Boolean> eq(String key, boolean value) {
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
