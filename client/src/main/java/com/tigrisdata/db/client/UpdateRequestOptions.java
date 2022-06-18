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

/** represents options for UpdateRequest */
public class UpdateRequestOptions {
  private WriteOptions writeOptions;

  public UpdateRequestOptions() {
    this.writeOptions = WriteOptions.DEFAULT_INSTANCE;
  }

  public UpdateRequestOptions(WriteOptions writeOptions) {
    this.writeOptions = writeOptions;
  }

  public WriteOptions getWriteOptions() {
    return writeOptions;
  }

  public void setWriteOptions(WriteOptions writeOptions) {
    this.writeOptions = writeOptions;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    UpdateRequestOptions that = (UpdateRequestOptions) o;

    return Objects.equals(writeOptions, that.writeOptions);
  }

  @Override
  public int hashCode() {
    return writeOptions != null ? writeOptions.hashCode() : 0;
  }
}
