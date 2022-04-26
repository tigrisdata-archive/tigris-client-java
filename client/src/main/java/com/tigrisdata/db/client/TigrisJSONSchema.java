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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tigrisdata.db.client.error.TigrisException;

import java.util.Objects;

/**
 * Represents TigrisDB JSON collection schema. This is used to read / parse and register
 * collection's schema.
 */
public class TigrisJSONSchema implements TigrisSchema {

  private String schemaName;
  private String schemaContent;

  /**
   * Constructs the {@link TigrisJSONSchema} from schemaContent and schemaName
   *
   * @param schemaContent content of schema
   */
  TigrisJSONSchema(String schemaContent) throws TigrisException {
    this.schemaContent = schemaContent;
    try {
      this.schemaName = new ObjectMapper().readTree(schemaContent).get("title").asText();
    } catch (JsonProcessingException ex) {
      throw new TigrisException("Could not parse schema", ex);
    }
  }

  @Override
  public String getSchemaContent() {
    return schemaContent;
  }

  @Override
  public String getName() {
    return schemaName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TigrisJSONSchema that = (TigrisJSONSchema) o;

    if (!Objects.equals(schemaName, that.schemaName)) return false;
    return Objects.equals(schemaContent, that.schemaContent);
  }

  @Override
  public int hashCode() {
    int result = schemaName != null ? schemaName.hashCode() : 0;
    result = 31 * result + (schemaContent != null ? schemaContent.hashCode() : 0);
    return result;
  }
}
