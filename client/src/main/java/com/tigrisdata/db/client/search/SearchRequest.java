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
import java.util.Arrays;

/** Builder class to create a Search request */
public final class SearchRequest {

  private final Query query;
  private final SearchFields searchFields;
  private final TigrisFilter filter;
  private final FacetQuery facetQuery;
  private final SortOrder sortOrder;
  private final ReadFields readFields;

  private SearchRequest(Builder builder) {
    this.query = builder.query;
    this.filter = builder.filter;
    this.searchFields = builder.searchFields;
    this.facetQuery = builder.facetQuery;
    this.sortOrder = builder.sortOrder;
    this.readFields = builder.fields;
  }

  /**
   * Gets the search query associated with this request
   *
   * @return non-null {@link Query}
   */
  public Query getQuery() {
    return query;
  }

  /**
   * Gets Fields on which the search query is projected
   *
   * @return {@link SearchFields} or null
   */
  public SearchFields getSearchFields() {
    return this.searchFields;
  }

  /**
   * Gets the filters that will be applied to refine search results
   *
   * @return {@link TigrisFilter} or null
   */
  public TigrisFilter getFilter() {
    return filter;
  }

  /**
   * Gets the facet query to categorically arrange the indexed terms
   *
   * @return {@link FacetQuery} or null
   */
  public FacetQuery getFacetQuery() {
    return facetQuery;
  }

  /**
   * Gets the Order to sort the search results
   *
   * @return {@link SortOrder} or null
   */
  public SortOrder getSortOrders() {
    return sortOrder;
  }

  /**
   * Gets the document fields to include/exclude in search results
   *
   * @return {@link ReadFields} or null
   */
  public ReadFields getReadFields() {
    return readFields;
  }

  /**
   * Builder API for {@link SearchRequest}
   *
   * @return {@link SearchRequest.Builder} object
   */
  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {

    private Query query;
    private TigrisFilter filter;
    private SearchFields searchFields;
    private FacetQuery facetQuery;
    private SortOrder sortOrder;
    private ReadFields fields;

    private Builder() {
      this.query = QueryString.getMatchAllQuery();
    }

    /**
     * Optional - Sets the query text. Defaults to {@code QueryString.getMatchAllQuery()}
     *
     * @param q the query term
     * @return {@link SearchRequest.Builder}
     */
    public Builder withQuery(String q) {
      this.query = QueryString.newBuilder(q).build();
      return this;
    }

    /**
     * Optional - Sets the search fields to project search query on
     *
     * @param fields Collection field names
     * @return {@link SearchRequest.Builder}
     * @see #withSearchFields(SearchFields)
     */
    public Builder withSearchFields(String... fields) {
      this.searchFields = SearchFields.newBuilder().withFields(Arrays.asList(fields)).build();
      return this;
    }

    /**
     * Optional - Sets the fields to project search query on
     *
     * @param fields {@link SearchFields}
     * @return {@link SearchRequest.Builder}
     */
    public Builder withSearchFields(SearchFields fields) {
      this.searchFields = fields;
      return this;
    }

    /**
     * Optional - Sets the filter to further refine search results
     *
     * @param filter {@link TigrisFilter}
     * @return {@link SearchRequest.Builder}
     */
    public Builder withFilter(TigrisFilter filter) {
      this.filter = filter;
      return this;
    }

    /**
     * Optional - Sets the facet fields to categorically arrange indexed terms
     *
     * @param fields {@link }
     * @return {@link SearchRequest.Builder}
     * @see #withFacetQuery(FacetQuery)
     */
    public Builder withFacetFields(String... fields) {
      this.facetQuery = FacetFieldsQuery.newBuilder().withFields(Arrays.asList(fields)).build();
      return this;
    }

    /**
     * Optional - Sets the facet query to categorically arrange the indexed terms
     *
     * @param facetQuery {@link FacetQuery}
     * @return {@link SearchRequest.Builder}
     */
    public Builder withFacetQuery(FacetQuery facetQuery) {
      this.facetQuery = facetQuery;
      return this;
    }

    /**
     * Optional - Sets the SortOrder to sorts search results according to specified attributes and
     * indicated order
     *
     * @param sortOrder {@link SortOrder}
     * @return {@link SearchRequest.Builder}
     */
    public Builder withSortOrder(SortOrder sortOrder) {
      this.sortOrder = sortOrder;
      return this;
    }

    /**
     * Optional - Sets the document fields to include/exclude in search results
     *
     * @param fields {@link ReadFields}
     * @return {@link SearchRequest.Builder}
     */
    public Builder withReadFields(ReadFields fields) {
      this.fields = fields;
      return this;
    }

    /**
     * Constructs a {@link SearchRequest}
     *
     * @throws IllegalArgumentException if query is null
     * @return {@link SearchRequest}
     */
    public SearchRequest build() {
      if (this.query == null) {
        throw new IllegalArgumentException("Query cannot be null");
      }
      return new SearchRequest(this);
    }
  }
}
