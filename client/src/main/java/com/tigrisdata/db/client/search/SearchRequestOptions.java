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

/** Pagination options for search request */
public final class SearchRequestOptions {
  private static final long CURRENT_PAGE = 1;
  private static final long DEFAULT_PER_PAGE = 10;
  private static final SearchRequestOptions DEFAULT_INSTANCE = newBuilder().build();

  private final long page;
  private final long perPage;

  private SearchRequestOptions(Builder builder) {
    this.page = builder.page;
    this.perPage = builder.perPage;
  }

  /** Results from this specific page number would be fetched */
  public long getPage() {
    return page;
  }

  /** Number of results to fetch per page */
  public long getPerPage() {
    return perPage;
  }

  /**
   * Get default pagination options
   * <li>Page numbers start at `1` for first page
   * <li>By default 10 results will be fetched per page
   */
  public static SearchRequestOptions getDefault() {
    return DEFAULT_INSTANCE;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    SearchRequestOptions that = (SearchRequestOptions) o;

    if (page != that.page) {
      return false;
    }
    return perPage == that.perPage;
  }

  @Override
  public int hashCode() {
    int result = (int) (page ^ (page >>> 32));
    result = 31 * result + (int) (perPage ^ (perPage >>> 32));
    return result;
  }

  /** Builder API for {@link SearchRequestOptions} */
  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {
    private long page;
    private long perPage;

    private Builder() {
      this.page = CURRENT_PAGE;
      this.perPage = DEFAULT_PER_PAGE;
    }

    /** Results from this page number will be fetched */
    public Builder withPage(long page) {
      this.page = page;
      return this;
    }

    /** Number of results to fetch per page */
    public Builder withPerPage(long perPage) {
      this.perPage = perPage;
      return this;
    }

    /** Builds {@link SearchRequestOptions} */
    public SearchRequestOptions build() {
      return new SearchRequestOptions(this);
    }
  }
}
