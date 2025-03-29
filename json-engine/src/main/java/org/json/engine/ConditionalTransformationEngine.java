package org.json.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.JsonPath;
import org.json.VariableCache;
import org.mvel2.MVEL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.json.utils.DataTypeResolver.setJsonValue;
import static org.json.utils.DataTypeResolver.determineDataType;

class ConditionalTransformationEngine implements TransformationEngine {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void process(Map<String, Object> xform, String inputJson, ObjectNode outputJson) throws Exception {
        String outputPath = (String) xform.get("output");
        List<String> inputPaths = (List<String>) xform.get("input");
        List<Map<String, Object>> conditions = (List<Map<String, Object>>) xform.get("criteria");

        if (conditions == null || conditions.isEmpty()) {
            System.out.println("Warning: No criteria provided for conditional transformation.");
            return;
        }

        // Create a context map for MVEL evaluation
        Map<String, Object> context = new HashMap<>();
        populateContext(context, inputJson, inputPaths);

        for (Map<String, Object> condition : conditions) {
            String expression = (String) condition.get("condition");
            Object outcome = condition.get("outcome");

            if (expression == null || outcome == null) {
                System.out.println("Warning: Invalid condition found in criteria.");
                continue;
            }

            // Replace placeholders ($1, $2, etc.) with corresponding input values
            String resolvedExpression = resolveExpression(expression, inputPaths, context);

            // Evaluate the condition using MVEL
            boolean conditionMet = (boolean) MVEL.eval(resolvedExpression, context);

            if (conditionMet) {
                // Resolve the final outcome value
                Object resolvedOutcome = resolveValue(outcome, context);

                // Store the transformed value
                setJsonValue(outputJson, outputPath, resolvedOutcome, determineDataType(resolvedOutcome));
                return; // Stop processing once a condition is met
            }
        }
    }

    /**
     * Populates the context map with JSON path values and variables.
     */
    private void populateContext(Map<String, Object> context, String inputJson, List<String> inputPaths) {
        // Add JSON fields as variables based on positions ($1, $2, etc.)
        for (int i = 0; i < inputPaths.size(); i++) {
            String inputPath = inputPaths.get(i);
            String key = "$" + (i + 1);

            try {
                Object value;
                if (inputPath.startsWith("#")) {
                    // Fetch from VariableCache
                    value = VariableCache.getVariable(inputPath);
                } else if (inputPath.startsWith("$.")) {
                    // Fetch from JSON Path
                    value = JsonPath.read(inputJson, inputPath);
                } else {
                    // Direct value
                    value = inputPath;
                }
                context.put(key, value);
            } catch (Exception e) {
                System.out.println("Warning: Unable to resolve input path -> " + inputPath);
                context.put(key, null);
            }
        }

        // Add all stored variables in VariableCache
        context.putAll(VariableCache.getAllVariables());
    }

    /**
     * Replaces placeholders ($1, $2, etc.) with actual values from the context.
     */
    private String resolveExpression(String expression, List<String> inputPaths, Map<String, Object> context) {
        for (int i = 0; i < inputPaths.size(); i++) {
            String placeholder = "$" + (i + 1);
            Object value = context.get(placeholder);
            String replacement = (value instanceof String) ? "\"" + value + "\"" : String.valueOf(value);
            expression = expression.replace(placeholder, replacement);
        }
        return expression;
    }

    /**
     * Resolves outcome values (can be a JSON path, variable, or a direct value).
     */
    private Object resolveValue(Object value, Map<String, Object> context) {
        if (value instanceof String) {
            String strValue = (String) value;
            if (context.containsKey(strValue)) {
                return context.get(strValue);
            }
        }
        return value; // Return as-is for direct values
    }
}
