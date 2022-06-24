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

import com.tigrisdata.db.api.v1.grpc.Api;
import java.util.Objects;

/** Representation of {@link Api.SearchMetadata}, information associated with /search results */
public final class SearchMeta {

  private final long found;
  private final long currentPage;
  private final long totalPages;
  private final int perPage;

  private SearchMeta(long found, long currentPage, long totalPages, int perPage) {
    this.found = found;
    this.currentPage = currentPage;
    this.totalPages = totalPages;
    this.perPage = perPage;
  }

  /** Total number of matches for the search query */
  public long getFound() {
    return found;
  }

  /** Current page number for the paginated search results */
  public long getCurrentPage() {
    return currentPage;
  }

  /** Total number pages for the search results */
  public long getTotalPages() {
    return totalPages;
  }

  /** Number of search results displayed per page */
  public int getPerPage() {
    return perPage;
  }

  /**
   * Conversion utility to create {@link SearchMeta} from server response
   *
   * @param resp {@link Api.SearchMetadata}
   */
  static SearchMeta from(Api.SearchMetadata resp) {
    Objects.requireNonNull(resp);
    Api.Page page = resp.getPage();
    return new SearchMeta(resp.getFound(), page.getCurrent(), page.getTotal(), page.getPerPage());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    SearchMeta that = (SearchMeta) o;

    if (found != that.found) {
      return false;
    }
    if (currentPage != that.currentPage) {
      return false;
    }
    if (totalPages != that.totalPages) {
      return false;
    }
    return perPage == that.perPage;
  }

  @Override
  public int hashCode() {
    int result = (int) (found ^ (found >>> 32));
    result = 31 * result + (int) (currentPage ^ (currentPage >>> 32));
    result = 31 * result + (int) (totalPages ^ (totalPages >>> 32));
    result = 31 * result + perPage;
    return result;
  }
}
