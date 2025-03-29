package org.json.engine;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.JsonPath;
import org.json.VariableCache;

import java.util.List;
import java.util.Map;

import static org.json.utils.DataTypeResolver.*;

class HardCodedTransformationEngine implements TransformationEngine {
    @Override
    public void process(Map<String, Object> xform, String inputJson, ObjectNode outputJson) throws Exception {
        String outputPath = (String) xform.get("output");

        Object value = null;

        // First, check if "value" is provided
        if (xform.containsKey("value")) {
            value = xform.get("value");

            // Check if value is a variable (e.g., "#discountRate")
            if (value instanceof String && ((String) value).startsWith("#")) {
                value = VariableCache.getVariable((String) value);
                if (value == null) {
                    System.out.println("Warning: Variable " + xform.get("value") + " not found!");
                    return;
                }
            }
        }

        // Handle case where no value is found
        if (value == null) {
            System.out.println("Warning: No hardcoded value or stored variable found for " + outputPath);
            return;
        }

        // Determine data types dynamically if not explicitly provided
        String inputDataType = xform.containsKey("inputDataType") ? (String) xform.get("inputDataType") : determineDataType(value);
        String outputDataType = xform.containsKey("outputDataType") ? (String) xform.get("outputDataType") : inputDataType;

        String inputFormat = (String) xform.get("inputDataFormat");
        String outputFormat = (String) xform.get("outputDataFormat");

        //Convert the value based on input and output type
        value = convertDataType(value, inputDataType, outputDataType, inputFormat, outputFormat);

        // Store the transformed value in the output JSON
        setJsonValue(outputJson, outputPath, value, outputDataType);
    }


}
