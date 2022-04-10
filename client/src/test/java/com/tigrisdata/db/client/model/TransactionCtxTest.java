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

public class TransactionCtxTest {

  @Test
  public void testEquals() {
    TransactionCtx transactionCtx1 = new TransactionCtx("id", "origin");
    TransactionCtx transactionCtx2 = new TransactionCtx("id", "origin");
    Assert.assertEquals(transactionCtx1, transactionCtx1);
    Assert.assertEquals(transactionCtx1, transactionCtx2);

    TransactionCtx transactionCtx3 = new TransactionCtx("id", null);
    TransactionCtx transactionCtx4 = new TransactionCtx("id", null);
    Assert.assertEquals(transactionCtx3, transactionCtx4);

    TransactionCtx transactionCtx5 = new TransactionCtx(null, "origin");
    TransactionCtx transactionCtx6 = new TransactionCtx(null, "origin");
    Assert.assertEquals(transactionCtx5, transactionCtx6);

    TransactionCtx transactionCtx7 = new TransactionCtx("id7", "origin7");
    TransactionCtx transactionCtx8 = new TransactionCtx("id8", "origin8");

    Assert.assertFalse(transactionCtx7.equals(transactionCtx8));
    Assert.assertFalse(transactionCtx7.equals("some-string"));
    Assert.assertFalse(transactionCtx7.equals(null));
  }

  @Test
  public void testHashCode() {
    TransactionCtx transactionCtx1 = new TransactionCtx("id", "origin");
    TransactionCtx transactionCtx2 = new TransactionCtx("id", "origin");
    Assert.assertEquals(transactionCtx1.hashCode(), transactionCtx1.hashCode());
    Assert.assertEquals(transactionCtx1.hashCode(), transactionCtx2.hashCode());

    TransactionCtx transactionCtx3 = new TransactionCtx("id", null);
    TransactionCtx transactionCtx4 = new TransactionCtx("id", null);
    Assert.assertEquals(transactionCtx3.hashCode(), transactionCtx4.hashCode());

    TransactionCtx transactionCtx5 = new TransactionCtx(null, "origin");
    TransactionCtx transactionCtx6 = new TransactionCtx(null, "origin");
    Assert.assertEquals(transactionCtx5.hashCode(), transactionCtx6.hashCode());
  }
}
