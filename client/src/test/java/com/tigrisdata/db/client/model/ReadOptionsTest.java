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
package com.tigrisdata.db.client.model;

import org.junit.Assert;
import org.junit.Test;

public class ReadOptionsTest {

  @Test
  public void testEquals() {
    ReadOptions readOptions1 = new ReadOptions();
    ReadOptions readOptions2 = new ReadOptions();
    Assert.assertEquals(readOptions1, readOptions1);
    Assert.assertEquals(readOptions1, readOptions2);

    Assert.assertFalse(readOptions1.equals(null));
    Assert.assertFalse(readOptions1.equals("some-string"));

    Assert.assertFalse(
        new ReadOptions(new TransactionCtx("id1", "orig1"))
            .equals(new ReadOptions(new TransactionCtx("id2", "orig2"))));
  }

  @Test
  public void testHashCode() {
    ReadOptions readOptions1 = new ReadOptions();
    ReadOptions readOptions2 = new ReadOptions();
    Assert.assertEquals(readOptions1.hashCode(), readOptions2.hashCode());

    ReadOptions readOptions3 = new ReadOptions(new TransactionCtx("id", "orig"));
    ReadOptions readOptions4 = new ReadOptions(new TransactionCtx("id", "orig"));
    Assert.assertEquals(readOptions3.hashCode(), readOptions4.hashCode());
  }

  @Test
  public void testGetTransactionCtxTest() {
    ReadOptions readOptions1 = new ReadOptions();
    Assert.assertNull(readOptions1.getTransactionCtx());

    TransactionCtx transactionCtx = new TransactionCtx("id", "orig");
    ReadOptions readOptions2 = new ReadOptions(transactionCtx);
    Assert.assertEquals(transactionCtx, readOptions2.getTransactionCtx());
  }
}
