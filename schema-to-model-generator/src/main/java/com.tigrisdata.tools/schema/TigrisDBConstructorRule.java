package com.tigrisdata.tools.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;
import org.jsonschema2pojo.GenerationConfig;
import org.jsonschema2pojo.Schema;
import org.jsonschema2pojo.rules.Rule;
import org.jsonschema2pojo.rules.RuleFactory;
import org.jsonschema2pojo.util.NameHelper;
import org.jsonschema2pojo.util.ReflectionHelper;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This class is created referring {@code ConstructorRule} for TigrisDB Schema This class is
 * responsible for creating constructors with PK as args. This is to tell users those are required
 * field of your type.
 *
 * <p>NOTE: The order of the constructor argument matches the order defined in the JSON schema
 */
public class TigrisDBConstructorRule implements Rule<JDefinedClass, JDefinedClass> {

  private static final String PRIMARY_KEYS = "primary_keys";
  private final RuleFactory ruleFactory;
  private final ReflectionHelper reflectionHelper;

  public TigrisDBConstructorRule(RuleFactory ruleFactory, ReflectionHelper reflectionHelper) {
    this.ruleFactory = ruleFactory;
    this.reflectionHelper = reflectionHelper;
  }

  @Override
  public JDefinedClass apply(
      String nodeName,
      JsonNode node,
      JsonNode parent,
      JDefinedClass instanceClass,
      Schema currentSchema) {
    return handle(node, instanceClass, currentSchema);
  }

  private JDefinedClass handle(JsonNode node, JDefinedClass instanceClass, Schema currentSchema) {
    // Determine which properties belong to that class (or its
    // superType/parent)
    LinkedHashSet<String> pkProperties = getConstructorProperties(node);
    LinkedHashSet<String> pkCombinedSuperProperties =
        getSuperTypeConstructorPropertiesRecursive(node, currentSchema);

    List<String> pkPropertiesList = new ArrayList<>(pkProperties);
    List<String> pkCombinedSuperPropertiesList = new ArrayList<>(pkCombinedSuperProperties);

    // Only generate the constructors if there are actually properties to
    // put in them
    if (!pkPropertiesList.isEmpty() || !pkCombinedSuperPropertiesList.isEmpty()) {
      // Generate the actual constructor taking in only the pk properties
      addFieldsConstructor(instanceClass, pkPropertiesList, pkCombinedSuperPropertiesList);
    }

    // Return the original class we modified
    return instanceClass;
  }

  private void addFieldsConstructor(
      JDefinedClass instanceClass,
      List<String> classProperties,
      List<String> combinedSuperProperties) {
    GenerationConfig generationConfig = ruleFactory.getGenerationConfig();

    // Generate the constructor with the properties which were located
    JMethod instanceConstructor =
        generateFieldsConstructor(instanceClass, classProperties, combinedSuperProperties);

    // If we're using InnerClassBuilder implementations then we also need
    // to generate those
    if (generationConfig.isGenerateBuilders() && generationConfig.isUseInnerClassBuilders()) {
      JDefinedClass baseBuilderClass =
          ruleFactory.getReflectionHelper().getBaseBuilderClass(instanceClass);
      JDefinedClass concreteBuilderClass =
          ruleFactory.getReflectionHelper().getConcreteBuilderClass(instanceClass);

      generateFieldsBuilderConstructor(
          baseBuilderClass, concreteBuilderClass, instanceClass, instanceConstructor);
    }
  }

  /** Retrieve the list of properties to go in the constructor from node. */
  private LinkedHashSet<String> getConstructorProperties(JsonNode node) {

    if (!node.has("properties")) {
      return new LinkedHashSet<>();
    }

    LinkedHashSet<String> rtn = new LinkedHashSet<>();
    LinkedHashSet<String> draft4PKProperties = new LinkedHashSet<>();

    // setup the set of PK properties for draft4 style PRIMARY_KEYS"
    if (node.has(PRIMARY_KEYS)) {
      JsonNode pkArray = node.get(PRIMARY_KEYS);
      if (pkArray.isArray()) {
        for (JsonNode pkEntry : pkArray) {
          if (pkEntry.isTextual()) {
            draft4PKProperties.add(pkEntry.asText());
          } else {
            throw new IllegalArgumentException(PRIMARY_KEYS + "has to be of an array of string");
          }
        }
      } else {
        throw new IllegalArgumentException(
            PRIMARY_KEYS + " has to " + "be" + " of " + "array type");
      }
    }

    NameHelper nameHelper = ruleFactory.getNameHelper();
    for (String draft4PKProperty : draft4PKProperties) {
      for (Iterator<Map.Entry<String, JsonNode>> properties = node.get("properties").fields();
          properties.hasNext(); ) {
        Map.Entry<String, JsonNode> property = properties.next();
        if (property.getKey().equals(draft4PKProperty)) {
          rtn.add(nameHelper.getPropertyName(property.getKey(), property.getValue()));
          break;
        }
      }
    }
    return rtn;
  }

  /**
   * Recursive, walks the schema tree and assembles a list of all properties of this schema's super
   * schemas
   */
  private LinkedHashSet<String> getSuperTypeConstructorPropertiesRecursive(
      JsonNode node, Schema schema) {
    Schema superTypeSchema = reflectionHelper.getSuperSchema(node, schema, true);

    if (superTypeSchema == null) {
      return new LinkedHashSet<>();
    }

    JsonNode superSchemaNode = superTypeSchema.getContent();

    LinkedHashSet<String> rtn = getConstructorProperties(superSchemaNode);
    rtn.addAll(getSuperTypeConstructorPropertiesRecursive(superSchemaNode, superTypeSchema));

    return rtn;
  }

  private void generateFieldsBuilderConstructor(
      JDefinedClass builderClass,
      JDefinedClass concreteBuilderClass,
      JDefinedClass instanceClass,
      JMethod instanceConstructor) {

    // Locate the instance field since we'll need it to assign a value
    JFieldVar instanceField =
        reflectionHelper.searchClassAndSuperClassesForField("instance", builderClass);

    // Create a new method to be the builder constructor we're defining
    JMethod builderConstructor = builderClass.constructor(JMod.PUBLIC);
    builderConstructor.annotate(SuppressWarnings.class).param("value", "unchecked");
    JBlock constructorBlock = builderConstructor.body();

    // The builder constructor should have the exact same parameters as
    // the instanceConstructor
    for (JVar param : instanceConstructor.params()) {
      builderConstructor.param(param.type(), param.name());
    }

    // Determine if we need to invoke the super() method for our parent
    // builder
    JClass parentClass = builderClass._extends();
    if (!(parentClass.isPrimitive()
        || reflectionHelper.isFinal(parentClass)
        || Objects.equals(parentClass.fullName(), "java.lang.Object"))) {
      constructorBlock.invoke("super");
    }

    // The constructor invocation will also need all the parameters
    // passed through
    JInvocation instanceConstructorInvocation = JExpr._new(instanceClass);
    for (JVar param : instanceConstructor.params()) {
      instanceConstructorInvocation.arg(param);
    }

    // Only initialize the instance if the object being constructed is
    // actually this class
    // if it's a subtype then ignore the instance initialization since
    // the subclass will initialize
    // it
    constructorBlock.directStatement("// Skip initialization when called " + "from subclass");

    JInvocation comparison =
        JExpr._this().invoke("getClass").invoke("equals").arg(JExpr.dotclass(concreteBuilderClass));
    JConditional ifNotSubclass = constructorBlock._if(comparison);
    ifNotSubclass
        ._then()
        .assign(
            JExpr._this().ref(instanceField),
            JExpr.cast(instanceField.type(), instanceConstructorInvocation));

    generateFieldsConcreteBuilderConstructor(
        builderClass, concreteBuilderClass, instanceConstructor);
  }

  private void generateFieldsConcreteBuilderConstructor(
      JDefinedClass baseBuilderClass, JDefinedClass builderClass, JMethod instanceConstructor) {

    // Create Typed Builder Constructor
    JMethod builderConstructor = builderClass.constructor(JMod.PUBLIC);
    JBlock builderConstructorBlock = builderConstructor.body();

    // The typed builder constructor should have the exact same
    // parameters as the inheritable
    // builder.
    for (JVar param : instanceConstructor.params()) {
      builderConstructor.param(param.type(), param.name());
    }

    if (!(baseBuilderClass.isPrimitive()
        || reflectionHelper.isFinal(baseBuilderClass)
        || Objects.equals(baseBuilderClass.fullName(), "java.lang" + ".Object"))) {
      JInvocation superMethod = builderConstructorBlock.invoke("super");

      for (JVar param : builderConstructor.params()) {
        superMethod.arg(param);
      }
    }
  }

  private JMethod generateFieldsConstructor(
      JDefinedClass jclass, List<String> classProperties, List<String> combinedSuperProperties) {
    // add the public constructor with property parameters
    JMethod fieldsConstructor = jclass.constructor(JMod.PUBLIC);

    GenerationConfig generationConfig = ruleFactory.getGenerationConfig();

    JAnnotationArrayMember constructorPropertiesAnnotation;

    if (generationConfig.isIncludeConstructorPropertiesAnnotation()) {
      constructorPropertiesAnnotation =
          fieldsConstructor.annotate(ConstructorProperties.class).paramArray("value");
    } else {
      constructorPropertiesAnnotation = null;
    }

    JBlock constructorBody = fieldsConstructor.body();
    JInvocation superInvocation = constructorBody.invoke("super");

    Map<String, JFieldVar> fields = jclass.fields();
    Map<String, JVar> classFieldParams = new HashMap<>();

    for (String property : classProperties) {
      JFieldVar field = fields.get(property);

      if (field == null) {
        throw new IllegalStateException(
            "Property "
                + property
                + " hasn't been added to JDefinedClass before"
                + " calling addConstructors");
      }

      fieldsConstructor.javadoc().addParam(property);
      if (generationConfig.isIncludeConstructorPropertiesAnnotation()
          && constructorPropertiesAnnotation != null) {
        constructorPropertiesAnnotation.param(field.name());
      }

      JVar param = fieldsConstructor.param(field.type(), field.name());
      constructorBody.assign(JExpr._this().ref(field), param);
      classFieldParams.put(property, param);
    }

    List<JVar> superConstructorParams = new ArrayList<>();

    for (String property : combinedSuperProperties) {
      JFieldVar field = reflectionHelper.searchSuperClassesForField(property, jclass);

      if (field == null) {
        throw new IllegalStateException(
            "Property "
                + property
                + " hasn't been added to JDefinedClass before"
                + " calling addConstructors");
      }

      JVar param = classFieldParams.get(property);

      if (param == null) {
        param = fieldsConstructor.param(field.type(), field.name());
      }

      fieldsConstructor.javadoc().addParam(property);
      if (generationConfig.isIncludeConstructorPropertiesAnnotation()
          && constructorPropertiesAnnotation != null) {
        constructorPropertiesAnnotation.param(param.name());
      }

      superConstructorParams.add(param);
    }

    for (JVar param : superConstructorParams) {
      superInvocation.arg(param);
    }

    return fieldsConstructor;
  }
}
