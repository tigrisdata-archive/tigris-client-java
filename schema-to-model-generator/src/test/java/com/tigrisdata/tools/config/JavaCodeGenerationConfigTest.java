package com.tigrisdata.tools.config;

import static org.junit.Assert.*;

import java.io.File;
import org.jsonschema2pojo.AnnotationStyle;
import org.junit.Test;

public class JavaCodeGenerationConfigTest {
  private static final String JAVA_PACKAGE = "com.tigrisdata.test";
  private static final File TEMP_DIRECTORY = new File(System.getProperty("java.io.tmpdir"));

  @Test
  public void testDefaults() {
    JavaCodeGenerationConfig config =
        JavaCodeGenerationConfig.newBuilder(JAVA_PACKAGE, TEMP_DIRECTORY).build();

    assertEquals(config.getJavaPackageName(), JAVA_PACKAGE);
    assertEquals(config.getOutputDirectory(), TEMP_DIRECTORY);

    assertTrue(config.isUsePrimitiveTypes());
    assertTrue(config.isIncludeAccessors());
    assertTrue(config.isIncludeJSR303Annotations());
    assertTrue(config.isInitializeCollections());
    assertTrue(config.isIncludeConstructorForRequiredTypesOnly());
    assertFalse(config.isGenerateBuilders());
    assertTrue(config.isIncludeTypeInformation());
    assertTrue(config.isIncludeHashCodeAndEquals());

    assertEquals(AnnotationStyle.JACKSON, config.getAnnotationStyle());
    assertFalse(config.isIncludeAdditionalProperties());
    assertTrue(config.isUseTitleAsClassName());
  }

  @Test
  public void testEqualsAndHashCode() {
    JavaCodeGenerationConfig config1 =
        JavaCodeGenerationConfig.newBuilder(JAVA_PACKAGE, TEMP_DIRECTORY).build();

    JavaCodeGenerationConfig config2 =
        JavaCodeGenerationConfig.newBuilder(JAVA_PACKAGE, TEMP_DIRECTORY).build();

    assertEquals(config1, config1);

    assertEquals(config1, config2);
    assertEquals(config2, config1);

    assertEquals(config1.hashCode(), config2.hashCode());
    assertEquals(config1.hashCode(), config1.hashCode());
  }

  @Test
  public void testBuilder() {
    JavaCodeGenerationConfig config =
        JavaCodeGenerationConfig.newBuilder(JAVA_PACKAGE, TEMP_DIRECTORY)
            .usePrimitiveTypes(false)
            .includeAccessors(false)
            .includeJSR303Annotations(false)
            .initializeCollections(false)
            .includeConstructorForRequiredTypesOnly(false)
            .generateBuilders(true)
            .includeTypeInformation(false)
            .includeHashCodeAndEquals(false)
            .withAnnotationStyle(AnnotationStyle.GSON)
            .includeAdditionalProperties(true)
            .useTitleAsClassName(false)
            .build();

    assertEquals(config.getJavaPackageName(), JAVA_PACKAGE);
    assertEquals(config.getOutputDirectory(), TEMP_DIRECTORY);

    assertFalse(config.isUsePrimitiveTypes());
    assertFalse(config.isIncludeAccessors());
    assertFalse(config.isIncludeJSR303Annotations());
    assertFalse(config.isInitializeCollections());
    assertFalse(config.isIncludeConstructorForRequiredTypesOnly());
    assertTrue(config.isGenerateBuilders());
    assertFalse(config.isIncludeTypeInformation());
    assertFalse(config.isIncludeHashCodeAndEquals());

    assertEquals(AnnotationStyle.GSON, config.getAnnotationStyle());
    assertTrue(config.isIncludeAdditionalProperties());
    assertFalse(config.isUseTitleAsClassName());
  }
}
