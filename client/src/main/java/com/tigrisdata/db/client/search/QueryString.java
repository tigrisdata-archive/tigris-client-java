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
import java.util.Objects;

/** Data class to construct "string" based search queries */
public final class QueryString implements Query {
  private static final String MATCH_ALL_STRING = "";
  private static final QueryString MATCH_ALL_QUERY = new QueryString(MATCH_ALL_STRING);

  private final String q;

  private QueryString(String q) {
    this.q = q;
  }

  /**
   * Perform a placeholder search. Returns all searchable documents, modified by any other search
   * parameters used.
   *
   * @return placeholder search query
   */
  public static QueryString getMatchAllQuery() {
    return MATCH_ALL_QUERY;
  }

  /**
   * Gets query string
   *
   * @return {@link String} query
   */
  public String getQ() {
    return q;
  }

  @Override
  public String toJSON(ObjectMapper objectMapper) {
    return q;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    QueryString that = (QueryString) o;

    return Objects.equals(q, that.q);
  }

  @Override
  public int hashCode() {
    return q != null ? q.hashCode() : 0;
  }

  /**
   * Builder API for {@link QueryString}
   *
   * @param q string query to search for
   * @return {@link QueryString.Builder} object
   * @see #getMatchAllQuery()
   */
  public static Builder newBuilder(String q) {
    return new Builder(q);
  }

  public static final class Builder {

    private final String q;

    private Builder(String queryString) {
      this.q = queryString;
    }

    /**
     * Constructs a {@link QueryString}
     *
     * @return {@link QueryString}
     * @throws IllegalArgumentException if query string is null
     * @see #getMatchAllQuery()
     */
    public QueryString build() {
      if (this.q == null) {
        throw new IllegalArgumentException("Query cannot be null");
      }
      return new QueryString(this.q);
    }
  }
}
