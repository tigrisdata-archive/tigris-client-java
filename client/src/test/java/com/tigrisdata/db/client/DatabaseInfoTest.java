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

public class DatabaseInfoTest {
  @Test
  public void testEquals() {
    DatabaseInfo databaseInfo1 = new DatabaseInfo("db1");
    DatabaseInfo databaseInfo2 = new DatabaseInfo("db1");
    Assert.assertEquals(databaseInfo1, databaseInfo1);
    Assert.assertEquals(databaseInfo1, databaseInfo2);
    Assert.assertFalse(databaseInfo1.equals(null));
    Assert.assertFalse(databaseInfo1.equals("some-string"));
    Assert.assertFalse(databaseInfo1.equals(new DatabaseInfo("db2")));
  }

  @Test
  public void testHashCode() {
    DatabaseInfo databaseInfo1 = new DatabaseInfo("db1");
    DatabaseInfo databaseInfo2 = new DatabaseInfo("db1");
    Assert.assertEquals(databaseInfo1.hashCode(), databaseInfo1.hashCode());
    Assert.assertEquals(databaseInfo1.hashCode(), databaseInfo2.hashCode());

    DatabaseInfo databaseInfo3 = new DatabaseInfo(null);
    DatabaseInfo databaseInfo4 = new DatabaseInfo(null);
    Assert.assertEquals(databaseInfo3.hashCode(), databaseInfo4.hashCode());
  }

  @Test
  public void testGetDatabaseName() {
    DatabaseInfo databaseInfo1 = new DatabaseInfo("db1");
    Assert.assertEquals("db1", databaseInfo1.getDatabaseName());
  }
}
