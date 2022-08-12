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

import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

public class SortTest {

  @Test
  public void testAscending() {
    TigrisSort sort = Sort.ascending("field_1");
    Map<String, String> expected =
        new HashMap<String, String>() {
          {
            put("field_1", SortingOperator.ASC.getOperator());
          }
        };

    Assert.assertEquals(expected, sort.toMap());
  }

  @Test
  public void testDescending() {
    TigrisSort sort = Sort.descending("field_1");
    Map<String, String> expected =
        new HashMap<String, String>() {
          {
            put("field_1", SortingOperator.DESC.getOperator());
          }
        };

    Assert.assertEquals(expected, sort.toMap());
  }
}
