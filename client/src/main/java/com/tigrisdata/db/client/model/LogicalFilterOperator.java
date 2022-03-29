package com.tigrisdata.db.client.model;

public enum LogicalFilterOperator {
  OR("$or"),
  AND("$and");

  private final String operator;

  LogicalFilterOperator(String operator) {
    this.operator = operator;
  }

  public String getOperator() {
    return operator;
  }
}
