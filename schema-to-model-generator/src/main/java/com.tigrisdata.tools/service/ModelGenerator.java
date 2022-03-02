package com.tigrisdata.tools.service;

import com.tigrisdata.tools.config.JavaCodeGenerationConfig;

import java.io.File;
import java.util.List;

/**
 * Generates the Model from schema files
 */
public interface ModelGenerator {
    void generate(List<File> schemaFiles, JavaCodeGenerationConfig options)
            throws IllegalStateException;
}
