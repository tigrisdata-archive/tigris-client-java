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
package com.tigrisdata.db.client.service;

import com.tigrisdata.db.client.model.TigrisDBJSONSchema;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

public class TigrisDBJSONSchemaTest {

  private static final String EXPECTED_SCHEMA =
      "{\n"
          + "  \"name\": \"user\",\n"
          + "  \"description\": \"This document records the details of user for tigris store\",\n"
          + "  \"properties\": {\n"
          + "    \"id\": {\n"
          + "      \"description\": \"A unique identifier for the user\",\n"
          + "      \"type\": \"int\"\n"
          + "    },\n"
          + "    \"name\": {\n"
          + "      \"description\": \"Name of the user\",\n"
          + "      \"type\": \"string\"\n"
          + "    },\n"
          + "    \"balance\": {\n"
          + "      \"description\": \"user balance in USD\",\n"
          + "      \"type\": \"double\"\n"
          + "    }\n"
          + "  },\n"
          + "  \"primary_key\": [\n"
          + "    \"id\"\n"
          + "  ]\n"
          + "}";

  @Test
  public void loadFromFileTest() throws IOException {
    TigrisDBJSONSchema tigrisDBJSONSchema =
        new TigrisDBJSONSchema(new URL("file:src/test/resources/test-schema.json"));
    Assert.assertEquals(EXPECTED_SCHEMA, tigrisDBJSONSchema.getSchemaContent());
    Assert.assertEquals("user", tigrisDBJSONSchema.getName());
  }

  @Test
  public void loadFromClasspathTest() throws IOException {
    TigrisDBJSONSchema tigrisDBJSONSchema =
        new TigrisDBJSONSchema(TigrisDBJSONSchemaTest.class.getResource("/test-schema.json"));
    Assert.assertEquals(EXPECTED_SCHEMA, tigrisDBJSONSchema.getSchemaContent());
    Assert.assertEquals("user", tigrisDBJSONSchema.getName());
  }
}
