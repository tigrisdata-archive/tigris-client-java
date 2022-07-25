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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.protobuf.Timestamp;
import com.tigrisdata.db.type.TigrisCollectionType;

/** Represents Server response for InsertOrReplace operation */
public class PublishResponse<T extends TigrisCollectionType> extends DMLResponse {
  private final Map<String, Object>[] generatedKeys;
  private final List<T> messages;

  public PublishResponse(
      String status,
      Timestamp createdAt,
      Timestamp updatedAt,
      Map<String, Object>[] generatedKeys,
      List<T> messages) {
    super(status, createdAt, updatedAt);
    this.generatedKeys = generatedKeys;
    this.messages = messages;
    Utilities.fillInIds(messages, generatedKeys);
  }

  /**
   * @return copy of the documents with their primary-keys set. This is useful to know when
   *     primary-key is set to autoGenerate
   */
  public List<T> getMessages() {
    return messages;
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
    PublishResponse<?> that = (PublishResponse<?>) o;
    return Arrays.equals(generatedKeys, that.generatedKeys)
        && Objects.equals(messages, that.messages);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(super.hashCode(), messages);
    result = 31 * result + Arrays.hashCode(generatedKeys);
    return result;
  }
}
