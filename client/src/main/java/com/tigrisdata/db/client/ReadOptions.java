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

import java.util.Objects;

/** Represents options related to Read operations */
class ReadOptions {
  private TransactionCtx transactionCtx;

  public ReadOptions() {}

  public ReadOptions(TransactionCtx transactionCtx) {
    this.transactionCtx = transactionCtx;
  }

  public TransactionCtx getTransactionCtx() {
    return transactionCtx;
  }

  public void setTransactionCtx(TransactionCtx transactionCtx) {
    this.transactionCtx = transactionCtx;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ReadOptions that = (ReadOptions) o;

    return Objects.equals(transactionCtx, that.transactionCtx);
  }

  @Override
  public int hashCode() {
    return transactionCtx != null ? transactionCtx.hashCode() : 0;
  }
}
