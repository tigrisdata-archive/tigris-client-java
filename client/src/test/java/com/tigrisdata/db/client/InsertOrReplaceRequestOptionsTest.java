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

public class InsertOrReplaceRequestOptionsTest {
  @Test
  public void equalsTest() {
    InsertOrReplaceRequestOptions op1 = new InsertOrReplaceRequestOptions();
    InsertOrReplaceRequestOptions op2 = new InsertOrReplaceRequestOptions();

    Assert.assertEquals(op1, op1);
    Assert.assertEquals(op1, op2);
    Assert.assertFalse(op1.equals(null));

    InsertOrReplaceRequestOptions op3 =
        new InsertOrReplaceRequestOptions(WriteOptions.DEFAULT_INSTANCE);
    InsertOrReplaceRequestOptions op4 =
        new InsertOrReplaceRequestOptions(WriteOptions.DEFAULT_INSTANCE);
    Assert.assertEquals(op3, op4);

    InsertOrReplaceRequestOptions op5 =
        new InsertOrReplaceRequestOptions(WriteOptions.DEFAULT_INSTANCE);
    InsertOrReplaceRequestOptions op6 =
        new InsertOrReplaceRequestOptions(WriteOptions.DEFAULT_INSTANCE);
    Assert.assertEquals(op5, op6);

    Assert.assertFalse(op5.equals("some-string"));
  }

  @Test
  public void testHashCode() {
    InsertOrReplaceRequestOptions op1 = new InsertOrReplaceRequestOptions();
    InsertOrReplaceRequestOptions op2 = new InsertOrReplaceRequestOptions();
    Assert.assertEquals(op1.hashCode(), op1.hashCode());
    Assert.assertEquals(op1.hashCode(), op2.hashCode());

    InsertOrReplaceRequestOptions op3 = new InsertOrReplaceRequestOptions();
    InsertOrReplaceRequestOptions op4 = new InsertOrReplaceRequestOptions();
    op3.setWriteOptions(null);
    op4.setWriteOptions(null);
    Assert.assertEquals(op3.hashCode(), op4.hashCode());
  }

  @Test
  public void setWriteOptionTest() {
    InsertOrReplaceRequestOptions op1 = new InsertOrReplaceRequestOptions();
    WriteOptions writeOptions = WriteOptions.DEFAULT_INSTANCE;
    op1.setWriteOptions(writeOptions);
    Assert.assertEquals(op1.getWriteOptions(), writeOptions);
  }
}
