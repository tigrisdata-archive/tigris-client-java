package com.tigrisdata.tools.schema.core;
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.victools.jsonschema.generator.CustomPropertyDefinition;
import com.github.victools.jsonschema.generator.Option;
import com.github.victools.jsonschema.generator.OptionPreset;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfig;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaVersion;
import com.tigrisdata.db.annotation.TigrisDBCollection;
import com.tigrisdata.db.annotation.TigrisDBCollectionField;
import com.tigrisdata.db.annotation.TigrisDBCollectionPrimaryKey;
import com.tigrisdata.db.type.TigrisCollectionType;
import com.tigrisdata.db.util.TypeUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/** Generates TigrisDB compatible JSON schema from Java models */
public class DefaultModelToTigrisDBJsonSchema implements ModelToJsonSchema {

  private static final String FORMAT = "format";
  private static final String BINARY = "binary";

  private static final String STRING = "string";
  private static final String DESCRIPTION = "description";
  private static final String TITLE = "title";
  private static final String TYPE = "type";
  private static final String ARRAY = "array";
  private static final String ITEMS = "items";

  private static final String $SCHEMA = "$schema";
  private static final String PRIMARY_KEYS = "primary_keys";
  private static final String ADDITIONAL_PROPERTIES = "additionalProperties";

  @Override
  public JsonNode toJsonSchema(Class<? extends TigrisCollectionType> clazz) {
    SchemaGeneratorConfigBuilder configBuilder =
        new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2020_12, OptionPreset.PLAIN_JSON);
    SchemaGeneratorConfig config =
        configBuilder
            .with(Option.EXTRA_OPEN_API_FORMAT_VALUES)
            .with(
                builder ->
                    builder
                        .forFields()
                        .withDescriptionResolver(
                            target -> {
                              TigrisDBCollectionField tigrisDBCollectionField =
                                  target.getAnnotation(TigrisDBCollectionField.class);
                              if (tigrisDBCollectionField != null
                                  && !tigrisDBCollectionField.description().isEmpty()) {
                                return target
                                    .getAnnotation(TigrisDBCollectionField.class)
                                    .description();
                              }
                              return null;
                            })
                        .withCustomDefinitionProvider(
                            (scope, context) -> {
                              // one or multiple dimensional byte array
                              if (scope.getType().getSignature().endsWith("[B")) {
                                ObjectNode customProperty =
                                    handleMultiDimensionalByteArray(scope.getType().getSignature());
                                return new CustomPropertyDefinition(customProperty);
                              }
                              return null;
                            }))
            .build();

    SchemaGenerator generator = new SchemaGenerator(config);
    JsonNode jsonSchema = generator.generateSchema(clazz);
    return customizeSchema(jsonSchema, clazz);
  }

  /**
   * handles special case where byte[] gets mapped to type: string, format: binary for one or
   * multiple dimension
   *
   * @param signature type signature
   * @return JSON property representation
   */
  private static ObjectNode handleMultiDimensionalByteArray(String signature) {
    int dimension = 0;
    for (char c : signature.toCharArray()) {
      if (c == '[') dimension++;
    }
    ObjectNode property;
    ObjectNode propertyItr = new ObjectMapper().createObjectNode();
    property = propertyItr;
    ObjectNode itemsItr = propertyItr;
    while (dimension > 1) {
      propertyItr.put(TYPE, ARRAY);

      itemsItr = new ObjectMapper().createObjectNode();

      propertyItr.set(ITEMS, itemsItr);

      propertyItr = itemsItr;
      dimension--;
    }
    if (itemsItr == null) {
      itemsItr = new ObjectMapper().createObjectNode();
    }
    itemsItr.put(TYPE, STRING);
    itemsItr.put(FORMAT, BINARY);

    return property;
  }

  private static JsonNode customizeSchema(
      JsonNode jsonSchema, Class<? extends TigrisCollectionType> clazz) {
    String schemaName = TypeUtils.getCollectionName(clazz);
    TigrisDBCollection tigrisDBCollection = clazz.getAnnotation(TigrisDBCollection.class);
    String description = null;
    if (tigrisDBCollection != null) {
      description = tigrisDBCollection.description();
    }

    ObjectNode objectNode = new ObjectMapper().createObjectNode();
    objectNode.put(TITLE, schemaName);

    if (description != null && !description.isEmpty()) {
      objectNode.put(DESCRIPTION, description);
    }

    objectNode.put(ADDITIONAL_PROPERTIES, false);

    // copy over generated schema
    Iterator<String> fields = jsonSchema.fieldNames();
    while (fields.hasNext()) {
      String field = fields.next();
      objectNode.set(field, jsonSchema.get(field));
    }

    // primary keys
    // inspect first level fields
    Map<Integer, String> primaryKeysMap = new HashMap<>();
    for (Field field : clazz.getDeclaredFields()) {
      TigrisDBCollectionPrimaryKey primaryKeyTag =
          field.getAnnotation(TigrisDBCollectionPrimaryKey.class);
      if (primaryKeyTag != null) {
        primaryKeysMap.put(primaryKeyTag.value(), field.getName());
      }
    }
    ArrayNode primaryKeys = objectNode.putArray(PRIMARY_KEYS);
    for (int i = 1; i <= primaryKeysMap.size(); i++) {
      primaryKeys.add(primaryKeysMap.get(i));
    }
    // remove generated $schema reference
    objectNode.remove($SCHEMA);
    return objectNode;
  }
}
