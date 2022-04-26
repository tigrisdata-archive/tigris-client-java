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

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/** Represents database description */
public class DatabaseDescription {
  private final String name;
  private final DatabaseMetadata metadata;
  private final List<CollectionDescription> collectionsDescription;

  DatabaseDescription(
      String name, DatabaseMetadata metadata, List<CollectionDescription> collectionsDescription) {
    this.name = name;
    this.metadata = metadata;
    this.collectionsDescription = collectionsDescription;
  }

  /** @return database name */
  public String getName() {
    return name;
  }

  /** @return metadata about database */
  public DatabaseMetadata getMetadata() {
    return metadata;
  }

  /** @return description about collection */
  public List<CollectionDescription> getCollectionsDescription() {
    return Collections.unmodifiableList(collectionsDescription);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DatabaseDescription that = (DatabaseDescription) o;
    return Objects.equals(name, that.name)
        && Objects.equals(metadata, that.metadata)
        && Objects.equals(collectionsDescription, that.collectionsDescription);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, metadata, collectionsDescription);
  }
}
