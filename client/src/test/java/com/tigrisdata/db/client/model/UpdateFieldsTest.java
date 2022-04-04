package com.tigrisdata.db.client.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

public class UpdateFieldsTest {

  @Test(expected = IllegalStateException.class)
  public void testEmptySet() {
    UpdateFields.newBuilder().set(UpdateFields.SetFields.newBuilder().build()).build();
  }

  @Test
  public void setFields() throws JsonProcessingException {
    UpdateFields withSetFields =
        UpdateFields.newBuilder()
            .set(
                UpdateFields.SetFields.newBuilder()
                    .set("name", "new_name")
                    .set("active", true)
                    .set("total_int_score", 100)
                    .set("total_long_score", 200L)
                    .set("byte_arr", "test input".getBytes(StandardCharsets.UTF_8))
                    .build())
            .build();
    Assert.assertEquals(
        "{\"$set\":{\"name\":\"new_name\",\"active\":true,\"total_int_score\":100,\"total_long_score\":200,"
            + "\"byte_arr\":\"dGVzdCBpbnB1dA==\"}}",
        withSetFields.toJSON());
  }
}
