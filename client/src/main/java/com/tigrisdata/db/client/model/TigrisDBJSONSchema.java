package com.tigrisdata.db.client.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Collectors;

public class TigrisDBJSONSchema implements TigrisDBSchema {

  private final String jsonSchemaFile;
  private static final Logger log = LoggerFactory.getLogger(TigrisDBJSONSchema.class);

  public TigrisDBJSONSchema(String jsonSchemaFile) {
    this.jsonSchemaFile = jsonSchemaFile;
  }

  @Override
  public String getSchemaContent() {
    try {
      return Files.readAllLines(new File(jsonSchemaFile).toPath()).stream()
          .collect(Collectors.joining("\n"));
    } catch (IOException ioException) {
      log.error("failed to read schema", ioException);
      return "";
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

    return jsonSchemaFile != null
        ? jsonSchemaFile.equals(that.jsonSchemaFile)
        : that.jsonSchemaFile == null;
  }

  @Override
  public int hashCode() {
    return jsonSchemaFile != null ? jsonSchemaFile.hashCode() : 0;
  }
}
