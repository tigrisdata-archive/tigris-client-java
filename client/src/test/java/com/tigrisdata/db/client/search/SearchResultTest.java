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
import com.google.protobuf.ByteString;
import com.tigrisdata.db.api.v1.grpc.Api;
import com.tigrisdata.db.client.collection.DB1_C1;
import com.tigrisdata.db.client.config.TigrisConfiguration;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Assert;
import org.junit.Test;

public class SearchResultTest {

  private static final ObjectMapper DEFAULT_OBJECT_MAPPER =
      TigrisConfiguration.newBuilder("test", "db1").build().getObjectMapper();

  @Test
  public void convert() {
    String data = "{\"id\":0,\"name\":\"db1_c1_d0\"}";
    Api.SearchHit actualSearchHit =
        Api.SearchHit.newBuilder().setData(ByteString.copyFromUtf8(data)).build();
    Api.SearchFacet actualFacet =
        Api.SearchFacet.newBuilder()
            .addCounts(Api.FacetCount.newBuilder().setValue("someValue").setCount(2))
            .build();
    Api.SearchMetadata actualMeta = Api.SearchMetadata.newBuilder().setFound(1900).build();
    Api.SearchResponse resp =
        Api.SearchResponse.newBuilder()
            .addHits(actualSearchHit)
            .putFacets("someField", actualFacet)
            .setMeta(actualMeta)
            .build();

    SearchResult<DB1_C1> result = SearchResult.from(resp, DEFAULT_OBJECT_MAPPER, DB1_C1.class);

    Assert.assertEquals(resp.getHitsList().size(), result.getHits().size());
    Assert.assertTrue(result.getFacets().containsKey("someField"));
    Assert.assertEquals(actualMeta.getFound(), result.getMeta().getFound());
  }

  @Test
  public void convertFromDefault() {
    Api.SearchResponse resp = Api.SearchResponse.newBuilder().build();
    SearchResult<DB1_C1> result = SearchResult.from(resp, DEFAULT_OBJECT_MAPPER, DB1_C1.class);

    Assert.assertNotNull(result);
    Assert.assertNotNull(result.getHits());
    Assert.assertEquals(0, result.getHits().size());
    Assert.assertNotNull(result.getFacets());
    Assert.assertEquals(0, result.getFacets().size());
    Assert.assertNotNull(result.getMeta());
  }

  @Test
  public void convertFromNull() {
    Assert.assertThrows(
        NullPointerException.class,
        () -> SearchResult.from(null, DEFAULT_OBJECT_MAPPER, DB1_C1.class));
  }

  @Test
  public void equalsContract() {
    EqualsVerifier.forClass(SearchResult.class).verify();
  }
}
