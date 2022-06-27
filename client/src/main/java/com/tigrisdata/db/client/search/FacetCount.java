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

/** Representation of {@link Api.FacetCount}, aggregate count of a value in field */
public final class FacetCount {

  private final String value;
  private final long count;

  private FacetCount(String value, long count) {
    this.value = value;
    this.count = count;
  }

  /**
   * Field's attribute value
   *
   * @return Field's attribute value
   */
  public String getValue() {
    return value;
  }

  /**
   * Count of field values in the search results
   *
   * @return count of field values in the search results
   */
  public long getCount() {
    return count;
  }

  /**
   * Conversion utility to create {@link FacetCount}
   *
   * @param resp {@link Api.FacetCount}
   */
  static FacetCount from(Api.FacetCount resp) {
    Objects.requireNonNull(resp);
    return new FacetCount(resp.getValue(), resp.getCount());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    FacetCount that = (FacetCount) o;

    if (count != that.count) {
      return false;
    }
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    int result = value != null ? value.hashCode() : 0;
    result = 31 * result + (int) (count ^ (count >>> 32));
    return result;
  }
}
