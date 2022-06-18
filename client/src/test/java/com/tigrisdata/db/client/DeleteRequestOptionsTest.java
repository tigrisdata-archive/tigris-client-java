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

public class DeleteRequestOptionsTest {

  @Test
  public void equalsTest() {
    DeleteRequestOptions op1 = new DeleteRequestOptions();
    DeleteRequestOptions op2 = new DeleteRequestOptions();

    Assert.assertEquals(op1, op1);
    Assert.assertEquals(op1, op2);
    //noinspection ConstantConditions
    Assert.assertFalse(op1.equals(null));

    DeleteRequestOptions op3 = new DeleteRequestOptions(WriteOptions.DEFAULT_INSTANCE);
    DeleteRequestOptions op4 = new DeleteRequestOptions(WriteOptions.DEFAULT_INSTANCE);
    Assert.assertEquals(op3, op4);

    DeleteRequestOptions op5 = new DeleteRequestOptions(WriteOptions.DEFAULT_INSTANCE);
    DeleteRequestOptions op6 = new DeleteRequestOptions(WriteOptions.DEFAULT_INSTANCE);
    Assert.assertEquals(op5, op6);

    //noinspection EqualsBetweenInconvertibleTypes
    Assert.assertFalse(op5.equals("some-string"));
  }

  @Test
  public void testHashCode() {
    DeleteRequestOptions op1 = new DeleteRequestOptions();
    DeleteRequestOptions op2 = new DeleteRequestOptions();
    Assert.assertEquals(op1.hashCode(), op1.hashCode());
    Assert.assertEquals(op1.hashCode(), op2.hashCode());

    DeleteRequestOptions op3 = new DeleteRequestOptions(null);
    DeleteRequestOptions op4 = new DeleteRequestOptions(null);
    Assert.assertEquals(op3.hashCode(), op4.hashCode());
  }

  @Test
  public void testAccessors() {
    DeleteRequestOptions op = new DeleteRequestOptions();
    WriteOptions writeOptions = WriteOptions.DEFAULT_INSTANCE;
    op.setWriteOptions(writeOptions);
    Assert.assertEquals(writeOptions, op.getWriteOptions());
  }
}
