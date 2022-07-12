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

public class FacetStatsTest {

  @Test
  public void convert() {
    Api.FacetStats resp =
        Api.FacetStats.newBuilder()
            .setCount(12)
            .setAvg(8.2f)
            .setMax(40)
            .setMin(2)
            .setSum(120)
            .build();
    FacetStats actual = FacetStats.from(resp);
    Assert.assertEquals(resp.getAvg(), actual.getAvg(), 0);
    Assert.assertEquals(resp.getCount(), actual.getCount());
    Assert.assertEquals(resp.getSum(), actual.getSum(), 0);
    Assert.assertEquals(resp.getMax(), actual.getMax(), 0);
    Assert.assertEquals(resp.getMin(), actual.getMin(), 0);
  }

  @Test
  public void convertFromNull() {
    Assert.assertThrows(NullPointerException.class, () -> FacetStats.from(null));
  }

  @Test
  public void equalsContract() {
    EqualsVerifier.forClass(FacetStats.class).verify();
  }
}
