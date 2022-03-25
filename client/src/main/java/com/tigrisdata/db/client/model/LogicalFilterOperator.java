package com.tigrisdata.db.client.model;

public enum LogicalFilterOperator {
  OR("$or"),
  AND("$and");

  private final String operatorName;

  LogicalFilterOperator(String operatorName) {
    this.operatorName = operatorName;
  }

  public String getOperatorName() {
    return operatorName;
  }
}
