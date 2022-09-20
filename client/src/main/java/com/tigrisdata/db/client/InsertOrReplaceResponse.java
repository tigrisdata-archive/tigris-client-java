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

import com.google.protobuf.Timestamp;
import com.tigrisdata.db.type.TigrisDocumentCollectionType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Represents Server response for InsertOrReplace operation */
public class InsertOrReplaceResponse<T extends TigrisDocumentCollectionType> extends DMLResponse {
  private final Map<String, Object>[] generatedKeys;
  private final List<T> docs;

  public InsertOrReplaceResponse(
      String status,
      Timestamp createdAt,
      Timestamp updatedAt,
      Map<String, Object>[] generatedKeys,
      List<T> docs) {
    super(status, createdAt, updatedAt);
    this.generatedKeys = generatedKeys;
    this.docs = docs;
    Utilities.fillInIds(docs, generatedKeys);
  }

  /**
   * @return copy of the documents with their primary-keys set. This is useful to know when
   *     primary-key is set to autoGenerate
   */
  public List<T> getDocs() {
    return docs;
  }

  /**
   * @return an array of (Map of (String to Object)). Value in map is one of these types (int, long,
   *     UUID, String). The key of the map is the primaryKey field name in your collection and value
   *     is the generated value for that. Array preserves the order in which the entries were
   *     submitted to server.
   */
  public Map<String, Object>[] getGeneratedKeys() {
    return generatedKeys;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    InsertOrReplaceResponse<?> that = (InsertOrReplaceResponse<?>) o;
    return Arrays.equals(generatedKeys, that.generatedKeys) && Objects.equals(docs, that.docs);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(super.hashCode(), docs);
    result = 31 * result + Arrays.hashCode(generatedKeys);
    return result;
  }
}
