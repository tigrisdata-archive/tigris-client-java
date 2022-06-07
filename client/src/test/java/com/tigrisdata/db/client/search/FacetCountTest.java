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

public class FacetCountTest {

  @Test
  public void convert() {
    Api.FacetCount resp = Api.FacetCount.newBuilder().setValue("Shoe").setCount(32).build();
    FacetCount actual = FacetCount.from(resp);
    Assert.assertEquals(resp.getValue(), actual.getValue());
    Assert.assertEquals(resp.getCount(), actual.getCount());
  }

  @Test
  public void convertFromNull() {
    Assert.assertThrows(NullPointerException.class, () -> FacetCount.from(null));
  }

  @Test
  public void equalsContract() {
    EqualsVerifier.forClass(FacetCount.class).verify();
  }
}
