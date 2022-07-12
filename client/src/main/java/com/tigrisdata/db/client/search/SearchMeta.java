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
  private final int totalPages;
  private final Page page;

  private SearchMeta(long found, int totalPages, Page page) {
    this.found = found;
    this.totalPages = totalPages;
    this.page = page;
  }

  /**
   * Gets total number of results for the search query
   *
   * @return total number of results for the search query
   */
  public long getFound() {
    return found;
  }

  /**
   * Gets total number of pages of the search results
   *
   * @return total number of pages of the search results
   */
  public int getTotalPages() {
    return totalPages;
  }

  /**
   * Gets current page information
   *
   * @return current page
   */
  public Page getPage() {
    return page;
  }

  /**
   * Conversion utility to create {@link SearchMeta} from server response
   *
   * @param resp {@link Api.SearchMetadata}
   * @return {@link SearchMeta}
   */
  static SearchMeta from(Api.SearchMetadata resp) {
    Objects.requireNonNull(resp);
    Page page = Page.from(resp.getPage());
    return new SearchMeta(resp.getFound(), resp.getTotalPages(), page);
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
    if (totalPages != that.totalPages) {
      return false;
    }
    return Objects.equals(page, that.page);
  }

  @Override
  public int hashCode() {
    int result = (int) (found ^ (found >>> 32));
    result = 31 * result + totalPages;
    result = 31 * result + (page != null ? page.hashCode() : 0);
    return result;
  }
}
