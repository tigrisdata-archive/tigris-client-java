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
package com.tigrisdata.maven;

import com.google.gson.JsonParser;
import com.tigrisdata.maven.utils.GitUtils;
import com.tigrisdata.tools.config.JavaCodeGenerationConfig;
import com.tigrisdata.tools.service.JsonSchemaToModelGenerator;
import com.tigrisdata.tools.service.ModelGenerator;
import com.tigrisdata.tools.validation.DefaultValidator;
import com.tigrisdata.tools.validation.ValidationException;
import com.tigrisdata.tools.validation.Validator;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;

/** Read TigrisDB Schema and generate Java models. */
@Mojo(name = "generate-sources", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class SourceCodeGeneratorMojo extends AbstractMojo {

  @Parameter(
      defaultValue = "${project.basedir}/src/main/resources/tigrisdb-schema",
      required = true,
      readonly = true)
  private String schemaDir;

  @Parameter(
      defaultValue = "${project.basedir}/target/generated-sources",
      required = true,
      readonly = true)
  private String outputDirectory;

  @Parameter(required = true, readonly = true)
  private String packageName;

  @Parameter(readonly = true, defaultValue = "false")
  private String disableValidation;

  /** The current Maven project. */
  @Parameter(defaultValue = "${project}", readonly = true)
  protected MavenProject project;

  private final Validator validator;
  private static final String JSON_EXTENSION = ".json";

  public SourceCodeGeneratorMojo() {
    this.validator = new DefaultValidator();
  }

  @Override
  public void execute() {
    File schemaDirFile =
        new File(project.getBasedir().getAbsolutePath() + File.separator + schemaDir);
    File[] schemaFiles =
        schemaDirFile.listFiles(
            pathname -> pathname.getAbsolutePath().toLowerCase().endsWith(JSON_EXTENSION));
    if (schemaFiles == null || schemaFiles.length == 0) {
      getLog().warn("No schema files found in the schemaDir=" + schemaDir + " skipping execution");
      return;
    }

    if (!Boolean.parseBoolean(disableValidation)) {
      try {
        validateSchema(schemaFiles);
      } catch (ValidationException ex) {
        getLog().error("Failed to validate schema", ex);
        return;
      } catch (IOException e) {
        getLog().warn("We were unable to validate the schema compatibility due to ", e);
      }
    }
    generateModels(schemaFiles);
  }

  private void validateSchema(File[] schemaFiles) throws IOException, ValidationException {
    final String repoRoot = System.getProperty("user.dir");

    for (File schemaFile : schemaFiles) {
      String filePathFromRoot =
          schemaFile.getAbsolutePath().replaceAll(repoRoot + File.separator, "");
      getLog().info("Validating " + filePathFromRoot);
      String previousContent = "";
      try {
        previousContent = GitUtils.getHeadContent(repoRoot, filePathFromRoot);
      } catch (IllegalArgumentException illegalArgumentException) {
        getLog().info("No HEAD copy of " + filePathFromRoot + " found, skipping validation");
        continue;
      }
      validator.validate(
          JsonParser.parseReader(new StringReader(previousContent)).getAsJsonObject(),
          JsonParser.parseReader(new FileReader(schemaFile)).getAsJsonObject());
      getLog().info("No issues found");
    }
  }

  private void generateModels(File[] schemaFiles) {
    File outputDirectoryFile =
        new File(project.getBasedir().getAbsolutePath() + File.separator + outputDirectory);
    getLog().info("Input schema files = " + Arrays.toString(schemaFiles));
    getLog().info("packageName = " + packageName);
    getLog().info("OutputDir = " + outputDirectoryFile.getAbsolutePath());

    ModelGenerator modelGenerator = new JsonSchemaToModelGenerator();
    modelGenerator.generate(
        Arrays.asList(schemaFiles),
        JavaCodeGenerationConfig.newBuilder(packageName, outputDirectoryFile).build());
    project.addCompileSourceRoot(outputDirectoryFile.getAbsolutePath());
  }
}
