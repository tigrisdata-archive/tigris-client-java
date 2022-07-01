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

import com.google.protobuf.Timestamp;
import com.tigrisdata.db.api.v1.grpc.Api;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * Representation of {@link Api.SearchHitMeta} exposing relevance information for the matched
 * document.
 */
public final class HitMeta {
  private final Instant createdAt;
  private final Instant updatedAt;

  private HitMeta(Instant createdAt, Instant updatedAt) {
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  /**
   * Gets the Instant in time at which document was inserted/replaced to a precision of milliseconds
   *
   * @return time as {@link Instant} or null
   */
  public Instant getCreatedAt() {
    return createdAt;
  }

  /**
   * Gets the Instant in time at which document was updated to a precision of milliseconds
   *
   * @return time as {@link Instant} or null
   */
  public Instant getUpdatedAt() {
    return updatedAt;
  }

  /**
   * Conversion utility for creating {@link HitMeta} from server response
   *
   * @param resp {@link Api.SearchHitMeta} from server response
   * @return {@link HitMeta}
   */
  static HitMeta from(Api.SearchHitMeta resp) {
    if (resp == null) {
      return new HitMeta(null, null);
    }
    Instant createdAt = protoTsToInstant(resp.getCreatedAt()).orElse(null);
    Instant updatedAt = protoTsToInstant(resp.getUpdatedAt()).orElse(null);
    return new HitMeta(createdAt, updatedAt);
  }

  private static Optional<Instant> protoTsToInstant(Timestamp ts) {
    if (ts == null || Timestamp.getDefaultInstance().equals(ts)) {
      return Optional.empty();
    }
    return Optional.of(Instant.ofEpochSecond(ts.getSeconds(), ts.getNanos()));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    HitMeta hitMeta = (HitMeta) o;

    if (!Objects.equals(createdAt, hitMeta.createdAt)) {
      return false;
    }
    return Objects.equals(updatedAt, hitMeta.updatedAt);
  }

  @Override
  public int hashCode() {
    int result = createdAt != null ? createdAt.hashCode() : 0;
    result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0);
    return result;
  }
}
