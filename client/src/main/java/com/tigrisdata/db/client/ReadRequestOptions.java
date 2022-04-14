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
public class ReadRequestOptions {
  // TODO: add offset and make this class immutable
  private ReadOptions readOptions;
  private long skip;
  private long limit;

  public ReadRequestOptions() {}

  public ReadRequestOptions(ReadOptions readOptions) {
    this.readOptions = readOptions;
  }

  public ReadRequestOptions(ReadOptions readOptions, long skip, long limit) {
    this.readOptions = readOptions;
    this.skip = skip;
    this.limit = limit;
  }

  public ReadOptions getReadOptions() {
    return readOptions;
  }

  public void setReadOptions(ReadOptions readOptions) {
    this.readOptions = readOptions;
  }

  public long getSkip() {
    return skip;
  }

  public void setSkip(long skip) {
    this.skip = skip;
  }

  public long getLimit() {
    return limit;
  }

  public void setLimit(long limit) {
    this.limit = limit;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ReadRequestOptions that = (ReadRequestOptions) o;

    if (skip != that.skip) return false;
    if (limit != that.limit) return false;
    return Objects.equals(readOptions, that.readOptions);
  }

  @Override
  public int hashCode() {
    int result = readOptions != null ? readOptions.hashCode() : 0;
    result = 31 * result + (int) (skip ^ (skip >>> 32));
    result = 31 * result + (int) (limit ^ (limit >>> 32));
    return result;
  }
}
