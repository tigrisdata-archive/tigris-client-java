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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tigrisdata.db.api.v1.grpc.Api;
import com.tigrisdata.db.type.TigrisCollectionType;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Outcome of executing /search query against server. Representation of {@link Api.SearchResponse}
 * from server
 *
 * @param <T> type of the Tigris collection
 */
public final class SearchResult<T extends TigrisCollectionType> {

  private final List<Hit<T>> hits;
  private final Map<String, FacetCountDistribution> facets;
  private final SearchMeta meta;

  private SearchResult(
      List<Hit<T>> hits, Map<String, FacetCountDistribution> facets, SearchMeta meta) {
    this.hits = Collections.unmodifiableList(hits);
    this.facets = Collections.unmodifiableMap(facets);
    this.meta = meta;
  }

  /**
   * Results of the query as a list
   *
   * @return Immutable list of search results
   */
  public List<Hit<T>> getHits() {
    return hits;
  }

  /**
   * Distribution of the facets provided as part of the facet query
   *
   * @return Immutable map of facet field name and relevant faceting options
   */
  public Map<String, FacetCountDistribution> getFacets() {
    return facets;
  }

  /**
   * Information about {@link SearchResult}
   *
   * @return {@link SearchMeta}
   */
  public SearchMeta getMeta() {
    return meta;
  }

  /**
   * Conversion utility for creating {@link SearchResult} from server response
   *
   * @param resp SearchResponse from server
   * @param objectMapper JSON deserializer
   * @param collectionClass Deserialize document to this schema class
   * @param <R> Tigris collection class type
   * @return {@link SearchResult}
   */
  public static <R extends TigrisCollectionType> SearchResult<R> from(
      Api.SearchResponse resp, ObjectMapper objectMapper, Class<R> collectionClass) {
    Objects.requireNonNull(resp);

    List<Hit<R>> hits =
        resp.getHitsList().stream()
            .map(h -> Hit.from(h, objectMapper, collectionClass))
            .collect(Collectors.toList());

    // proto inserts a default entry
    Map<String, FacetCountDistribution> facets =
        resp.getFacetsMap().entrySet().stream()
            .collect(
                Collectors.toMap(Entry::getKey, e -> FacetCountDistribution.from(e.getValue())));

    return new SearchResult<>(hits, facets, SearchMeta.from(resp.getMeta()));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    SearchResult<?> that = (SearchResult<?>) o;

    if (!Objects.equals(hits, that.hits)) {
      return false;
    }
    if (!Objects.equals(facets, that.facets)) {
      return false;
    }
    return Objects.equals(meta, that.meta);
  }

  @Override
  public int hashCode() {
    int result = hits != null ? hits.hashCode() : 0;
    result = 31 * result + (facets != null ? facets.hashCode() : 0);
    result = 31 * result + (meta != null ? meta.hashCode() : 0);
    return result;
  }
}
