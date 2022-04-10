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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.io.JsonEOFException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tigrisdata.db.client.config.TigrisDBConfiguration;
import org.junit.Assert;
import org.junit.Test;

public class FiltersTest {
  private static ObjectMapper DEFAULT_OBJECT_MAPPER =
      TigrisDBConfiguration.newBuilder("test").build().getObjectMapper();

  @Test
  public void equalFilterTest() {
    Assert.assertEquals("{\"k1\":123}", Filters.eq("k1", 123).toJSON(DEFAULT_OBJECT_MAPPER));
    Assert.assertEquals("{\"k2\":false}", Filters.eq("k2", false).toJSON(DEFAULT_OBJECT_MAPPER));
    Assert.assertEquals("{\"k3\":true}", Filters.eq("k3", true).toJSON(DEFAULT_OBJECT_MAPPER));
    Assert.assertEquals(
        "{\"k4\":\"val1\"}", Filters.eq("k4", "val1").toJSON(DEFAULT_OBJECT_MAPPER));
  }

  @Test
  public void orFilterTest() {
    Assert.assertEquals(
        "{\"$or\":[{\"k1\":123},{\"k2\":false},{\"k3\":\"val3\"}]",
        Filters.or(Filters.eq("k1", 123), Filters.eq("k2", false), Filters.eq("k3", "val3"))
            .toJSON(DEFAULT_OBJECT_MAPPER));
  }

  @Test
  public void andFilterTest() {
    Assert.assertEquals(
        "{\"$and\":[{\"k1\":123},{\"k2\":false},{\"k3\":\"val3\"}]",
        Filters.and(Filters.eq("k1", 123), Filters.eq("k2", false), Filters.eq("k3", "val3"))
            .toJSON(DEFAULT_OBJECT_MAPPER));
  }

  @Test
  public void nestedFilterTest1() {
    TigrisFilter filter1 =
        Filters.and(Filters.eq("k1", 123), Filters.eq("k2", false), Filters.eq("k3", "val3"));
    TigrisFilter filter2 =
        Filters.and(Filters.eq("k1", 456), Filters.eq("k2", false), Filters.eq("k3", "val4"));

    Assert.assertEquals(
        "{\"$or\":[{\"$and\":[{\"k1\":123},{\"k2\":false},{\"k3\":\"val3\"}],"
            + "{\"$and\":[{\"k1\":456},{\"k2\":false},{\"k3\":\"val4\"}]]",
        Filters.or(filter1, filter2).toJSON(DEFAULT_OBJECT_MAPPER));
  }

  @Test
  public void nestedFilterTest2() {
    TigrisFilter filter1 =
        Filters.and(Filters.eq("k1", 123), Filters.eq("k2", false), Filters.eq("k3", "val3"));
    TigrisFilter filter2 =
        Filters.and(Filters.eq("k4", 456), Filters.eq("k5", false), Filters.eq("k6", "val4"));

    Assert.assertEquals(
        "{\"$and\":[{\"$and\":[{\"k1\":123},{\"k2\":false},{\"k3\":\"val3\"}],"
            + "{\"$and\":[{\"k4\":456},{\"k5\":false},{\"k6\":\"val4\"}]]",
        Filters.and(filter1, filter2).toJSON(DEFAULT_OBJECT_MAPPER));
  }

  @Test
  public void nestedFilterTest3() {
    TigrisFilter filter1 =
        Filters.or(Filters.eq("k1", 123), Filters.eq("k2", false), Filters.eq("k3", "val3"));
    TigrisFilter filter2 =
        Filters.or(Filters.eq("k1", 456), Filters.eq("k2", false), Filters.eq("k3", "val4"));

    Assert.assertEquals(
        "{\"$and\":[{\"$or\":[{\"k1\":123},{\"k2\":false},{\"k3\":\"val3\"}],"
            + "{\"$or\":[{\"k1\":456},{\"k2\":false},{\"k3\":\"val4\"}]]",
        Filters.and(filter1, filter2).toJSON(DEFAULT_OBJECT_MAPPER));
  }

  @Test
  public void nestedFilterTest4() {
    TigrisFilter filter1 =
        Filters.or(Filters.eq("k1", 123), Filters.eq("k2", false), Filters.eq("k3", "val3"));
    TigrisFilter filter2 =
        Filters.or(Filters.eq("k4", 456), Filters.eq("k5", false), Filters.eq("k6", "val4"));

    Assert.assertEquals(
        "{\"$and\":[{\"$or\":[{\"k1\":123},{\"k2\":false},{\"k3\":\"val3\"}],"
            + "{\"$or\":[{\"k4\":456},{\"k5\":false},{\"k6\":\"val4\"}]]",
        Filters.and(filter1, filter2).toJSON(DEFAULT_OBJECT_MAPPER));
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

  @Test
  public void equalsTest() {
    SelectorFilter<String> filter1 = Filters.eq("id", "val1");
    SelectorFilter<String> filter2 = Filters.eq("id", "val1");
    Assert.assertEquals(filter1, filter1);
    Assert.assertEquals(filter1, filter2);
    Assert.assertFalse(filter1.equals(null));
    Assert.assertFalse(filter1.equals("some-string"));

    SelectorFilter<String> filter3 = Filters.eq("id3", "val3");
    SelectorFilter<String> filter4 = Filters.eq("id4", "val4");
    Assert.assertFalse(filter3.equals(filter4));

    SelectorFilter<String> filter5 = new SelectorFilter<>(null, "id5", "Val5");
    SelectorFilter<String> filter6 = new SelectorFilter<>(ComparisonOperator.EQUALS, "id6", "Val6");
    Assert.assertFalse(filter5.equals(filter6));
  }

  @Test
  public void hashCodeTest() {
    SelectorFilter<String> filter1 = Filters.eq("id", "val1");
    SelectorFilter<String> filter2 = Filters.eq("id", "val1");
    Assert.assertEquals(filter1.hashCode(), filter2.hashCode());

    SelectorFilter<String> filter31 = new SelectorFilter<>(null, "id3", "val3");
    SelectorFilter<String> filter32 = new SelectorFilter<>(null, "id3", "val3");
    Assert.assertEquals(filter31.hashCode(), filter32.hashCode());

    SelectorFilter<String> filter41 = new SelectorFilter<>(ComparisonOperator.EQUALS, null, "val4");
    SelectorFilter<String> filter42 = new SelectorFilter<>(ComparisonOperator.EQUALS, null, "val4");
    Assert.assertEquals(filter41.hashCode(), filter42.hashCode());

    SelectorFilter<String> filter51 = new SelectorFilter<>(ComparisonOperator.EQUALS, "id5", null);
    SelectorFilter<String> filter52 = new SelectorFilter<>(ComparisonOperator.EQUALS, "id5", null);
    Assert.assertEquals(filter51.hashCode(), filter52.hashCode());
  }
}
