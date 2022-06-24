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
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Assert;
import org.junit.Test;

public class FacetCountDistributionTest {

  @Test
  public void convert() {
    Api.FacetStats stats = Api.FacetStats.newBuilder().setCount(12).build();
    Api.SearchFacet resp =
        Api.SearchFacet.newBuilder()
            .setStats(stats)
            .addCounts(Api.FacetCount.newBuilder().setValue("name").setCount(2))
            .build();
    FacetCountDistribution actual = FacetCountDistribution.from(resp);

    Assert.assertNotNull(actual.getCounts());
    Assert.assertNotNull(actual.getStats());

    Assert.assertEquals(resp.getCounts(0).getValue(), actual.getCounts().get(0).getValue());
    Assert.assertEquals(resp.getCounts(0).getCount(), actual.getCounts().get(0).getCount());

    Assert.assertEquals(stats.getCount(), actual.getStats().getCount());
  }

  @Test
  public void convertWithNullValues() {
    Api.SearchFacet resp = Api.SearchFacet.newBuilder().build();
    FacetCountDistribution actual = FacetCountDistribution.from(resp);

    Assert.assertNotNull(actual.getCounts());
    Assert.assertNotNull(actual.getStats());
  }

  @Test
  public void convertFromNull() {
    Assert.assertThrows(NullPointerException.class, () -> FacetCountDistribution.from(null));
  }

  @Test
  public void equalsContract() {
    EqualsVerifier.forClass(FacetCountDistribution.class).verify();
  }
}
