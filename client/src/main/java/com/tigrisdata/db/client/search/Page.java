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

/** Representation of {@link Api.Page}m pagination information associated with search results */
public final class Page {

  private final int current;
  private final int size;

  private Page(int current, int size) {
    this.current = current;
    this.size = size;
  }

  /**
   * Gets current page number for the paginated search results
   *
   * @return current page number for the paginated search results
   */
  public int getCurrent() {
    return current;
  }

  /**
   * Gets maximum number of search results included per page
   *
   * @return maximum number of search results included per page
   */
  public int getSize() {
    return size;
  }

  /**
   * Conversion utility to create {@link Page} from server response
   *
   * @param resp {@link Api.Page}
   * @return {@link Page}
   */
  static Page from(Api.Page resp) {
    Objects.requireNonNull(resp);
    return new Page(resp.getCurrent(), resp.getSize());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Page page = (Page) o;

    if (current != page.current) {
      return false;
    }
    return size == page.size;
  }

  @Override
  public int hashCode() {
    int result = current;
    result = 31 * result + size;
    return result;
  }
}
