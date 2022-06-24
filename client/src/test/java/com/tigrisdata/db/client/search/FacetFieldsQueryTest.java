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
import java.util.Collections;
import java.util.Map;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Assert;
import org.junit.Test;

public class FacetFieldsQueryTest {

  private static final ObjectMapper DEFAULT_OBJECT_MAPPER =
      TigrisConfiguration.newBuilder("test").build().getObjectMapper();

  @Test
  public void empty() {
    FacetFieldsQuery fields = FacetFieldsQuery.empty();
    Assert.assertTrue(fields.isEmpty());
  }

  @Test
  public void equalsContract() {
    EqualsVerifier.forClass(FacetFieldsQuery.class).verify();
  }

  @Test
  public void buildWithFields() {
    Map<String, FacetQueryOptions> optionsMap =
        Collections.singletonMap("someField", FacetQueryOptions.newBuilder().withSize(30).build());
    FacetFieldsQuery fields = FacetFieldsQuery.newBuilder().addAll(optionsMap).build();
    Assert.assertEquals(optionsMap, fields.getFacetFields());
  }

  @Test
  public void buildWithOptions() {
    FacetFieldsQuery query =
        FacetFieldsQuery.newBuilder()
            .withFieldOptions("name", FacetQueryOptions.newBuilder().withSize(20).build())
            .build();
    Assert.assertTrue(query.getFacetFields().containsKey("name"));
  }

  @Test
  public void toJSONSerialization() {
    Map<String, FacetQueryOptions> optionsMap =
        Collections.singletonMap("someField", FacetQueryOptions.newBuilder().withSize(30).build());
    FacetFieldsQuery input = FacetFieldsQuery.newBuilder().addAll(optionsMap).build();
    String expected = "{\"someField\":{\"size\":\"30\",\"type\":\"value\"}}";
    Assert.assertEquals(expected, input.toJSON(DEFAULT_OBJECT_MAPPER));
  }

  @Test
  public void toJSONSerializationFailure() {
    FacetFieldsQuery fields = FacetFieldsQuery.empty();
    try {
      fields.toJSON(
          new ObjectMapper() {
            @Override
            public String writeValueAsString(Object value) throws JsonProcessingException {
              throw new JsonParseException(null, "");
            }
          });
    } catch (IllegalArgumentException e) {
      Assert.assertEquals("Failed to serialize FacetFields to JSON", e.getMessage());
    }
  }
}
