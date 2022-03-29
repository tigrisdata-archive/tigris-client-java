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
package com.tigrisdata.db.client.model;

import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class FieldsTest {

  @Test
  public void testFields() {
    Field<String> strField = Fields.stringField("k", "v");
    Assert.assertEquals("k", strField.name());
    Assert.assertEquals("v", strField.value());

    Field<Integer> intField = Fields.integerField("k", 123);
    Assert.assertEquals("k", intField.name());
    MatcherAssert.assertThat("values match", 123 == intField.value());

    Field<Double> doubleField = Fields.doubleField("k", 123.456D);
    Assert.assertEquals("k", doubleField.name());
    MatcherAssert.assertThat("values match", 123.456D == doubleField.value());

    Field<Boolean> booleanField = Fields.booleanField("k", true);
    Assert.assertEquals("k", booleanField.name());
    MatcherAssert.assertThat("values match", booleanField.value());

    Field<byte[]> byteArrayField =
        Fields.byteArrayField("k", "hello".getBytes(StandardCharsets.UTF_8));
    Assert.assertEquals("k", byteArrayField.name());
    MatcherAssert.assertThat(
        "values match",
        Arrays.equals("hello".getBytes(StandardCharsets.UTF_8), byteArrayField.value()));
  }
}
