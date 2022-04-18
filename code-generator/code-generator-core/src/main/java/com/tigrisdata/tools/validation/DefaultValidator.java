package com.tigrisdata.tools.validation;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class DefaultValidator implements Validator {

  private final List<ValidationRule> validationRules;

  public DefaultValidator() {
    this.validationRules = new ArrayList<>();
    validationRules.add(new FieldTypeChangeRule());
    validationRules.add(new FieldRemovedRule());
    validationRules.add(new PrimaryKeysRule());
  }

  @Override
  public void validate(JsonObject beforeSchema, JsonObject afterSchema) throws ValidationException {
    for (ValidationRule validationRule : validationRules) {
      validationRule.validate(beforeSchema, afterSchema);
    }
  }
}
