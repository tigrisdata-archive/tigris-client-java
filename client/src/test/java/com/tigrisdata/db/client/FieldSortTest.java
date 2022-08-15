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

package com.tigrisdata.db.client;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Assert;
import org.junit.Test;

public class FieldSortTest {

  @Test
  public void testToMap() {
    String expectedFieldName = "parent.nested_field";
    FieldSort fs = new FieldSort(expectedFieldName, SortOrder.DESC);
    Assert.assertEquals(expectedFieldName, fs.getFieldName());
    Assert.assertEquals(1, fs.getOrder().size());
    Assert.assertEquals(SortOrder.DESC.getOperator(), fs.getOrder().get(expectedFieldName));
  }

  @Test
  public void equalsContract() {
    EqualsVerifier.forClass(FieldSort.class).withIgnoredFields("cachedMap").verify();
  }
}
