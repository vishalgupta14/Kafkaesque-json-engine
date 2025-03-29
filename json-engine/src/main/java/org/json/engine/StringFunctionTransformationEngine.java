package org.json.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.JsonPath;
import org.json.VariableCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.json.utils.DataTypeResolver.setJsonValue;
import static org.json.utils.StringFunctionResolver.applyStringFunction;

class StringFunctionTransformationEngine implements TransformationEngine {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void process(Map<String, Object> xform, String inputJson, ObjectNode outputJson) throws Exception {
        String outputPath = (String) xform.get("output");
        List<String> inputPaths = (List<String>) xform.get("input");
        String function = (String) xform.get("function");

        List<String> values = new ArrayList<>();

        for (String inputPath : inputPaths) {
            Object value;

            // Determine the type: JSON Path (`$.`), Variable (`#`), or direct content
            if (inputPath.startsWith("$.") && !inputPath.startsWith("#")) {
                try {
                    value = JsonPath.read(inputJson, inputPath);
                } catch (Exception e) {
                    System.out.println("Warning: Path not found -> " + inputPath);
                    continue;
                }
            } else if (inputPath.startsWith("#")) {
                value = VariableCache.getVariable(inputPath);
                if (value == null) {
                    System.out.println("Warning: Variable " + inputPath + " not found!");
                    continue;
                }
            } else {
                value = inputPath; // Direct string input
            }

            // If value is an array, extract individual elements
            if (value instanceof List<?>) {
                for (Object item : (List<?>) value) {
                    if (item instanceof String) {
                        values.add((String) item);
                    }
                }
            } else if (value instanceof String) {
                values.add((String) value);
            }
        }

        if (!values.isEmpty()) {
            Object transformedValue = applyStringFunction(values, function, xform,inputJson);
            setJsonValue(outputJson, outputPath, transformedValue, "string");
        }
    }
}
