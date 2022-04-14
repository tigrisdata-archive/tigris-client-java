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

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class TigrisDBJSONSchemaTest {

  @Test
  public void testGetName() throws IOException {
    TigrisDBJSONSchema schema1 =
        new TigrisDBJSONSchema(new URL("file:src/test/resources/test-schema.json"));
    Assert.assertEquals("user", schema1.getName());
    // read cached
    Assert.assertEquals("user", schema1.getName());
  }

  @Test
  public void testEquals() throws MalformedURLException {
    TigrisDBJSONSchema schema1 =
        new TigrisDBJSONSchema(new URL("file:src/test/resources/test-schema.json"));
    TigrisDBJSONSchema schema2 =
        new TigrisDBJSONSchema(new URL("file:src/test/resources/test-schema.json"));
    Assert.assertEquals(schema1, schema1);
    Assert.assertEquals(schema1, schema2);

    Assert.assertFalse(schema1.equals(null));
    Assert.assertFalse(schema1.equals("some-string"));

    TigrisDBJSONSchema schema3 =
        new TigrisDBJSONSchema(new URL("file:src/test/resources/test-schema.json"));
    TigrisDBJSONSchema schema4 = new TigrisDBJSONSchema(null);
    Assert.assertFalse(schema3.equals(schema4));
  }

  @Test
  public void testHashCode() throws MalformedURLException {
    TigrisDBJSONSchema schema1 =
        new TigrisDBJSONSchema(new URL("file:src/test/resources/test-schema.json"));
    TigrisDBJSONSchema schema2 =
        new TigrisDBJSONSchema(new URL("file:src/test/resources/test-schema.json"));
    Assert.assertEquals(schema1.hashCode(), schema1.hashCode());
    Assert.assertEquals(schema1.hashCode(), schema2.hashCode());

    TigrisDBJSONSchema schema3 = new TigrisDBJSONSchema(null);
    TigrisDBJSONSchema schema4 = new TigrisDBJSONSchema(null);
    Assert.assertEquals(schema3.hashCode(), schema4.hashCode());
  }
}
