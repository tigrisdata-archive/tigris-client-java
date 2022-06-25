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

import com.tigrisdata.db.client.Filters;
import com.tigrisdata.db.client.ReadFields;
import com.tigrisdata.db.client.TigrisFilter;
import org.junit.Assert;
import org.junit.Test;

public class SearchRequestTest {

  // TODO: Add sort orders to this test
  @Test
  public void build() {
    Query expectedQuery = QueryString.getMatchAllQuery();
    SearchFields expectedSearchFields = SearchFields.newBuilder().withField("field_1").build();
    TigrisFilter expectedFilter = Filters.eq("field_2", "otherValue");
    FacetQuery expectedFacetQuery = FacetFieldsQuery.newBuilder().withField("field_3").build();
    ReadFields expectedReadFields = ReadFields.newBuilder().includeField("field_1").build();

    SearchRequest actual =
        SearchRequest.newBuilder(expectedQuery)
            .withSearchFields(expectedSearchFields)
            .withFacetQuery(expectedFacetQuery)
            .withFilter(expectedFilter)
            .withReadFields(expectedReadFields)
            .build();

    Assert.assertNotNull(actual);
    Assert.assertEquals(expectedQuery, actual.getQuery());
    Assert.assertEquals(expectedSearchFields, actual.getSearchFields());
    Assert.assertEquals(expectedFacetQuery, actual.getFacetQuery());
    Assert.assertEquals(expectedFilter, actual.getFilter());
    Assert.assertEquals(expectedReadFields, actual.getReadFields());
    Assert.assertNull(actual.getSortOrders());
  }

  @Test
  public void buildWithVarargs() {
    Query expectedQuery = QueryString.newBuilder("some query").build();
    SearchFields expectedSearchFields = SearchFields.newBuilder().withField("field_1").build();
    FacetQuery expectedFacetQuery = FacetFieldsQuery.newBuilder().withField("field_3").build();

    SearchRequest actual =
        SearchRequest.newBuilder("some query")
            .withSearchFields("field_1")
            .withFacetFields("field_3")
            .build();
    Assert.assertEquals(expectedQuery, actual.getQuery());
    Assert.assertEquals(expectedSearchFields, actual.getSearchFields());
    Assert.assertEquals(expectedFacetQuery, actual.getFacetQuery());
  }

  @Test
  public void failsWithNullQuery() {
    Assert.assertThrows(
        NullPointerException.class, () -> SearchRequest.newBuilder((String) null).build());
  }
}
