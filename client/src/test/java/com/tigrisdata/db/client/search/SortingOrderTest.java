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

package com.tigrisdata.db.client.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tigrisdata.db.client.FieldSort;
import com.tigrisdata.db.client.Sort;
import com.tigrisdata.db.client.config.TigrisConfiguration;
import java.util.Arrays;
import java.util.Collection;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class SortingOrderTest {
  private static final ObjectMapper DEFAULT_OBJECT_MAPPER =
      TigrisConfiguration.newBuilder("test").build().getObjectMapper();

  @Parameter(0)
  public String testCaseLabel;

  @Parameter(1)
  public SortingOrder orderInput;

  @Parameter(2)
  public String expectedJSON;

  @Test
  public void sortingOrderToJSON() {
    Assert.assertEquals(expectedJSON, orderInput.toJSON(DEFAULT_OBJECT_MAPPER));
  }

  @Parameters(name = "{index}: {0}")
  public static Collection<Object[]> SortingOrders() {
    FieldSort field1_Asc = Sort.ascending("field_1");
    FieldSort field1_Desc = Sort.descending("field_1");
    FieldSort field2_Desc = Sort.descending("field_2");

    return Arrays.asList(
        new Object[][] {
          {"Empty sort order", SortingOrder.newBuilder().build(), "[]"},
          {
            "Duplicate fields",
            SortingOrder.newBuilder().withOrder(field1_Asc, field1_Desc).build(),
            "[{\"field_1\":\"$desc\"}]"
          },
          {
            "Single field",
            SortingOrder.newBuilder().withOrder(field2_Desc).build(),
            "[{\"field_2\":\"$desc\"}]"
          },
          {
            "Multiple fields",
            SortingOrder.newBuilder().withOrder(field2_Desc, field1_Asc).build(),
            "[{\"field_2\":\"$desc\"},{\"field_1\":\"$asc\"}]"
          }
        });
  }

  @Test
  public void equalsContract() {
    EqualsVerifier.forClass(SortingOrder.class).withIgnoredFields("cachedJSON").verify();
  }
}
