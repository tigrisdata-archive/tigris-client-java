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

/** Represents description about collection */
public class CollectionDescription {
  private final String name;
  private final CollectionMetadata metadata;
  private final TigrisSchema schema;

  CollectionDescription(String name, CollectionMetadata metadata, TigrisSchema schema) {
    this.name = name;
    this.metadata = metadata;
    this.schema = schema;
  }

  /** @return collection name */
  public String getName() {
    return name;
  }

  /** @return metadata about collection */
  public CollectionMetadata getMetadata() {
    return metadata;
  }

  /** @return schema of the collection */
  public TigrisSchema getSchema() {
    return schema;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CollectionDescription that = (CollectionDescription) o;
    return Objects.equals(name, that.name)
        && Objects.equals(metadata, that.metadata)
        && Objects.equals(schema, that.schema);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, metadata, schema);
  }
}
