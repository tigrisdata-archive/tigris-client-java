package com.tigrisdata.db.client.model;

import java.util.Arrays;
import java.util.stream.Collectors;

public class LogicalFilter implements TigrisFilter {

  private final LogicalFilterOperator logicalFilterOperator;
  private final TigrisFilter[] tigrisFilters;

  LogicalFilter(LogicalFilterOperator logicalFilterOperator, TigrisFilter[] tigrisFilters) {
    if (tigrisFilters.length < 2) {
      throw new IllegalArgumentException(
          "At least 2 filters are required to form composite filter");
    }
    this.logicalFilterOperator = logicalFilterOperator;
    this.tigrisFilters = tigrisFilters;
  }

  @Override
  public String toString() {
    return "{\""
        + logicalFilterOperator.getOperator()
        + "\":["
        + Arrays.stream(tigrisFilters).map(TigrisFilter::toString).collect(Collectors.joining(","))
        + "]";
  }
}
