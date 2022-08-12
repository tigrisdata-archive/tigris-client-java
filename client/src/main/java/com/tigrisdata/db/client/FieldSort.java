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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/** Represents a collection field and corresponding sort order */
public final class FieldSort implements TigrisSort {

  private final String fieldName;
  private final SortingOperator operator;
  private Map<String, Object> cachedMap;

  FieldSort(String fieldName, SortingOperator op) {
    this.fieldName = fieldName;
    this.operator = op;
  }

  /** {@inheritDoc} */
  @Override
  public Map<String, Object> toMap() {
    if (cachedMap == null) {
      cachedMap =
          Collections.unmodifiableMap(
              new HashMap<String, String>() {
                {
                  put(fieldName, operator.getOperator());
                }
              });
    }
    return cachedMap;
  }

  /** {@inheritDoc} */
  @Override
  public String getFieldName() {
    return fieldName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    FieldSort fieldSort = (FieldSort) o;

    if (!Objects.equals(fieldName, fieldSort.fieldName)) {
      return false;
    }
    return operator == fieldSort.operator;
  }

  @Override
  public int hashCode() {
    int result = fieldName != null ? fieldName.hashCode() : 0;
    result = 31 * result + (operator != null ? operator.hashCode() : 0);
    return result;
  }
}
