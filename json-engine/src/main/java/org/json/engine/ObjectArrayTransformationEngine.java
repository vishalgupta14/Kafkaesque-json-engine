package org.json.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.JsonPath;
import org.json.VariableCache;
import java.util.List;
import java.util.Map;
import static org.json.utils.DataTypeResolver.setJsonValue;

class ObjectArrayTransformationEngine implements TransformationEngine {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void process(Map<String, Object> xform, String inputJson, ObjectNode outputJson) throws Exception {
        String outputPath = (String) xform.get("output");
        List<String> inputPaths = (List<String>) xform.get("input");
        List<Map<String, Object>> schemaList = (List<Map<String, Object>>) xform.get("schema");

        // Determine whether the output should be an object or an array
        boolean isObject = "object".equals(xform.get("outputDataType"));

        ArrayNode arrayNode = objectMapper.createArrayNode();
        ObjectNode firstObjectNode = objectMapper.createObjectNode();

        for (String inputPath : inputPaths) {
            Object objects;

            if (inputPath.isEmpty()) {
                objects = objectMapper.readTree(inputJson);  // Fetch entire JSON if empty input
            } else if (inputPath.startsWith("$.")) {
                try {
                    objects = JsonPath.read(inputJson, inputPath);  // Fetch value from JSON
                } catch (Exception e) {
                    System.out.println("Warning: Path not found -> " + inputPath);
                    continue;
                }
            } else if (inputPath.startsWith("#")) {
                objects = VariableCache.getVariable(inputPath);  // Retrieve variable
                if (objects == null) {
                    System.out.println("Warning: Variable " + inputPath + " not found!");
                    continue;
                }
            } else {
                objects = inputPath;  // Direct content (string, integer, boolean, etc.)
            }

            // Ensure objects is an array (wrap if necessary)
            List<Object> objectList = (objects instanceof List) ? (List<Object>) objects : List.of(objects);

            for (Object obj : objectList) {
                ObjectNode transformedNode = objectMapper.createObjectNode();
                String objJson = objectMapper.writeValueAsString(obj);

                for (Map<String, Object> schemaEntry : schemaList) {
                    JsonTransformer.processTransformation(schemaEntry, objJson, transformedNode);
                }

                arrayNode.add(transformedNode);
            }
        }

        // Convert array to object if required
        if (isObject) {
            if (!arrayNode.isEmpty()) {
                int index = xform.containsKey("index") ? (int) xform.get("index") : 0;
                index = Math.min(index, arrayNode.size() - 1);
                firstObjectNode = (ObjectNode) arrayNode.get(index);
            }
            setJsonValue(outputJson, outputPath, firstObjectNode, "object");
        } else {
            setJsonValue(outputJson, outputPath, arrayNode, "array");
        }
    }
}
