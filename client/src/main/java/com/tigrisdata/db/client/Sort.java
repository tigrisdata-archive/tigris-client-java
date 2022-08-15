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

package com.tigrisdata.db.client;

/** Helper class to construct {@link TigrisSort} orders */
public final class Sort {
  private Sort() {}

  /**
   * Creates ascending sort order for given field name
   *
   * @param fieldName collection field name
   * @return constructed {@link FieldSort} of ascending order
   */
  public static FieldSort ascending(String fieldName) {
    return new FieldSort(fieldName, SortOrder.ASC);
  }

  /**
   * Creates descending sort order for given field name
   *
   * @param fieldName collection field name
   * @return constructed {@link FieldSort} of descending order
   */
  public static FieldSort descending(String fieldName) {
    return new FieldSort(fieldName, SortOrder.DESC);
  }
}
