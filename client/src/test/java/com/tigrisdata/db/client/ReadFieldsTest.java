package com.tigrisdata.db.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.io.JsonEOFException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tigrisdata.db.client.config.TigrisDBConfiguration;
import org.junit.Assert;
import org.junit.Test;

public class ReadFieldsTest {
  private static ObjectMapper DEFAULT_OBJECT_MAPPER =
      TigrisDBConfiguration.newBuilder("test").build().getObjectMapper();

  @Test
  public void emptyReadFields() {
    ReadFields emptyReadFields = ReadFields.empty();
    Assert.assertTrue(emptyReadFields.isEmpty());
  }

  @Test
  public void includeReadFields() {
    ReadFields includeReadFields =
        ReadFields.newBuilder().includeField("id").includeField("name").includeField("age").build();
    Assert.assertEquals(
        "{\"id\":true,\"name\":true,\"age\":true}",
        includeReadFields.toJSON(DEFAULT_OBJECT_MAPPER));
  }

  @Test
  public void excludeReadFields() {
    ReadFields excludeReadFields =
        ReadFields.newBuilder().excludeField("id").excludeField("name").excludeField("age").build();
    Assert.assertEquals(
        "{\"id\":false,\"name\":false,\"age\":false}",
        excludeReadFields.toJSON(DEFAULT_OBJECT_MAPPER));
  }

  @Test
  public void includeAndExcludeReadFields() {
    ReadFields excludeReadFields =
        ReadFields.newBuilder().includeField("id").excludeField("name").includeField("age").build();
    Assert.assertEquals(
        "{\"id\":true,\"name\":false,\"age\":true}",
        excludeReadFields.toJSON(DEFAULT_OBJECT_MAPPER));
  }

  @Test
  public void testJSONSerializationFailure() {
    ReadFields readField =
        ReadFields.newBuilder()
            .includeField("inc1")
            .includeField("inc2")
            .excludeField("exc1")
            .excludeField("exc2")
            .build();
    try {
      readField.toJSON(
          // simulate failure
          // this situation is not expected to happen in normal use case
          new ObjectMapper() {
            @Override
            public String writeValueAsString(Object value) throws JsonProcessingException {
              throw new JsonEOFException(null, null, null);
            }
          });
      Assert.fail("This must fail");
    } catch (IllegalStateException ex) {
      Assert.assertEquals(
          "This is caused because JSON serialization of ReadFields was not successful",
          ex.getMessage());
    }
  }
}
