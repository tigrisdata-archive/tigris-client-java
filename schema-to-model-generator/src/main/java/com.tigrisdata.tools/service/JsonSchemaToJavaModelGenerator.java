package com.tigrisdata.tools.service;

import com.sun.codemodel.JCodeModel;
import com.tigrisdata.tools.config.JavaCodeGenerationConfig;
import com.tigrisdata.tools.schema.TigrisDBRuleFactory;
import org.jsonschema2pojo.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class JsonSchemaToJavaModelGenerator implements ModelGenerator {

  public JsonSchemaToJavaModelGenerator() {}

  @Override
  public void generate(
      List<File> jsonSchemaFiles, JavaCodeGenerationConfig javaCodeGenerationConfig)
      throws IllegalStateException {
    if (!javaCodeGenerationConfig.getOutputDirectory().exists()) {
      javaCodeGenerationConfig.getOutputDirectory().mkdirs();
    }
    for (File jsonSchemaFile : jsonSchemaFiles) {
      try {
        JCodeModel codeModel = new JCodeModel();
        URL source = jsonSchemaFile.toPath().toUri().toURL();
        GenerationConfig generationConfig = javaCodeGenerationConfig.toGenerationConfig();

        SchemaMapper mapper =
            new SchemaMapper(
                new TigrisDBRuleFactory(
                    generationConfig, new Jackson2Annotator(generationConfig), new SchemaStore()),
                new SchemaGenerator());
        mapper.generate(codeModel, "title", javaCodeGenerationConfig.getJavaPackageName(), source);
        codeModel.build(javaCodeGenerationConfig.getOutputDirectory());
      } catch (MalformedURLException malformedURLException) {
        throw new IllegalStateException(
            "Cannot find the json schema file " + jsonSchemaFile, malformedURLException);
      } catch (IOException ioException) {
        throw new IllegalStateException(
            "Failed to read jsin schema file " + jsonSchemaFile, ioException);
      }
    }
  }
}
