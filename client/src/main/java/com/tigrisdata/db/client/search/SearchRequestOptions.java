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

/** Builder class for pagination options for search request */
public final class SearchRequestOptions {
  private static final int CURRENT_PAGE = 1;
  private static final int DEFAULT_PER_PAGE = 20;
  private static final SearchRequestOptions DEFAULT_INSTANCE = newBuilder().build();

  private final int page;
  private final int perPage;

  private SearchRequestOptions(Builder builder) {
    this.page = builder.page;
    this.perPage = builder.perPage;
  }

  /**
   * Gets the page number to fetch search results
   *
   * @return page number to fetch search results
   */
  public int getPage() {
    return page;
  }

  /**
   * Gets the number of results to fetch per page
   *
   * @return number of results to fetch per page
   */
  public int getPerPage() {
    return perPage;
  }

  /**
   * Gets default pagination options
   *
   * <p>Page numbers start at {@code 1} for first page
   *
   * <p>By default {@code 10} results will be fetched per page
   *
   * @return default {@link SearchRequestOptions}
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

    SearchRequestOptions options = (SearchRequestOptions) o;

    if (page != options.page) {
      return false;
    }
    return perPage == options.perPage;
  }

  @Override
  public int hashCode() {
    int result = page;
    result = 31 * result + perPage;
    return result;
  }

  /**
   * Builder API for {@link SearchRequestOptions}
   *
   * @return {@link SearchRequestOptions.Builder}
   */
  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {
    private int page;
    private int perPage;

    private Builder() {
      this.page = CURRENT_PAGE;
      this.perPage = DEFAULT_PER_PAGE;
    }

    /**
     * Sets the page number to fetch search results for
     *
     * @param page number
     * @return {@link SearchRequestOptions.Builder}
     */
    public Builder withPage(int page) {
      this.page = page;
      return this;
    }

    /**
     * Sets the number of results to fetch per page
     *
     * @param perPage number of results to fetch per page
     * @return {@link SearchRequestOptions.Builder}
     */
    public Builder withPerPage(int perPage) {
      this.perPage = perPage;
      return this;
    }

    /**
     * Builds {@link SearchRequestOptions}
     *
     * @return {@link SearchRequestOptions}
     */
    public SearchRequestOptions build() {
      return new SearchRequestOptions(this);
    }
  }
}
