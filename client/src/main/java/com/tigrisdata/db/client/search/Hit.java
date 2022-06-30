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

import static java.lang.String.format;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tigrisdata.db.api.v1.grpc.Api;
import com.tigrisdata.db.type.TigrisCollectionType;
import java.util.Objects;

/**
 * Representation of {@link Api.SearchHit} that provides collection document and associated metadata
 * from /search result
 *
 * @param <T> {@link TigrisCollectionType} type
 */
public final class Hit<T extends TigrisCollectionType> {

  private final T document;
  private final HitMeta meta;

  private Hit(T document, HitMeta meta) {
    this.document = document;
    this.meta = meta;
  }

  /**
   * Json deserialized document as its collection class
   *
   * @return {@link TigrisCollectionType}
   */
  public T getDocument() {
    return document;
  }

  /**
   * Relevance metadata for matched document
   *
   * @return {@link HitMeta}
   */
  public HitMeta getMeta() {
    return meta;
  }

  /**
   * Conversion utility for creating {@link Hit} from server response
   *
   * @param resp {@link Api.SearchHit} from server response
   * @param objectMapper JSON deserializer
   * @param collectionClass Deserialize document to this schema class
   * @param <R> Tigris collection class type
   * @return {@link Hit}
   */
  static <R extends TigrisCollectionType> Hit<R> from(
      Api.SearchHit resp, ObjectMapper objectMapper, Class<R> collectionClass) {
    Objects.requireNonNull(resp);
    try {
      return new Hit<>(
          objectMapper.readValue(resp.getData().toStringUtf8(), collectionClass),
          HitMeta.from(resp.getMetadata()));
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException(
          format("Failed to convert response to %s.class", collectionClass.getSimpleName()), e);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Hit<?> hit = (Hit<?>) o;

    if (!Objects.equals(document, hit.document)) {
      return false;
    }
    return Objects.equals(meta, hit.meta);
  }

  @Override
  public int hashCode() {
    int result = document != null ? document.hashCode() : 0;
    result = 31 * result + (meta != null ? meta.hashCode() : 0);
    return result;
  }
}
