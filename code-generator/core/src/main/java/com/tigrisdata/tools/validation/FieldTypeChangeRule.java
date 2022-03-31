package com.tigrisdata.tools.validation;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class FieldTypeChangeRule implements ValidationRule {

  private static final String PROPERTIES = "properties";
  private static final String TYPE = "type";

  @Override
  public void validate(JsonObject beforeSchema, JsonObject afterSchema) throws ValidationException {
    Map<String, String> beforeTypes = new HashMap<>();
    for (String beforeKey : beforeSchema.getAsJsonObject(PROPERTIES).keySet()) {
      beforeTypes.put(
          beforeKey,
          beforeSchema
              .getAsJsonObject(PROPERTIES)
              .getAsJsonObject(beforeKey)
              .getAsJsonPrimitive(TYPE)
              .getAsString());
    }
    // check after keys
    for (String afterKey : afterSchema.getAsJsonObject(PROPERTIES).keySet()) {
      if (beforeTypes.containsKey(afterKey)) {
        String beforeType = beforeTypes.get(afterKey);
        String afterType =
            afterSchema
                .getAsJsonObject(PROPERTIES)
                .getAsJsonObject(afterKey)
                .getAsJsonPrimitive(TYPE)
                .getAsString();

        if (!beforeType.equals(afterType)) {
          throw new ValidationException("Change in type detected for field named = " + afterKey);
        }
      }
    }
  }
}
