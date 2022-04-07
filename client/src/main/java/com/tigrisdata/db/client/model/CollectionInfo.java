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
package com.tigrisdata.db.client.model;

import java.util.Objects;

public class CollectionInfo {
  private final String collectionName;

  public CollectionInfo(String collectionName) {
    this.collectionName = collectionName;
  }

  public String getCollectionName() {
    return collectionName;
  }

  @Override
  public String toString() {
    return "CollectionInfo{" + "collectionName='" + collectionName + '\'' + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    CollectionInfo that = (CollectionInfo) o;

    return Objects.equals(collectionName, that.collectionName);
  }

  @Override
  public int hashCode() {
    return collectionName != null ? collectionName.hashCode() : 0;
  }
}
