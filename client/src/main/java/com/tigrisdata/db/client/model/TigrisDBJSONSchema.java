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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;
import java.util.stream.Collectors;

public class TigrisDBJSONSchema implements TigrisDBSchema {

  private final String jsonSchemaFile;
  private final InputStream jsonSchemaInputStream;
  private static final Logger log = LoggerFactory.getLogger(TigrisDBJSONSchema.class);

  public TigrisDBJSONSchema(String jsonSchemaFile) {
    this.jsonSchemaFile = jsonSchemaFile;
    this.jsonSchemaInputStream = null;
  }

  public TigrisDBJSONSchema(InputStream jsonSchemaInputStream) {
    this.jsonSchemaFile = null;
    this.jsonSchemaInputStream = jsonSchemaInputStream;
  }

  @Override
  public String getSchemaContent() throws IOException {
    if (jsonSchemaFile == null && jsonSchemaInputStream == null) {
      throw new IllegalStateException("jsonSchemaFile and jsonSchemaInputStream both are null");
    }
    if (jsonSchemaInputStream != null) {
      try (BufferedReader bufferedReader =
          new BufferedReader(
              new InputStreamReader(jsonSchemaInputStream, StandardCharsets.UTF_8))) {
        log.info("reading schema from jsonSchemaInputStream");
        return bufferedReader.lines().collect(Collectors.joining("\n"));
      }
    } else {
      log.info("reading schema from jsonSchemaFile");
      return String.join("\n", Files.readAllLines(new File(jsonSchemaFile).toPath()));
    }
  }

  @Override
  public String toString() {
    return "TigrisDBJSONSchema{" + "jsonSchemaFile='" + jsonSchemaFile + '\'' + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TigrisDBJSONSchema that = (TigrisDBJSONSchema) o;

    return Objects.equals(jsonSchemaFile, that.jsonSchemaFile);
  }

  @Override
  public int hashCode() {
    return jsonSchemaFile != null ? jsonSchemaFile.hashCode() : 0;
  }
}
