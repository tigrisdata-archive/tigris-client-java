package com.tigrisdata.tools.schema.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.tigrisdata.db.type.TigrisCollectionType;

/** Java models to JSON Schema generator */
public interface ModelToJsonSchema {
  /**
   * Generates JSON schema for the given Java class
   *
   * @param model a class that is of type {@link TigrisCollectionType}
   * @return JsonNode representing the schema
   */
  JsonNode toJsonSchema(Class<? extends TigrisCollectionType> model);
}
