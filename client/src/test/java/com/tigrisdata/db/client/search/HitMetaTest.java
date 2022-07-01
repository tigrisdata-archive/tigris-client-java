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
import com.tigrisdata.db.api.v1.grpc.Api.SearchHitMeta;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Assert;
import org.junit.Test;

public class HitMetaTest {

  @Test
  public void fromTs() {
    // June 30, 2022 12:00:00 AM GMT
    Timestamp june30 = Timestamp.newBuilder().setSeconds(1656547200).build();
    SearchHitMeta apiResponse = SearchHitMeta.newBuilder().setCreatedAt(june30).build();
    HitMeta generated = HitMeta.from(apiResponse);

    Assert.assertEquals(june30.getNanos(), generated.getCreatedAt().getNano());
    Assert.assertNull(generated.getUpdatedAt());
  }

  @Test
  public void fromDefault() {
    SearchHitMeta apiResponse = SearchHitMeta.newBuilder().build();
    HitMeta generated = HitMeta.from(apiResponse);
    Assert.assertNull(generated.getCreatedAt());
    Assert.assertNull(generated.getUpdatedAt());
  }

  @Test
  public void fromNull() {
    HitMeta meta = HitMeta.from(null);
    Assert.assertNull(meta.getCreatedAt());
    Assert.assertNull(meta.getUpdatedAt());
  }

  @Test
  public void equalsContract() {
    EqualsVerifier.forClass(HitMeta.class).verify();
  }
}
