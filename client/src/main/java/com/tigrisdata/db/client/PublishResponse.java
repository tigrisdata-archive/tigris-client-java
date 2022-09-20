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
import com.tigrisdata.db.type.TigrisMessageCollectionType;

import java.util.List;
import java.util.Objects;

/** Represents Server response for Publish operation */
public class PublishResponse<T extends TigrisMessageCollectionType> extends DMLResponse {
  private final List<T> messages;

  public PublishResponse(
      String status, Timestamp createdAt, Timestamp updatedAt, List<T> messages) {
    super(status, createdAt, updatedAt);
    this.messages = messages;
  }

  /** @return copy of the messages published */
  public List<T> getMessages() {
    return messages;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    PublishResponse<?> that = (PublishResponse<?>) o;
    return Objects.equals(messages, that.messages);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), messages);
  }
}
