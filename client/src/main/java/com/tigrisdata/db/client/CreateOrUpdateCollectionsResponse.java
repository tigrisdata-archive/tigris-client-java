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

/** Represents Server response for CreateOrUpdateCollections operation */
public class CreateOrUpdateCollectionsResponse {
  private final TigrisDBResponse tigrisDBResponse;

  public CreateOrUpdateCollectionsResponse(TigrisDBResponse tigrisDBResponse) {
    this.tigrisDBResponse = tigrisDBResponse;
  }

  public TigrisDBResponse getTigrisDBResponse() {
    return tigrisDBResponse;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    CreateOrUpdateCollectionsResponse that = (CreateOrUpdateCollectionsResponse) o;

    return Objects.equals(tigrisDBResponse, that.tigrisDBResponse);
  }

  @Override
  public int hashCode() {
    return tigrisDBResponse != null ? tigrisDBResponse.hashCode() : 0;
  }
}
