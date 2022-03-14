package com.tigrisdata.db.client.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tigrisdata.db.client.model.Field;
import com.tigrisdata.db.client.model.Fields;
import com.tigrisdata.db.client.model.Operators;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class UtilitiesTest {

  @Test
  public void testFieldsOperationToJsonConversion() throws JsonProcessingException {
    Field<String> strField = Fields.stringField("k1", "v1");
    Field<Integer> intField = Fields.integerField("k2", 123);

    String fieldsOperationJsonString =
        Utilities.fieldsOperation(
            Operators.SET,
            new ArrayList<Field<?>>() {
              {
                add(strField);
                add(intField);
              }
            });
    Assert.assertEquals("{\"$set\":{\"k1\":\"v1\",\"k2\":123}}", fieldsOperationJsonString);
  }

  @Test
  public void testFieldsToJsonConversion() throws JsonProcessingException {
    Field<Boolean> a = Fields.booleanField("a", true);
    Field<Boolean> b = Fields.booleanField("b", false);
    Field<Boolean> c = Fields.booleanField("c", true);
    String fieldsJsonString =
        Utilities.fields(
            new ArrayList<Field<?>>() {
              {
                add(a);
                add(b);
                add(c);
              }
            });
    Assert.assertEquals("{\"a\":true,\"b\":false,\"c\":true}", fieldsJsonString);
  }
}
