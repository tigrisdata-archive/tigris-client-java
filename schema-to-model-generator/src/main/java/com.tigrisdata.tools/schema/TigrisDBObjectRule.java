package com.tigrisdata.tools.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;
import org.jsonschema2pojo.Schema;
import org.jsonschema2pojo.rules.Rule;
import org.jsonschema2pojo.rules.RuleFactory;
import org.jsonschema2pojo.util.ParcelableHelper;
import org.jsonschema2pojo.util.ReflectionHelper;

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
     * <p>When this rule is applied for schemas of type object, the properties
     * of the schema are used
     * to generate a new Java class and determine its characteristics. See
     * other implementers of
     * {@link Rule} for details.
     */
    @Override
    public JType apply(
            String nodeName, JsonNode node, JsonNode parent,
            JPackage _package, Schema schema) {
        // TODO: implement the type
        //result._implements()
        return super.apply(nodeName, node, parent, _package,
                schema);

    }
}
