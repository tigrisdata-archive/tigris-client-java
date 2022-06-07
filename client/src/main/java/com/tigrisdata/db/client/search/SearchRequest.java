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

import com.tigrisdata.db.client.ReadFields;
import com.tigrisdata.db.client.TigrisFilter;
import java.util.Objects;

/** Builder to create /search request */
public final class SearchRequest {

  private final Query query;
  private final SearchFields searchFields;
  private final TigrisFilter filter;
  private final FacetQuery facetQuery;
  private final SortOrders sortOrders;
  private final ReadFields readFields;

  private SearchRequest(Builder builder) {
    this.query = builder.query;
    this.filter = builder.filter;
    this.searchFields = builder.searchFields;
    this.facetQuery = builder.facetQuery;
    this.sortOrders = builder.sortOrders;
    this.readFields = builder.fields;
  }

  /**
   * Builder API for {@link SearchRequest}
   *
   * @param query - Search query
   * @return {@link SearchRequest.Builder}
   */
  public static Builder newBuilder(Query query) {
    return new Builder(query);
  }

  /** Search query */
  public Query getQuery() {
    return query;
  }

  /** Fields on which the search query is projected */
  public SearchFields getSearchFields() {
    return this.searchFields;
  }

  /** Filters to further refine search results */
  public TigrisFilter getFilter() {
    return filter;
  }

  /** Facet query to categorically arrange the indexed terms */
  public FacetQuery getFacetQuery() {
    return facetQuery;
  }

  /** Order for ordering the search results */
  public SortOrders getSortOrders() {
    return sortOrders;
  }

  /** Document fields to include/exclude in search results */
  public ReadFields getReadFields() {
    return readFields;
  }

  public static final class Builder {

    private final Query query;
    private TigrisFilter filter;
    private SearchFields searchFields;
    private FacetQuery facetQuery;
    private SortOrders sortOrders;
    private ReadFields fields;

    private Builder(Query query) {
      this.query = query;
    }

    /** Optional fields to project search query */
    public Builder withSearchFields(SearchFields fields) {
      this.searchFields = fields;
      return this;
    }
    /** Optional filters to further refine search results */
    public Builder withFilter(TigrisFilter filter) {
      this.filter = filter;
      return this;
    }
    /** Optional facet query to categorically arrange the indexed terms */
    public Builder withFacetQuery(FacetQuery facetQuery) {
      this.facetQuery = facetQuery;
      return this;
    }
    /** Optional order for ordering the search results */
    public Builder withSortOrders(SortOrders sortOrders) {
      this.sortOrders = sortOrders;
      return this;
    }

    /** Optional document fields to include/exclude in search results */
    public Builder withReadFields(ReadFields fields) {
      this.fields = fields;
      return this;
    }

    /**
     * Builds a {@link SearchRequest}
     *
     * @throws NullPointerException if query is missing
     */
    public SearchRequest build() {
      Objects.requireNonNull(this.query);
      return new SearchRequest(this);
    }
  }
}
