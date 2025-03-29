package org.json.engine;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.JsonPath;
import org.json.VariableCache;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.json.utils.DataTypeResolver.*;

class MathOperationTransformationEngine implements TransformationEngine {
    @Override
    public void process(Map<String, Object> xform, String inputJson, ObjectNode outputJson) throws Exception {
        String outputPath = (String) xform.get("output");
        List<String> inputPaths = (List<String>) xform.get("input");
        String operation = (String) xform.get("operation");

        if (operation == null || operation.isEmpty()) {
            System.out.println("Warning: No operation specified for math-operation engine.");
            return;
        }

        BigDecimal result = null;
        String outputDataType = xform.getOrDefault("outputDataType", "bigDecimal").toString();

        for (String inputPath : inputPaths) {
            Object value = null;

            // Determine source: JSON path (`$.`), Variable (`#`), or direct content
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
                value = inputPath; // Direct content (string, integer, boolean, etc.)
            }

            BigDecimal num = convertToBigDecimal(value);
            if (num == null) {
                System.out.println("Warning: Non-numeric value encountered in math-operation engine. Skipping...");
                continue;
            }

            // Perform the operation
            if (result == null) {
                result = num; // Initialize first value
            } else {
                switch (operation) {
                    case "add":
                        result = result.add(num);
                        break;
                    case "subtract":
                        result = result.subtract(num);
                        break;
                    case "multiply":
                        result = result.multiply(num);
                        break;
                    case "divide":
                        if (num.equals(BigDecimal.ZERO)) {
                            System.out.println("Warning: Division by zero encountered.");
                            result = BigDecimal.valueOf(Double.POSITIVE_INFINITY); // Handle Infinity
                        } else {
                            result = result.divide(num, 10, BigDecimal.ROUND_HALF_UP);
                        }
                        break;
                    case "mod":
                        result = result.remainder(num);
                        break;
                    case "power":
                        result = result.pow(num.intValue());
                        break;
                    case "min":
                        result = result.min(num);
                        break;
                    case "max":
                        result = result.max(num);
                        break;
                    default:
                        System.out.println("Warning: Unsupported math operation -> " + operation);
                        return;
                }
            }
        }

        if (result == null) {
            System.out.println("Warning: No valid numbers found for math operation.");
            return;
        }

        Object finalResult = convertToOutputType(result, outputDataType);
        setJsonValue(outputJson, outputPath, finalResult, outputDataType);
    }
}
