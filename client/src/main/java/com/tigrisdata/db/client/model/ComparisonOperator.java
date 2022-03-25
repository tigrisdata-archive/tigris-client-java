package com.tigrisdata.db.client.model;

public enum ComparisonOperator {
  EQUALS("$eq");

  private final String operator;

  ComparisonOperator(String operator) {
    this.operator = operator;
  }

  public String getOperator() {
    return operator;
  }
}
