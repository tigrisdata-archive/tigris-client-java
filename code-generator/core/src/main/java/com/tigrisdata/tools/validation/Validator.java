package com.tigrisdata.tools.validation;

import com.google.gson.JsonObject;

public interface Validator {

  void validate(JsonObject beforeSchema, JsonObject afterSchema) throws ValidationException;
}
