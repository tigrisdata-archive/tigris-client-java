package com.tigrisdata.tools.validation;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStreamReader;

public class ValidationTest {

  @Test
  public void changeInPrimaryKeysOrderTest() {
    JsonObject before = parse("/changeInPrimaryKeysOrderTest/before.json");
    JsonObject after = parse("/changeInPrimaryKeysOrderTest/after.json");
    ValidationRule validationRule = new PrimaryKeysRule();
    try {
      validationRule.validate(before, after);
      Assert.fail("This must not pass");
    } catch (ValidationException validationException) {
      Assert.assertEquals(
          "Change in primary_key field was detected", validationException.getMessage());
    }
  }

  @Test
  public void primaryKeyDoesNotExistInProperties() {
    JsonObject before = parse("/primaryKeyDoesNotExistInProperties/before.json");
    JsonObject after = parse("/primaryKeyDoesNotExistInProperties/after.json");
    ValidationRule validationRule = new PrimaryKeysRule();
    try {
      validationRule.validate(before, after);
      Assert.fail("This must not pass");
    } catch (ValidationException validationException) {
      Assert.assertEquals(
          "PrimaryKey field named \"name\" was not present in properties",
          validationException.getMessage());
    }
  }

  @Test
  public void primaryKeySuccessTest() throws ValidationException {
    JsonObject before = parse("/primaryKeySuccessTest/before.json");
    JsonObject after = parse("/primaryKeySuccessTest/after.json");
    ValidationRule validationRule = new PrimaryKeysRule();
    validationRule.validate(before, after);
  }

  @Test
  public void fieldTypeChangeRuleTest() {
    JsonObject before = parse("/fieldTypeChangeRuleTest/before.json");
    JsonObject after = parse("/fieldTypeChangeRuleTest/after.json");
    ValidationRule validationRule = new FieldTypeChangeRule();
    try {
      validationRule.validate(before, after);
      Assert.fail("This validation must fail");
    } catch (ValidationException validationException) {
      Assert.assertEquals(
          "Change in type detected for field named = balance", validationException.getMessage());
    }
  }

  @Test
  public void fieldRemovedTest() {
    JsonObject before = parse("/fieldRemovedTest/before.json");
    JsonObject after = parse("/fieldRemovedTest/after.json");
    ValidationRule validationRule = new FieldRemovedRule();
    try {
      validationRule.validate(before, after);
      Assert.fail("This validation must fail");
    } catch (ValidationException validationException) {
      Assert.assertEquals(
          "property name=balance, is not present in newer version",
          validationException.getMessage());
    }
  }

  private static JsonObject parse(String path) {
    return JsonParser.parseReader(
            new InputStreamReader(ValidationTest.class.getResourceAsStream(path)))
        .getAsJsonObject();
  }
}
