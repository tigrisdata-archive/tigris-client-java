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

import com.tigrisdata.db.api.v1.grpc.Api;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/** Representation of {@link Api.SearchFacet} */
public final class FacetCountDistribution {

  private final List<FacetCount> counts;
  private final FacetStats stats;

  private FacetCountDistribution(List<FacetCount> counts, FacetStats stats) {
    this.counts = Collections.unmodifiableList(counts);
    this.stats = stats;
  }

  /**
   * List of field values and their aggregated counts
   *
   * @return immutable {@link List} of {@link FacetCount}
   */
  public List<FacetCount> getCounts() {
    return counts;
  }

  /**
   * Statistics for this particular field
   *
   * @return summary of facets as {@link FacetStats}
   */
  public FacetStats getStats() {
    return stats;
  }

  /**
   * Conversion utility to create {@link FacetCountDistribution} from server response
   *
   * @param resp {@link Api.SearchFacet}
   */
  static FacetCountDistribution from(Api.SearchFacet resp) {
    Objects.requireNonNull(resp);

    List<FacetCount> counts =
        Optional.of(resp.getCountsList())
            .map(c -> c.stream().map(FacetCount::from).collect(Collectors.toList()))
            .orElse(Collections.emptyList());

    FacetStats stats = Optional.of(resp.getStats()).map(FacetStats::from).orElse(null);

    return new FacetCountDistribution(counts, stats);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    FacetCountDistribution that = (FacetCountDistribution) o;

    if (!Objects.equals(counts, that.counts)) {
      return false;
    }
    return Objects.equals(stats, that.stats);
  }

  @Override
  public int hashCode() {
    int result = counts != null ? counts.hashCode() : 0;
    result = 31 * result + (stats != null ? stats.hashCode() : 0);
    return result;
  }
}
