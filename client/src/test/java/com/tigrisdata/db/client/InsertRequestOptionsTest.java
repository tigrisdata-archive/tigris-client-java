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

public class InsertRequestOptionsTest {
  @Test
  public void equalsTest() {
    InsertRequestOptions op1 = new InsertRequestOptions();
    InsertRequestOptions op2 = new InsertRequestOptions();

    Assert.assertEquals(op1, op1);
    Assert.assertEquals(op1, op2);
    Assert.assertNotEquals(op1, null);

    InsertRequestOptions op3 = new InsertRequestOptions(WriteOptions.DEFAULT_INSTANCE);
    InsertRequestOptions op4 = new InsertRequestOptions(WriteOptions.DEFAULT_INSTANCE);
    Assert.assertEquals(op3, op4);

    Assert.assertNotEquals(op1, "some-string");
  }

  @Test
  public void testHashCode() {
    InsertRequestOptions op1 = new InsertRequestOptions();
    InsertRequestOptions op2 = new InsertRequestOptions();
    Assert.assertEquals(op1.hashCode(), op1.hashCode());
    Assert.assertEquals(op1.hashCode(), op2.hashCode());

    InsertRequestOptions op3 = new InsertRequestOptions(null);
    InsertRequestOptions op4 = new InsertRequestOptions(null);
    Assert.assertEquals(op3.hashCode(), op4.hashCode());
  }

  @Test
  public void testAccessors() {
    InsertRequestOptions op = new InsertRequestOptions();
    WriteOptions writeOptions = WriteOptions.DEFAULT_INSTANCE;
    op.setWriteOptions(writeOptions);
    Assert.assertEquals(writeOptions, op.getWriteOptions());
  }
}
