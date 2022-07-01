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
import java.time.Instant;
import java.util.Objects;

class DMLResponse extends TigrisResponse {

  protected final Metadata metadata;

  DMLResponse(String status, Timestamp createdAt, Timestamp updatedAt) {
    super(status);
    this.metadata = Metadata.from(createdAt, updatedAt);
  }

  /** @return metadata about this operation */
  public Metadata getMetadata() {
    return metadata;
  }

  public String getStatus() {
    return status;
  }

  static class Metadata {
    private final Instant createdAt;
    private final Instant updatedAt;

    public Metadata(Instant createdAt, Instant updatedAt) {
      this.createdAt = createdAt;
      this.updatedAt = updatedAt;
    }

    private static Metadata from(Timestamp createdAt, Timestamp updatedAt) {
      Instant _created = Utilities.protoTimestampToInstant(createdAt);
      Instant _updated = Utilities.protoTimestampToInstant(updatedAt);
      return new Metadata(_created, _updated);
    }

    /**
     * @return Instant in time at which the document was inserted/replaced. Measured in nanoseconds
     *     since the Unix epoch.
     */
    public Instant getCreatedAt() {
      return createdAt;
    }
    /**
     * @return Time at which the document was updated. Measured in nanoseconds since the Unix epoch.
     */
    public Instant getUpdatedAt() {
      return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Metadata metadata = (Metadata) o;
      return Objects.equals(createdAt, metadata.createdAt)
          && Objects.equals(updatedAt, metadata.updatedAt);
    }

    @Override
    public int hashCode() {
      return Objects.hash(createdAt, updatedAt);
    }
  }
}
