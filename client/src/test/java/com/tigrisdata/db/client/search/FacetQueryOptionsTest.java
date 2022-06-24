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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tigrisdata.db.client.config.TigrisConfiguration;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Assert;
import org.junit.Test;

public class FacetQueryOptionsTest {

  private static final ObjectMapper DEFAULT_OBJECT_MAPPER =
      TigrisConfiguration.newBuilder("test").build().getObjectMapper();

  @Test
  public void defaultInstance() {
    FacetQueryOptions expected =
        FacetQueryOptions.newBuilder().withType(FacetFieldType.VALUE).withSize(10).build();
    FacetQueryOptions actual = FacetQueryOptions.getDefaultInstance();
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void withNullType() {
    FacetQueryOptions actual = FacetQueryOptions.newBuilder().withType(null).withSize(20).build();
    Assert.assertEquals(FacetFieldType.VALUE, actual.getType());
    Assert.assertEquals(20, actual.getSize());
  }

  @Test
  public void equalsContract() {
    EqualsVerifier.forClass(FacetQueryOptions.class).verify();
  }

  @Test
  public void toJSONSerialization() {
    FacetQueryOptions options = FacetQueryOptions.newBuilder().withSize(20).build();
    String expected = "{\"size\":\"20\",\"type\":\"value\"}";
    String actual = options.toJSON(DEFAULT_OBJECT_MAPPER);
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void toJSONSerializationFailure() {
    FacetQueryOptions options = FacetQueryOptions.getDefaultInstance();
    try {
      options.toJSON(
          new ObjectMapper() {
            @Override
            public String writeValueAsString(Object value) throws JsonProcessingException {
              throw new JsonParseException(null, "");
            }
          });
    } catch (IllegalArgumentException e) {
      Assert.assertEquals("Failed to serialize FacetFieldOption to JSON", e.getMessage());
    }
  }
}
