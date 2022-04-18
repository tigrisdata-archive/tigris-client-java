package com.tigrisdata.tools.validation;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ValidationTest {
  private final Logger log = LoggerFactory.getLogger(ValidationTest.class);

  @Test
  public void testSchemaValidation() throws Exception {
    File[] testDataDirs =
        new File("test-data/schema-compatibility-validation/").listFiles(File::isDirectory);
    for (File testDataDir : testDataDirs) {
      log.info("Running test for  {}", testDataDir.getName());

      JsonObject before = parse(testDataDir.getName() + "/before.json");
      JsonObject after = parse(testDataDir.getName() + "/after.json");
      JsonObject expectation = parse(testDataDir.getName() + "/expectation.json");
      boolean expectError = "error".equals(expectation.get("status").getAsString());
      Validator defaultValidator = new DefaultValidator();
      try {
        defaultValidator.validate(before, after);
        if (expectError) {
          Assert.fail("This must not pass");
        }
      } catch (ValidationException validationException) {
        if (expectError) {
          String expectedErrorMessage =
              expectation.getAsJsonObject("error").get("message").getAsString();
          Assert.assertEquals(expectedErrorMessage, validationException.getMessage());
        }
      }
    }
  }

  private static JsonObject parse(String path) throws IOException {
    return JsonParser.parseReader(
            new FileReader("test-data/schema-compatibility-validation/" + path))
        .getAsJsonObject();
  }
}
