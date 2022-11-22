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

import static java.lang.String.format;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.io.JsonEOFException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tigrisdata.db.client.config.TigrisConfiguration;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class FiltersTest {

  private static final ObjectMapper DEFAULT_OBJECT_MAPPER =
      TigrisConfiguration.newBuilder("test", "db1").build().getObjectMapper();

  @Parameter(0)
  public String testCaseLabel;

  @Parameter(1)
  public TigrisFilter filterInput;

  @Parameter(2)
  public String expectedJSON;

  @Test
  public void filterToJSON() {
    Assert.assertEquals(expectedJSON, filterInput.toJSON(DEFAULT_OBJECT_MAPPER));
  }

  @Parameters(name = "{index}: {0}")
  public static Collection<Object[]> filters() {
    TigrisFilter orFilter =
        Filters.or(Filters.lt("k1", 123), Filters.eq("k2", false), Filters.gt("k3", 45.0));
    String orFilterSerialized =
        "{\"$or\":[{\"k1\":{\"$lt\":123}},{\"k2\":false},{\"k3\":{\"$gt\":45.0}}]}";

    TigrisFilter andFilter =
        Filters.and(
            Filters.gte("k1", 24.56f), Filters.lte("k3", 29.56f), Filters.eq("k2", "label"));
    String andFilterSerialized =
        "{\"$and\":[{\"k1\":{\"$gte\":24.56}},{\"k3\":{\"$lte\":29.56}},{\"k2\":\"label\"}]}";

    TigrisFilter nestedOr = Filters.or(orFilter, andFilter);
    String nestedOrSerialized =
        format("{\"$or\":[%s,%s]}", orFilterSerialized, andFilterSerialized);

    TigrisFilter nestedAnd =
        Filters.and(
            andFilter, orFilter, Filters.and(Filters.gt("k1", 10), Filters.lte("k1", 18.5f)));
    String nestedAndSerialized =
        format(
            "{\"$and\":[%s,%s,{\"$and\":[{\"k1\":{\"$gt\":10}},{\"k1\":{\"$lte\":18.5}}]}]}",
            andFilterSerialized, orFilterSerialized);

    return Arrays.asList(
        new Object[][] {
          {"Nothing filter", Filters.nothing(), "{}"},
          {"Eq - number", Filters.eq("k1", 123), "{\"k1\":123}"},
          {"Eq - boolean", Filters.eq("k1", false), "{\"k1\":false}"},
          {"Eq - string", Filters.eq("k4", "val1"), "{\"k4\":\"val1\"}"},
          {
            "Eq - UUID",
            Filters.eq("uuidField", UUID.fromString("aa8f8da5-5fd6-4660-a348-9ed7fe96253a")),
            "{\"uuidField\":\"aa8f8da5-5fd6-4660-a348-9ed7fe96253a\"}"
          },
          {"Lt - integer", Filters.lt("k1", 123), "{\"k1\":{\"$lt\":123}}"},
          {"Lt - long", Filters.lt("k1", 123L), "{\"k1\":{\"$lt\":123}}"},
          {"Lt - float", Filters.lt("k1", 123.01f), "{\"k1\":{\"$lt\":123.01}}"},
          {"Lt - double", Filters.lt("k1", 123.05), "{\"k1\":{\"$lt\":123.05}}"},
          {"Lte - integer", Filters.lte("k1", 123), "{\"k1\":{\"$lte\":123}}"},
          {"Lte - long", Filters.lte("k1", 123L), "{\"k1\":{\"$lte\":123}}"},
          {"Lte - float", Filters.lte("k1", 123.01f), "{\"k1\":{\"$lte\":123.01}}"},
          {"Lte - double", Filters.lte("k1", 123.05), "{\"k1\":{\"$lte\":123.05}}"},
          {"Gt - integer", Filters.gt("k1", 123), "{\"k1\":{\"$gt\":123}}"},
          {"Gt - long", Filters.gt("k1", 123L), "{\"k1\":{\"$gt\":123}}"},
          {"Gt - float", Filters.gt("k1", 123.01f), "{\"k1\":{\"$gt\":123.01}}"},
          {"Gt - double", Filters.gt("k1", 123.05), "{\"k1\":{\"$gt\":123.05}}"},
          {"Gte - integer", Filters.gte("k1", 123), "{\"k1\":{\"$gte\":123}}"},
          {"Gte - long", Filters.gte("k1", 123L), "{\"k1\":{\"$gte\":123}}"},
          {"Gte - float", Filters.gte("k1", 123.01f), "{\"k1\":{\"$gte\":123.01}}"},
          {"Gte - double", Filters.gte("k1", 123.05), "{\"k1\":{\"$gte\":123.05}}"},
          {"OR - eq, lt, gt", orFilter, orFilterSerialized},
          {"AND - gte, lte, eq", andFilter, andFilterSerialized},
          {"Nested - OR", nestedOr, nestedOrSerialized},
          {"Nested - AND", nestedAnd, nestedAndSerialized},
        });
  }

  @Test(expected = IllegalArgumentException.class)
  public void invalidCompositeFilterTest1() {
    //noinspection ResultOfMethodCallIgnored
    Filters.or(Filters.eq("k1", 123));
  }

  @Test(expected = IllegalArgumentException.class)
  public void invalidCompositeFilterTest2() {
    //noinspection ResultOfMethodCallIgnored
    Filters.and(Filters.eq("k1", 123));
  }

  // this is never expected to be the case in normal scenario
  // this test verifies the error message
  @Test
  public void selectorFailureJsonSerializationFailure() {
    ObjectMapper objectMapper =
        new ObjectMapper() {
          @Override
          public String writeValueAsString(Object value) throws JsonProcessingException {
            throw new JsonEOFException(null, null, null);
          }
        };
    SelectorFilter<Long> filter = Filters.eq("id", 0L);

    try {
      filter.toJSON(objectMapper);
      Assert.fail("This must fail");
    } catch (IllegalStateException illegalStateException) {
      Assert.assertEquals(
          "This was caused because the SelectorFilter's JSON serialization raised errors",
          illegalStateException.getMessage());
    }
  }
}
