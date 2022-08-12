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

import java.util.Map;

/** Represents the sorting order */
public interface TigrisSort {

  /**
   * Sort order representation as Key, Value pairs
   *
   * @return non-null immutable {@link Map}
   */
  public Map<String, Object> toMap();

  /**
   * Gets collection field name that this order represents
   *
   * @return {@link String}
   */
  public String getFieldName();
}
