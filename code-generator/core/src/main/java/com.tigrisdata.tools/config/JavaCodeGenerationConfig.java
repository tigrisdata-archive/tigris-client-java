package com.tigrisdata.tools.config;

import org.jsonschema2pojo.AnnotationStyle;
import org.jsonschema2pojo.DefaultGenerationConfig;
import org.jsonschema2pojo.GenerationConfig;

import java.io.File;

public class JavaCodeGenerationConfig {

  private final String javaPackageName;
  private final File outputDirectory;

  private final boolean usePrimitiveTypes;
  private final boolean includeAccessors;
  private final boolean includeJSR303Annotations;
  private final boolean initializeCollections;
  private final boolean includeConstructorForRequiredTypesOnly;
  private final boolean generateBuilders;
  private final boolean includeTypeInformation;
  private final boolean includeHashCodeAndEquals;
  private final AnnotationStyle annotationStyle;
  private final boolean includeAdditionalProperties;
  private final boolean useTitleAsClassName;

  private JavaCodeGenerationConfig(Builder builder) {
    this.javaPackageName = builder.javaPackageName;
    this.outputDirectory = builder.outputDirectory;

    this.usePrimitiveTypes = builder.usePrimitiveTypes;
    this.includeAccessors = builder.includeAccessors;
    this.includeJSR303Annotations = builder.includeJSR303Annotations;
    this.initializeCollections = builder.initializeCollections;
    this.includeConstructorForRequiredTypesOnly = builder.includeConstructorForRequiredTypesOnly;
    this.generateBuilders = builder.generateBuilders;
    this.includeTypeInformation = builder.includeTypeInformation;
    this.includeHashCodeAndEquals = builder.includeHashCodeAndEquals;
    this.annotationStyle = builder.annotationStyle;
    this.includeAdditionalProperties = builder.includeAdditionalProperties;
    this.useTitleAsClassName = builder.useTitleAsClassName;
  }

  public static Builder newBuilder(String javaPackageName, File outputDirectory) {
    return new Builder(javaPackageName, outputDirectory);
  }

  public String getJavaPackageName() {
    return javaPackageName;
  }

  public boolean isUsePrimitiveTypes() {
    return usePrimitiveTypes;
  }

  public boolean isIncludeAccessors() {
    return includeAccessors;
  }

  public boolean isIncludeJSR303Annotations() {
    return includeJSR303Annotations;
  }

  public boolean isUseTitleAsClassName() {
    return useTitleAsClassName;
  }

  public boolean isInitializeCollections() {
    return initializeCollections;
  }

  public boolean isIncludeConstructorForRequiredTypesOnly() {
    return includeConstructorForRequiredTypesOnly;
  }

  public boolean isGenerateBuilders() {
    return generateBuilders;
  }

  public boolean isIncludeTypeInformation() {
    return includeTypeInformation;
  }

  public boolean isIncludeHashCodeAndEquals() {
    return includeHashCodeAndEquals;
  }

  public AnnotationStyle getAnnotationStyle() {
    return annotationStyle;
  }

  public boolean isIncludeAdditionalProperties() {
    return includeAdditionalProperties;
  }

  public File getOutputDirectory() {
    return outputDirectory;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    JavaCodeGenerationConfig config = (JavaCodeGenerationConfig) o;

    if (usePrimitiveTypes != config.usePrimitiveTypes) return false;
    if (includeAccessors != config.includeAccessors) return false;
    if (includeJSR303Annotations != config.includeJSR303Annotations) return false;
    if (initializeCollections != config.initializeCollections) return false;
    if (includeConstructorForRequiredTypesOnly != config.includeConstructorForRequiredTypesOnly)
      return false;
    if (generateBuilders != config.generateBuilders) return false;
    if (includeTypeInformation != config.includeTypeInformation) return false;
    if (includeHashCodeAndEquals != config.includeHashCodeAndEquals) return false;
    if (includeAdditionalProperties != config.includeAdditionalProperties) return false;
    if (useTitleAsClassName != config.useTitleAsClassName) return false;
    if (!javaPackageName.equals(config.javaPackageName)) return false;
    if (!outputDirectory.equals(config.outputDirectory)) return false;
    return annotationStyle == config.annotationStyle;
  }

  @Override
  public int hashCode() {
    int result = javaPackageName.hashCode();
    result = 31 * result + outputDirectory.hashCode();
    result = 31 * result + (usePrimitiveTypes ? 1 : 0);
    result = 31 * result + (includeAccessors ? 1 : 0);
    result = 31 * result + (includeJSR303Annotations ? 1 : 0);
    result = 31 * result + (initializeCollections ? 1 : 0);
    result = 31 * result + (includeConstructorForRequiredTypesOnly ? 1 : 0);
    result = 31 * result + (generateBuilders ? 1 : 0);
    result = 31 * result + (includeTypeInformation ? 1 : 0);
    result = 31 * result + (includeHashCodeAndEquals ? 1 : 0);
    result = 31 * result + (annotationStyle != null ? annotationStyle.hashCode() : 0);
    result = 31 * result + (includeAdditionalProperties ? 1 : 0);
    result = 31 * result + (useTitleAsClassName ? 1 : 0);
    return result;
  }

  public GenerationConfig toGenerationConfig() {
    return new DefaultGenerationConfig() {
      @Override
      public boolean isGenerateBuilders() {
        return true;
      }

      @Override
      public boolean isIncludeTypeInfo() {
        return includeTypeInformation;
      }

      @Override
      public boolean isUsePrimitives() {
        return usePrimitiveTypes;
      }

      @Override
      public String getTargetPackage() {
        return javaPackageName;
      }

      @Override
      public boolean isIncludeHashcodeAndEquals() {
        return includeHashCodeAndEquals;
      }

      @Override
      public AnnotationStyle getAnnotationStyle() {
        return annotationStyle;
      }

      @Override
      public boolean isIncludeJsr303Annotations() {
        return includeJSR303Annotations;
      }

      @Override
      public boolean isConstructorsRequiredPropertiesOnly() {
        return includeConstructorForRequiredTypesOnly;
      }

      @Override
      public boolean isIncludeGetters() {
        return includeAccessors;
      }

      @Override
      public boolean isIncludeConstructors() {
        return includeConstructorForRequiredTypesOnly;
      }

      @Override
      public boolean isIncludeSetters() {
        return includeAccessors;
      }

      @Override
      public boolean isUseInnerClassBuilders() {
        return false;
      }

      @Override
      public boolean isIncludeAdditionalProperties() {
        return includeAdditionalProperties;
      }

      @Override
      public boolean isUseTitleAsClassname() {
        return useTitleAsClassName;
      }
    };
  }

  public static final class Builder {
    private final String javaPackageName;
    private final File outputDirectory;

    private boolean usePrimitiveTypes = true;
    private boolean includeAccessors = true;
    private boolean includeJSR303Annotations = true;
    private boolean initializeCollections = true;
    private boolean includeConstructorForRequiredTypesOnly = true;
    private boolean generateBuilders = false;
    private boolean includeTypeInformation = false;
    private boolean includeHashCodeAndEquals = true;
    private AnnotationStyle annotationStyle = AnnotationStyle.JACKSON;
    private boolean includeAdditionalProperties = false;
    private boolean useTitleAsClassName = true;

    private Builder(String javaPackageName, File outputDirectory) {
      this.javaPackageName = javaPackageName;
      this.outputDirectory = outputDirectory;
    }

    public Builder usePrimitiveTypes(boolean usePrimitiveTypes) {
      this.usePrimitiveTypes = usePrimitiveTypes;
      return this;
    }

    public Builder includeAccessors(boolean includeAccessors) {
      this.includeAccessors = includeAccessors;
      return this;
    }

    public Builder includeJSR303Annotations(boolean includeJSR303Annotations) {
      this.includeJSR303Annotations = includeJSR303Annotations;
      return this;
    }

    public Builder initializeCollections(boolean initializeCollections) {
      this.initializeCollections = initializeCollections;
      return this;
    }

    public Builder includeConstructorForRequiredTypesOnly(
        boolean includeConstructorForRequiredTypesOnly) {
      this.includeConstructorForRequiredTypesOnly = includeConstructorForRequiredTypesOnly;
      return this;
    }

    public Builder generateBuilders(boolean generateBuilders) {
      this.generateBuilders = generateBuilders;
      return this;
    }

    public Builder includeTypeInformation(boolean includeTypeInformation) {
      this.includeTypeInformation = includeTypeInformation;
      return this;
    }

    public Builder includeHashCodeAndEquals(boolean includeHashCodeAndEquals) {
      this.includeHashCodeAndEquals = includeHashCodeAndEquals;
      return this;
    }

    public Builder withAnnotationStyle(AnnotationStyle annotationStyle) {
      this.annotationStyle = annotationStyle;
      return this;
    }

    public Builder includeAdditionalProperties(boolean includeAdditionalProperties) {
      this.includeAdditionalProperties = includeAdditionalProperties;
      return this;
    }

    public Builder useTitleAsClassName(boolean useTitleAsClassName) {
      this.useTitleAsClassName = useTitleAsClassName;
      return this;
    }

    public JavaCodeGenerationConfig build() {
      return new JavaCodeGenerationConfig(this);
    }
  }
}
