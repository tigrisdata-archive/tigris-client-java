package com.tigrisdata.tools;

import com.tigrisdata.tools.config.JavaCodeGenerationConfig;
import com.tigrisdata.tools.service.JsonSchemaToModelGenerator;
import com.tigrisdata.tools.service.ModelGenerator;
import com.tigrisdata.tools.utils.CLIParser;
import java.io.File;
import java.util.List;
import org.apache.commons.cli.CommandLine;

public class SchemaToModelGenerator {

  public static void main(String[] args) throws Exception {
    CommandLine commandLine = CLIParser.parseArguments(args);

    JavaCodeGenerationConfig javaCodeGenerationConfig = CLIParser.transform(commandLine);
    List<File> inputSchemaFiles = CLIParser.getInputSchemaFiles(commandLine);

    ModelGenerator modelGenerator = new JsonSchemaToModelGenerator();
    modelGenerator.generate(inputSchemaFiles, javaCodeGenerationConfig);
  }
}
