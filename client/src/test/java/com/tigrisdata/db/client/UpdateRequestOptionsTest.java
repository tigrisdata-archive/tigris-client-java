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

public class UpdateRequestOptionsTest {

  @Test
  public void equalsTest() {
    UpdateRequestOptions op1 = new UpdateRequestOptions();
    UpdateRequestOptions op2 = new UpdateRequestOptions();

    Assert.assertEquals(op1, op1);
    Assert.assertEquals(op1, op2);
    Assert.assertFalse(op1.equals(null));

    UpdateRequestOptions op3 = new UpdateRequestOptions(new WriteOptions());
    UpdateRequestOptions op4 = new UpdateRequestOptions(new WriteOptions());
    Assert.assertEquals(op3, op4);

    TransactionCtx transactionCtx5 = new TransactionCtx("id", "origin");
    TransactionCtx transactionCtx6 = new TransactionCtx("id", "origin");

    UpdateRequestOptions op5 = new UpdateRequestOptions(new WriteOptions(transactionCtx5));
    UpdateRequestOptions op6 = new UpdateRequestOptions(new WriteOptions(transactionCtx6));
    Assert.assertEquals(op5, op6);

    Assert.assertFalse(op5.equals("some-string"));
  }

  @Test
  public void testHashCode() {
    UpdateRequestOptions op1 = new UpdateRequestOptions();
    UpdateRequestOptions op2 = new UpdateRequestOptions();
    Assert.assertEquals(op1.hashCode(), op1.hashCode());
    Assert.assertEquals(op1.hashCode(), op2.hashCode());

    UpdateRequestOptions op3 = new UpdateRequestOptions(null);
    UpdateRequestOptions op4 = new UpdateRequestOptions(null);
    Assert.assertEquals(op3.hashCode(), op4.hashCode());
  }
}
