package com.tigrisdata.db.client.service;

import com.tigrisdata.db.client.model.TigrisDBJSONSchema;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class TigrisDBJSONSchemaTest {

  private static final String EXPECTED_SCHEMA =
      "{\n"
          + "  \"title\": \"User\",\n"
          + "  \"description\": \"This document records the details of user for tigris store\",\n"
          + "  \"properties\": {\n"
          + "    \"id\": {\n"
          + "      \"description\": \"A unique identifier for the user\",\n"
          + "      \"type\": \"int\"\n"
          + "    },\n"
          + "    \"name\": {\n"
          + "      \"description\": \"Name of the user\",\n"
          + "      \"type\": \"string\",\n"
          + "      \"max_length\": 100\n"
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
        new TigrisDBJSONSchema("src/test/resources/test-schema.json");
    Assert.assertEquals(EXPECTED_SCHEMA, tigrisDBJSONSchema.getSchemaContent());
  }

  @Test
  public void loadFromInputstreamTest() throws IOException {
    TigrisDBJSONSchema tigrisDBJSONSchema =
        new TigrisDBJSONSchema(TigrisDBJSONSchema.class.getResourceAsStream("/test-schema.json"));
    Assert.assertEquals(EXPECTED_SCHEMA, tigrisDBJSONSchema.getSchemaContent());
  }
}
