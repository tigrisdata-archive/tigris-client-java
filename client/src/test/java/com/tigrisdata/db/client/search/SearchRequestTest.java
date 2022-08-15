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

import com.tigrisdata.db.client.FieldSort;
import com.tigrisdata.db.client.Filters;
import com.tigrisdata.db.client.Sort;
import com.tigrisdata.db.client.TigrisFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class SearchRequestTest {

  @Test
  public void build() {
    QueryString expectedQuery = QueryString.getMatchAllQuery();
    SearchFields expectedSearchFields = SearchFields.newBuilder().withField("field_1").build();
    TigrisFilter expectedFilter = Filters.eq("field_2", "otherValue");
    FacetQuery expectedFacetQuery = FacetFieldsQuery.newBuilder().withField("field_3").build();
    SortingOrder expectedSortingOrder =
        SortingOrder.newBuilder().withOrder(Sort.ascending("field_4")).build();
    List<String> expectedIncludeFields = Collections.singletonList("field_1");

    SearchRequest actual =
        SearchRequest.newBuilder()
            .withQuery(expectedQuery.getQ())
            .withSearchFields(expectedSearchFields)
            .withFacetQuery(expectedFacetQuery)
            .withFilter(expectedFilter)
            .withIncludeFields(expectedIncludeFields.get(0))
            .withSort(expectedSortingOrder)
            .build();

    Assert.assertNotNull(actual);
    Assert.assertEquals(expectedQuery, actual.getQuery());
    Assert.assertEquals(expectedSearchFields, actual.getSearchFields());
    Assert.assertEquals(expectedFacetQuery, actual.getFacetQuery());
    Assert.assertEquals(expectedFilter, actual.getFilter());
    Assert.assertEquals(expectedIncludeFields, actual.getIncludeFields());
    Assert.assertEquals(0, actual.getExcludeFields().size());
    Assert.assertEquals(expectedSortingOrder, actual.getSortingOrder());
  }

  @Test
  public void buildWithVarargs() {
    Query expectedQuery = QueryString.newBuilder("some query").build();
    SearchFields expectedSearchFields = SearchFields.newBuilder().withField("field_1").build();
    FacetQuery expectedFacetQuery = FacetFieldsQuery.newBuilder().withField("field_3").build();
    FieldSort expectedSort = Sort.descending("field_4");
    SearchRequest actual =
        SearchRequest.newBuilder()
            .withQuery("some query")
            .withSearchFields("field_1")
            .withFacetFields("field_3")
            .withExcludeFields("field_4", "field_5")
            .withIncludeFields("field_6")
            .withSort(expectedSort)
            .build();
    Assert.assertEquals(expectedQuery, actual.getQuery());
    Assert.assertEquals(expectedSearchFields, actual.getSearchFields());
    Assert.assertEquals(expectedFacetQuery, actual.getFacetQuery());
    Assert.assertEquals(Arrays.asList("field_6"), actual.getIncludeFields());
    Assert.assertEquals(Arrays.asList("field_4", "field_5"), actual.getExcludeFields());
    Assert.assertEquals(Collections.singletonList(expectedSort), actual.getSortingOrder().get());
  }

  @Test
  public void emptyBuild() {
    SearchRequest built = SearchRequest.newBuilder().build();
    Assert.assertEquals(built.getQuery(), QueryString.getMatchAllQuery());
  }

  @Test
  public void matchAllBuild() {
    SearchRequest built = SearchRequest.matchAll().build();
    Assert.assertEquals(built.getQuery(), QueryString.getMatchAllQuery());
  }

  @Test
  public void failsWithNullQuery() {
    Exception thrown =
        Assert.assertThrows(
            IllegalArgumentException.class,
            () -> SearchRequest.newBuilder().withQuery(null).build());
    Assert.assertEquals("Query cannot be null", thrown.getMessage());
  }
}
