package com.tigrisdata.tools.validation;

import com.google.gson.JsonObject;

import java.util.HashSet;
import java.util.Set;

public class FieldRemovedRule implements ValidationRule {
  private static final String PROPERTIES = "properties";

  @Override
  public void validate(JsonObject beforeSchema, JsonObject afterSchema) throws ValidationException {
    Set<String> beforeKeys = new HashSet<>(beforeSchema.getAsJsonObject(PROPERTIES).keySet());
    Set<String> afterKeys = new HashSet<>(afterSchema.getAsJsonObject(PROPERTIES).keySet());
    for (String beforeKey : beforeKeys) {
      if (!afterKeys.contains(beforeKey)) {
        throw new ValidationException(
            "property name=" + beforeKey + ", is not present in newer version");
      }
    }
  }
}
