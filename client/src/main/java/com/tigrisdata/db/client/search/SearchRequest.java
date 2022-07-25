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

import com.tigrisdata.db.client.TigrisFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Builder class to create a Search request */
public final class SearchRequest {

  private final Query query;
  private final SearchFields searchFields;
  private final TigrisFilter filter;
  private final FacetQuery facetQuery;
  private final SortOrder sortOrder;
  private final List<String> includeFields;
  private final List<String> excludeFields;

  private SearchRequest(Builder builder) {
    this.query = builder.query;
    this.filter = builder.filter;
    this.searchFields = builder.searchFields;
    this.facetQuery = builder.facetQuery;
    this.sortOrder = builder.sortOrder;
    this.includeFields = Collections.unmodifiableList(builder.includeFields);
    this.excludeFields = Collections.unmodifiableList(builder.excludeFields);
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
   * Gets list of fields to include in returned documents
   *
   * @return non-null immutable {@link List}
   */
  public List<String> getIncludeFields() {
    return includeFields;
  }

  /**
   * Gets list of fields to exclude from returned documents
   *
   * @return non-null immutable {@link List}
   */
  public List<String> getExcludeFields() {
    return excludeFields;
  }

  /**
   * Builder API for {@link SearchRequest}
   *
   * @return {@link SearchRequest.Builder} object
   */
  public static Builder newBuilder() {
    return new Builder();
  }

  /**
   * Builder object that matches all documents in the collection
   *
   * <p>Note: Avoid using {@code withQuery()} construct with this builder, it may override pre-built
   * match all query syntax and may not function as expected.
   *
   * @return {@link SearchRequest.Builder} object that includes a match all {@code query}
   */
  public static Builder matchAll() {
    return newBuilder();
  }

  public static final class Builder {

    private Query query;
    private TigrisFilter filter;
    private SearchFields searchFields;
    private FacetQuery facetQuery;
    private SortOrder sortOrder;
    private final List<String> includeFields;
    private final List<String> excludeFields;

    private Builder() {
      this.query = QueryString.getMatchAllQuery();
      this.includeFields = new ArrayList<>();
      this.excludeFields = new ArrayList<>();
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
     * @param fields Collection field names
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
     * Optional - Sets collection fields to include in returned documents
     *
     * @param fields Collection field names
     * @return {@link SearchRequest.Builder}
     */
    public Builder withIncludeFields(String... fields) {
      this.includeFields.addAll(Arrays.asList(fields));
      return this;
    }

    /**
     * Optional - Sets collection fields to exclude from returned documents
     *
     * @param fields Collection field names
     * @return {@link SearchRequest.Builder}
     */
    public Builder withExcludeFields(String... fields) {
      this.excludeFields.addAll(Arrays.asList(fields));
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
