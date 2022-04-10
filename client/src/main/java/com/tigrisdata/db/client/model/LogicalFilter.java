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

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.stream.Collectors;

public class LogicalFilter implements TigrisFilter {

  private final LogicalFilterOperator logicalFilterOperator;
  private final TigrisFilter[] tigrisFilters;

  LogicalFilter(LogicalFilterOperator logicalFilterOperator, TigrisFilter[] tigrisFilters) {
    if (tigrisFilters.length < 2) {
      throw new IllegalArgumentException(
          "At least 2 filters are required to form composite filter");
    }
    this.logicalFilterOperator = logicalFilterOperator;
    this.tigrisFilters = tigrisFilters;
  }

  @Override
  public String toJSON(ObjectMapper objectMapper) {
    return "{\""
        + logicalFilterOperator.getOperator()
        + "\":["
        + Arrays.stream(tigrisFilters)
            .map(filter -> filter.toJSON(objectMapper))
            .collect(Collectors.joining(","))
        + "]";
  }
}
