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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Collectors;

public class TigrisDBJSONSchema implements TigrisDBSchema {

  private final URL schemaURL;
  private final ObjectMapper objectMapper;
  private String schemaName;

  public TigrisDBJSONSchema(URL schemaURL) {
    this(schemaURL, new ObjectMapper());
  }

  public TigrisDBJSONSchema(URL schemaURL, ObjectMapper objectMapper) {
    this.schemaURL = schemaURL;
    this.objectMapper = objectMapper;
  }

  @Override
  public String getSchemaContent() throws IOException {
    try (BufferedReader bufferedReader =
        new BufferedReader(new InputStreamReader(schemaURL.openStream(), StandardCharsets.UTF_8))) {
      return bufferedReader.lines().collect(Collectors.joining("\n"));
    }
  }

  @Override
  public String getName() throws IOException {
    if (schemaName != null) {
      return schemaName;
    }
    this.schemaName = objectMapper.readTree(getSchemaContent()).get("name").asText();
    return schemaName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TigrisDBJSONSchema that = (TigrisDBJSONSchema) o;

    return Objects.equals(schemaURL, that.schemaURL);
  }

  @Override
  public int hashCode() {
    return schemaURL != null ? schemaURL.hashCode() : 0;
  }
}
