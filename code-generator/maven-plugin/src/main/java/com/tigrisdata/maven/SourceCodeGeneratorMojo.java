package com.tigrisdata.maven;

import com.tigrisdata.tools.config.JavaCodeGenerationConfig;
import com.tigrisdata.tools.service.JsonSchemaToModelGenerator;
import com.tigrisdata.tools.service.ModelGenerator;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
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

  /** The current Maven project. */
  @Parameter(defaultValue = "${project}", readonly = true)
  protected MavenProject project;

  private static final String JSON_EXTENSION = ".json";

  @Override
  public void execute() {
    File[] schemaFiles =
        new File(schemaDir)
            .listFiles(
                pathname ->
                    pathname.getAbsolutePath().endsWith(JSON_EXTENSION)
                        || pathname.getAbsolutePath().endsWith(JSON_EXTENSION.toUpperCase()));
    if (schemaFiles == null || schemaFiles.length == 0) {
      getLog().warn("No schema files found in the schemaDir=" + schemaDir + " skipping execution");
      return;
    }

    getLog().info("Input schema files = " + Arrays.toString(schemaFiles));
    getLog().info("packageName = " + packageName);
    File outputDirectoryFile = new File(outputDirectory);
    ModelGenerator modelGenerator = new JsonSchemaToModelGenerator();
    modelGenerator.generate(
        Arrays.asList(schemaFiles),
        JavaCodeGenerationConfig.newBuilder(packageName, outputDirectoryFile).build());
    project.addCompileSourceRoot(outputDirectoryFile.getAbsolutePath());
  }
}
