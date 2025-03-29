package org.json.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.JsonPath;
import org.json.VariableCache;
import java.util.List;
import java.util.Map;
import static org.json.utils.DataTypeResolver.convertDataType;
import static org.json.utils.DataTypeResolver.determineDataType;
import static org.json.utils.DataTypeResolver.setJsonValue;

class StraightPullTransformationEngine implements TransformationEngine {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void process(Map<String, Object> xform, String inputJson, ObjectNode outputJson) throws Exception {
        String outputPath = (String) xform.get("output");
        List<String> inputPaths = (List<String>) xform.get("input");

        for (String inputPath : inputPaths) {
            Object value;

            if (inputPath.isEmpty()) {
                value = objectMapper.readTree(inputJson);
            } else if (inputPath.startsWith("$.")) {
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

            if (value != null) {
                // Determine input/output data types
                String inputDataType = xform.containsKey("inputDataType") ? (String) xform.get("inputDataType") : determineDataType(value);
                String outputDataType = xform.containsKey("outputDataType") ? (String) xform.get("outputDataType") : inputDataType;

                String inputFormat = (String) xform.get("inputDataFormat");
                String outputFormat = (String) xform.get("outputDataFormat");

                // Convert data type
                value = convertDataType(value, inputDataType, outputDataType, inputFormat, outputFormat);

                // Store value in the output JSON
                setJsonValue(outputJson, outputPath, value, outputDataType);
            }
        }
    }
}
