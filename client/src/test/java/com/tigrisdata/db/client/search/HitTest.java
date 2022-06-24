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

public class HitTest {

  private static final ObjectMapper DEFAULT_OBJECT_MAPPER =
      TigrisConfiguration.newBuilder("test").build().getObjectMapper();

  // TODO: Add deserialization for meta as well
  @Test
  public void fromNull() {
    Assert.assertThrows(
        NullPointerException.class, () -> Hit.from(null, DEFAULT_OBJECT_MAPPER, DB1_C1.class));
  }

  @Test
  public void deserialization() {
    String data = "{\"id\":0,\"name\":\"db1_c1_d0\"}";
    Api.SearchHit input = Api.SearchHit.newBuilder().setData(ByteString.copyFromUtf8(data)).build();
    Hit<DB1_C1> hit = Hit.from(input, DEFAULT_OBJECT_MAPPER, DB1_C1.class);
    Assert.assertNotNull(hit);
    Assert.assertEquals(DB1_C1.class, hit.getDocument().getClass());
    Assert.assertEquals(0, hit.getDocument().getId());
    Assert.assertEquals("db1_c1_d0", hit.getDocument().getName());
  }

  @Test
  public void deserializationFailure() {
    Api.SearchHit input =
        Api.SearchHit.newBuilder().setData(ByteString.copyFromUtf8("data")).build();
    Exception thrown =
        Assert.assertThrows(
            IllegalArgumentException.class,
            () -> Hit.from(input, DEFAULT_OBJECT_MAPPER, DB1_C1.class));
    Assert.assertEquals("Failed to convert response to DB1_C1.class", thrown.getMessage());
  }

  @Test
  public void equalsContract() {
    EqualsVerifier.forClass(Hit.class).verify();
  }
}
