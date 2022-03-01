package com.tigrisdata.tools;

import com.tigrisdata.tools.config.JavaCodeGenerationConfig;
import com.tigrisdata.tools.service.JsonSchemaToJavaModelGenerator;
import com.tigrisdata.tools.service.ModelGenerator;
import com.tigrisdata.tools.utils.CLIParser;
import org.apache.commons.cli.CommandLine;

import java.io.File;
import java.util.List;

public class TigrisDBSchemaToStructGenerator {

  public static void main(String[] args) throws Exception {
    CommandLine commandLine = CLIParser.parseArguments(args);

    JavaCodeGenerationConfig javaCodeGenerationConfig = CLIParser.transform(commandLine);
    List<File> inputSchemaFiles = CLIParser.getInputSchemaFiles(commandLine);

    ModelGenerator modelGenerator = new JsonSchemaToJavaModelGenerator();
    modelGenerator.generate(inputSchemaFiles, javaCodeGenerationConfig);
  }
}
