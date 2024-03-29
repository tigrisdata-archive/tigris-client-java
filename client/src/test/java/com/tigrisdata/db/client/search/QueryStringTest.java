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
import com.tigrisdata.db.client.config.TigrisConfiguration;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Assert;
import org.junit.Test;

public class QueryStringTest {
  private static final ObjectMapper DEFAULT_OBJECT_MAPPER =
      TigrisConfiguration.newBuilder("test", "db1").build().getObjectMapper();

  @Test
  public void matchAll() {
    Assert.assertEquals("", QueryString.getMatchAllQuery().getQ());
  }

  @Test
  public void toJSONSerialization() {
    QueryString queryString = QueryString.newBuilder("some query").build();
    Assert.assertEquals("some query", queryString.toJSON(DEFAULT_OBJECT_MAPPER));
  }

  @Test
  public void buildWithNull() {
    Exception thrown =
        Assert.assertThrows(
            IllegalArgumentException.class, () -> QueryString.newBuilder(null).build());
    Assert.assertEquals("Query cannot be null", thrown.getMessage());
  }

  @Test
  public void equalsContract() {
    EqualsVerifier.forClass(QueryString.class).verify();
  }
}
