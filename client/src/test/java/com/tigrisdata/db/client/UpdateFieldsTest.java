package com.tigrisdata.db.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.io.JsonEOFException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tigrisdata.db.client.config.TigrisConfiguration;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class UpdateFieldsTest {
  private static ObjectMapper DEFAULT_OBJECT_MAPPER =
      TigrisConfiguration.newBuilder("test", "db1").build().getObjectMapper();

  @Test(expected = IllegalStateException.class)
  public void testEmptySet() {
    UpdateFields.newBuilder().build();
  }

  @Test
  public void setFields() {
    UUID uuid = UUID.fromString("aa8f8da5-5fd6-4660-a348-9ed7fe96253a");
    UpdateFields withSetFields =
        UpdateFields.newBuilder()
            .set("name", "new_name")
            .set("active", true)
            .set("total_int_score", 100)
            .set("total_long_score", 200L)
            .set("byte_arr", "test input".getBytes(StandardCharsets.UTF_8))
            .set("double_field", 123.456D)
            .set("uuidField", uuid)
            .build();
    Assert.assertEquals(
        "{\"$set\":{\"name\":\"new_name\",\"active\":true,\"total_int_score\":100,\"total_long_score\":200,"
            + "\"byte_arr\":\"dGVzdCBpbnB1dA==\",\"double_field\":123.456,"
            + "\"uuidField\":\"aa8f8da5-5fd6-4660-a348-9ed7fe96253a\"}}",
        withSetFields.toJSON(DEFAULT_OBJECT_MAPPER));
  }

  @Test
  public void testJSONSerializationFailure() {
    UpdateFields withSetFields =
        UpdateFields.newBuilder().set("name", "new_name").set("active", true).build();

    try {
      withSetFields.toJSON(
          // simulate failure
          new ObjectMapper() {
            @Override
            public String writeValueAsString(Object value) throws JsonProcessingException {
              throw new JsonEOFException(null, null, null);
            }
          });
      Assert.fail("This must fail");
    } catch (IllegalStateException ex) {
      Assert.assertEquals(
          "This is raised because the JSON serialization of UpdateFields failed", ex.getMessage());
    }
  }
}
