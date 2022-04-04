package com.tigrisdata.db.client.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Assert;
import org.junit.Test;

public class ReadFieldsTest {
  @Test
  public void emptyReadFields() {
    ReadFields emptyReadFields = ReadFields.empty();
    Assert.assertTrue(emptyReadFields.isEmpty());
  }

  @Test
  public void includeReadFields() throws JsonProcessingException {
    ReadFields includeReadFields =
        ReadFields.newBuilder().includeField("id").includeField("name").includeField("age").build();
    Assert.assertEquals("{\"id\":true,\"name\":true,\"age\":true}", includeReadFields.toJSON());
  }

  @Test
  public void excludeReadFields() throws JsonProcessingException {
    ReadFields excludeReadFields =
        ReadFields.newBuilder().excludeField("id").excludeField("name").excludeField("age").build();
    Assert.assertEquals("{\"id\":false,\"name\":false,\"age\":false}", excludeReadFields.toJSON());
  }

  @Test
  public void includeAndExcludeReadFields() throws JsonProcessingException {
    ReadFields excludeReadFields =
        ReadFields.newBuilder().includeField("id").excludeField("name").includeField("age").build();
    Assert.assertEquals("{\"id\":true,\"name\":false,\"age\":true}", excludeReadFields.toJSON());
  }
}
