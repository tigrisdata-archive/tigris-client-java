package com.tigrisdata.db.client.model;

import org.junit.Assert;
import org.junit.Test;

public class ComparisonOperatorTest {

  @Test
  public void testOperator() {
    Assert.assertEquals("$eq", ComparisonOperator.EQUALS.getOperator());
  }
}
