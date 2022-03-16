package com.tigrisdata.tools.utils;

import com.tigrisdata.tools.config.JavaCodeGenerationConfig;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jsonschema2pojo.AnnotationStyle;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/** Utility for command line argument parsing */
public final class CLIParser {
  private CLIParser() {}

  private static final Option INPUT_SCHEMA_FILES =
      Option.builder()
          .option("i")
          .hasArg()
          .desc("Comma seperated input json schema files")
          .required()
          .longOpt("input_json_schema_files")
          .valueSeparator(',')
          .build();

  private static final Option OUTPUT_DIRECTORY =
      Option.builder()
          .option("o")
          .hasArg()
          .desc("Output directory")
          .required()
          .longOpt("output_directory")
          .build();
  private static final Option JAVA_PACKAGE_NAME =
      Option.builder()
          .option("package")
          .hasArg()
          .desc("Java package name")
          .longOpt("java_package_name")
          .required()
          .build();

  private static final Option INCLUDE_ACCESSOR =
      Option.builder()
          .option("accessors")
          .desc("Include setter and getters")
          .longOpt("include_accessors")
          .build();

  private static final Option INCLUDE_JSR_303_ANNOTATIONS =
      Option.builder()
          .option("jsr303")
          .desc("Include JSR303 " + "annotations")
          .longOpt("include_jsr303")
          .build();

  private static final Option INITIALIZE_COLLECTIONS =
      Option.builder()
          .option("ic")
          .desc("Initialize collections")
          .longOpt("initialize_collections")
          .build();

  private static final Option INCLUDE_CONSTRUCTOR_FOR_REQUIRED_TYPES_ONLY =
      Option.builder()
          .option("icrt")
          .desc("Include constructor for required types only")
          .longOpt("include_constructor_for_required_types_only")
          .build();

  private static final Option GENERATE_BUILDERS =
      Option.builder()
          .option("builders")
          .desc("Generate builders")
          .longOpt("generate_builders")
          .build();

  private static final Option INCLUDE_TYPE_INFORMATION =
      Option.builder()
          .option("type_info")
          .desc("include type information")
          .longOpt("include_type_information")
          .build();

  private static final Option INCLUDE_HASH_CODE_AND_EQUALS =
      Option.builder()
          .option("he")
          .desc("include hashcode and equals")
          .longOpt("include_hashcode_and_equals")
          .build();

  private static final Option ANNOTATIONS_STYLE =
      Option.builder()
          .option("annotation")
          .hasArg()
          .desc("Annotation style")
          .longOpt("annotation_style")
          .build();

  public static CommandLine parseArguments(String[] args) throws ParseException {
    Options options = new Options();

    try {

      options.addOption(INPUT_SCHEMA_FILES);
      options.addOption(OUTPUT_DIRECTORY);
      options.addOption(JAVA_PACKAGE_NAME);
      options.addOption(INCLUDE_ACCESSOR);
      options.addOption(INCLUDE_JSR_303_ANNOTATIONS);
      options.addOption(INITIALIZE_COLLECTIONS);
      options.addOption(INCLUDE_CONSTRUCTOR_FOR_REQUIRED_TYPES_ONLY);
      options.addOption(GENERATE_BUILDERS);
      options.addOption(INCLUDE_TYPE_INFORMATION);
      options.addOption(INCLUDE_HASH_CODE_AND_EQUALS);
      options.addOption(ANNOTATIONS_STYLE);

      CommandLineParser parser = new DefaultParser();
      return parser.parse(options, args);
    } catch (ParseException parseException) {
      new HelpFormatter().printHelp("model-generator", options);
      throw parseException;
    }
  }

  public static List<File> getInputSchemaFiles(CommandLine commandLine) {
    return Arrays.stream(commandLine.getOptionValues(INPUT_SCHEMA_FILES))
        .map(File::new)
        .collect(Collectors.toList());
  }

  public static JavaCodeGenerationConfig transform(CommandLine commandLine) {
    JavaCodeGenerationConfig.Builder builder =
        JavaCodeGenerationConfig.newBuilder(
            commandLine.getOptionValue(JAVA_PACKAGE_NAME),
            new File(commandLine.getOptionValue(OUTPUT_DIRECTORY)));

    if (commandLine.hasOption(INCLUDE_ACCESSOR)) {
      builder.includeAccessors(true);
    }
    if (commandLine.hasOption(INCLUDE_JSR_303_ANNOTATIONS)) {
      builder.includeJSR303Annotations(true);
    }
    if (commandLine.hasOption(INITIALIZE_COLLECTIONS)) {
      builder.initializeCollections(true);
    }
    if (commandLine.hasOption(INCLUDE_CONSTRUCTOR_FOR_REQUIRED_TYPES_ONLY)) {
      builder.includeConstructorForRequiredTypesOnly(true);
    }
    if (commandLine.hasOption(GENERATE_BUILDERS)) {
      builder.generateBuilders(true);
    }
    if (commandLine.hasOption(INCLUDE_TYPE_INFORMATION)) {
      builder.includeTypeInformation(true);
    }
    if (commandLine.hasOption(INCLUDE_HASH_CODE_AND_EQUALS)) {
      builder.includeHashCodeAndEquals(true);
    }
    if (commandLine.hasOption(ANNOTATIONS_STYLE)) {
      builder.withAnnotationStyle(
          AnnotationStyle.valueOf(commandLine.getOptionValue(ANNOTATIONS_STYLE)));
    }
    return builder.build();
  }
}
