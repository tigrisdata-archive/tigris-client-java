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

import com.tigrisdata.db.client.error.TigrisException;
import org.junit.Assert;
import org.junit.Test;

public class TigrisJSONSchemaTest {

  private static final String USERS_SCHEMA =
      "{\"title\":\"users\",\"description\":\"Collection of documents with "
          + "details of users\",\"properties\":{\"id\":{\"description\":\"A unique identifier for the user\",\"type\":\"integer\"},\"name\":{\"description\":\"Name of the user\",\"type\":\"string\"},\"balance\":{\"description\":\"User account balance\",\"type\":\"number\"}},\"primary_key\":[\"id\"]}";

  @Test
  public void testGetName() throws TigrisException {
    TigrisJSONSchema schema1 = new TigrisJSONSchema(USERS_SCHEMA);
    Assert.assertEquals("users", schema1.getName());
  }

  @Test
  public void testEquals() throws TigrisException {
    TigrisJSONSchema schema1 = new TigrisJSONSchema(USERS_SCHEMA);
    TigrisJSONSchema schema2 =
        new TigrisJSONSchema(
            "{\"title\":\"users\",\"description\":\"Collection of documents with details of users\",\"properties\":{\"id\":{\"description\":\"A unique identifier for the user\",\"type\":\"integer\"},\"name\":{\"description\":\"Name of the user\",\"type\":\"string\"},\"balance\":{\"description\":\"User account balance\",\"type\":\"number\"}},\"primary_key\":[\"id\"]}");
    Assert.assertEquals(schema1, schema1);
    Assert.assertEquals(schema1, schema2);

    Assert.assertFalse(schema1.equals(null));
    Assert.assertFalse(schema1.equals("some-string"));
  }

  @Test
  public void testHashCode() throws TigrisException {
    TigrisJSONSchema schema1 = new TigrisJSONSchema(USERS_SCHEMA);
    TigrisJSONSchema schema2 =
        new TigrisJSONSchema(
            "{\"title\":\"users\",\"description\":\"Collection of documents with details of users\",\"properties\":{\"id\":{\"description\":\"A unique identifier for the user\",\"type\":\"integer\"},\"name\":{\"description\":\"Name of the user\",\"type\":\"string\"},\"balance\":{\"description\":\"User account balance\",\"type\":\"number\"}},\"primary_key\":[\"id\"]}");
    Assert.assertEquals(schema1.hashCode(), schema1.hashCode());
    Assert.assertEquals(schema1.hashCode(), schema2.hashCode());
  }

  @Test
  public void testNullSchema() throws TigrisException {
    try {
      new TigrisJSONSchema(null);
      Assert.fail("This must fail");
    } catch (IllegalArgumentException illegalArgumentException) {
      Assert.assertEquals("argument \"content\" is null", illegalArgumentException.getMessage());
    }
  }
}
