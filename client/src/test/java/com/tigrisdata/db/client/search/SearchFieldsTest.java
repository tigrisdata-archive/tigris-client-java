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

import static java.util.Collections.emptyList;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tigrisdata.db.client.config.TigrisConfiguration;
import java.util.Arrays;
import java.util.List;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Assert;
import org.junit.Test;

public class SearchFieldsTest {

  private static final ObjectMapper DEFAULT_OBJECT_MAPPER =
      TigrisConfiguration.newBuilder("test").build().getObjectMapper();

  @Test
  public void emptySearchFields() {
    Assert.assertTrue(SearchFields.empty().getFields().isEmpty());
  }

  @Test
  public void searchFieldsIsEmpty() {
    SearchFields actual = SearchFields.newBuilder().withFields(emptyList()).build();
    Assert.assertTrue(actual.getFields().isEmpty());
  }

  @Test
  public void builderAddOne() {
    String expected = "someField";
    SearchFields actual = SearchFields.newBuilder().withField(expected).build();
    Assert.assertEquals(1, actual.getFields().size());
    Assert.assertTrue(actual.getFields().contains(expected));
  }

  @Test
  public void builderAddAll() {
    List<String> expected = Arrays.asList("firstField", "secondField");
    SearchFields actual = SearchFields.newBuilder().withFields(expected).build();
    Assert.assertEquals(expected.size(), actual.getFields().size());
    Assert.assertTrue(actual.getFields().containsAll(expected));
  }

  @Test
  public void toJSONSerialization() {
    List<String> input = Arrays.asList("firstField", "secondField");
    String actual =
        SearchFields.newBuilder().withFields(input).build().toJSON(DEFAULT_OBJECT_MAPPER);
    String expected = "[\"firstField\",\"secondField\"]";
    Assert.assertNotNull(actual);
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void toJSONSerializationFailure() {
    SearchFields fields = SearchFields.empty();
    Exception thrown =
        Assert.assertThrows(
            IllegalStateException.class,
            () ->
                fields.toJSON(
                    new ObjectMapper() {
                      @Override
                      public String writeValueAsString(Object value)
                          throws JsonProcessingException {
                        throw new JsonParseException(null, "");
                      }
                    }));
    Assert.assertEquals(
        "This was caused because the SearchFields's JSON serialization raised errors",
        thrown.getMessage());
  }

  @Test
  public void equalsContract() {
    EqualsVerifier.forClass(SearchFields.class).verify();
  }
}
