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
package com.tigrisdata.db.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tigrisdata.db.client.config.TigrisConfiguration;
import com.tigrisdata.db.client.customization.Order;
import com.tigrisdata.db.client.customization.OrderCustomized;
import com.tigrisdata.db.client.customization.OrderWithoutId;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

public class FieldCustomization {

  @Test
  public void with_idField() throws JsonProcessingException {
    ObjectMapper objectMapper = TigrisConfiguration.newBuilder("").build().getObjectMapper();
    UUID generatedId = UUID.randomUUID();
    Order order =
        objectMapper.readValue(
            "{\n"
                + "  \"buyerId\": 1,\n"
                + "  \"sellerId\": 2,\n"
                + "  \"tradePrice\": 12.34,\n"
                + "  \"_id\": \""
                + generatedId
                + "\"\n"
                + "}",
            Order.class);
    Assert.assertEquals(generatedId, order.get_id());
  }

  @Test
  public void withCustomization() throws JsonProcessingException {
    ObjectMapper objectMapper = TigrisConfiguration.newBuilder("").build().getObjectMapper();
    UUID generatedId = UUID.randomUUID();
    final String jsonContent =
        "{\"buyerId\":1,\"sellerId\":2,\"tradePrice\":12.34,\"_id\":\"" + generatedId + "\"}";
    OrderCustomized order = objectMapper.readValue(jsonContent, OrderCustomized.class);
    Assert.assertEquals(generatedId, order.getOrderId());

    // serialize again
    Assert.assertEquals(jsonContent, objectMapper.writeValueAsString(order));
  }

  @Test
  public void withIdFieldMissing() throws JsonProcessingException {
    ObjectMapper objectMapper = TigrisConfiguration.newBuilder("").build().getObjectMapper();
    UUID generatedId = UUID.randomUUID();
    final String jsonContent =
        "{\"buyerId\":1,\"sellerId\":2,\"tradePrice\":12.34,\"_id\":\"" + generatedId + "\"}";
    OrderWithoutId order = objectMapper.readValue(jsonContent, OrderWithoutId.class);
    Assert.assertNotNull(order);
  }
}
