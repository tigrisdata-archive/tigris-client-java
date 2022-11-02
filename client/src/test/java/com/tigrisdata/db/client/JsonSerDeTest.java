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
import com.tigrisdata.db.client.collection.JsonSerDeTestModel;
import com.tigrisdata.db.client.config.TigrisConfiguration;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class JsonSerDeTest {
  // Wed Nov 02 2022 18:34:31 UTC
  // OR
  // 2022-11-02T18:34:31.053+00:00
  private static final long TEST_DATE = 1667414071053L;

  @Test
  public void dateTimeSerialization() throws JsonProcessingException {
    JsonSerDeTestModel model = new JsonSerDeTestModel();
    model.setCreatedAt(new Date(TEST_DATE));
    TigrisConfiguration configuration = TigrisConfiguration.newBuilder("test-url").build();
    String serializedModel = configuration.getObjectMapper().writeValueAsString(model);
    Assert.assertEquals("{\"createdAt\":\"2022-11-02T18:34:31.053+00:00\"}", serializedModel);
  }

  @Test
  public void dateTimeDeSerialization() throws JsonProcessingException {
    TigrisConfiguration configuration = TigrisConfiguration.newBuilder("test-url").build();
    JsonSerDeTestModel reconstructedModel =
        configuration
            .getObjectMapper()
            .readValue(
                "{\"createdAt\":\"2022" + "-11-02T18:34:31" + ".053+00:00\"}",
                JsonSerDeTestModel.class);

    Assert.assertEquals(TEST_DATE, reconstructedModel.getCreatedAt().getTime());
  }
}
