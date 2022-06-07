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

/** Data class for "string" based /search queries */
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
   */
  public static QueryString getMatchAllQuery() {
    return MATCH_ALL_QUERY;
  }

  /** Get query string */
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
   * Builder object to build {@link QueryString}
   *
   * @param q Query string
   */
  public static Builder newBuilder(String q) {
    return new Builder(q);
  }

  public static final class Builder {

    private String q;

    private Builder(String searchString) {
      this.q = searchString;
    }

    /** Builds a {@link QueryString} */
    public QueryString build() {
      if (q == null) {
        q = MATCH_ALL_STRING;
      }
      return new QueryString(this.q);
    }
  }
}
