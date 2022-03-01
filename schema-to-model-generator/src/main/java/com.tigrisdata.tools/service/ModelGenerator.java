package com.tigrisdata.tools.service;

import com.tigrisdata.tools.config.JavaCodeGenerationConfig;

import java.io.File;
import java.util.List;

public interface ModelGenerator {
  void generate(List<File> schemaFiles, JavaCodeGenerationConfig options)
      throws IllegalStateException;
}
