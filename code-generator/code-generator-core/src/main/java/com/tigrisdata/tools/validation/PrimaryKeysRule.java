package com.tigrisdata.tools.validation;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class PrimaryKeysRule implements ValidationRule {

  private static final String PRIMARY_KEYS = "primary_key";
  private static final String PROPERTIES = "properties";

  @Override
  public void validate(JsonObject beforeSchema, JsonObject afterSchema) throws ValidationException {
    JsonArray beforePrimaryKeys = beforeSchema.get(PRIMARY_KEYS).getAsJsonArray();
    JsonArray afterPrimaryKeys = afterSchema.get(PRIMARY_KEYS).getAsJsonArray();
    if (!beforePrimaryKeys.equals(afterPrimaryKeys)) {
      throw new ValidationException("Change in " + PRIMARY_KEYS + " field was detected");
    }

    // all primary_keys field must exist in properties
    for (JsonElement afterPrimaryKey : afterPrimaryKeys) {
      boolean exists = false;
      for (String property : afterSchema.getAsJsonObject(PROPERTIES).keySet()) {
        if (property.equals(afterPrimaryKey.getAsString())) {
          exists = true;
          break;
        }
      }
      if (!exists) {
        throw new ValidationException(
            "PrimaryKey field named "
                + afterPrimaryKey.toString()
                + " was not present in properties");
      }
    }
  }
}
