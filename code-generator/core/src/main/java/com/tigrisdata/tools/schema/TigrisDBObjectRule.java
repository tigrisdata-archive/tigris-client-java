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
package com.tigrisdata.tools.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;
import org.jsonschema2pojo.Schema;
import org.jsonschema2pojo.rules.Rule;
import org.jsonschema2pojo.rules.RuleFactory;
import org.jsonschema2pojo.util.ParcelableHelper;
import org.jsonschema2pojo.util.ReflectionHelper;

/** This is to customize the type of the generated model */
public class TigrisDBObjectRule extends org.jsonschema2pojo.rules.ObjectRule {

  protected TigrisDBObjectRule(
      RuleFactory ruleFactory,
      ParcelableHelper parcelableHelper,
      ReflectionHelper reflectionHelper) {
    super(ruleFactory, parcelableHelper, reflectionHelper);
  }

  /**
   * Applies this schema rule to take the required code generation steps.
   *
   * <p>When this rule is applied for schemas of type object, the properties of the schema are used
   * to generate a new Java class and determine its characteristics. See other implementers of
   * {@link Rule} for details.
   */
  @Override
  public JType apply(
      String nodeName, JsonNode node, JsonNode parent, JPackage _package, Schema schema) {
    // TODO: implement the type
    JDefinedClass result = (JDefinedClass) super.apply(nodeName, node, parent, _package, schema);
    result._implements(com.tigrisdata.db.client.model.TigrisCollectionType.class);
    return result;
  }
}
