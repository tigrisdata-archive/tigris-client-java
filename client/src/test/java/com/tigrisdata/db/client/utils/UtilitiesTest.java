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
