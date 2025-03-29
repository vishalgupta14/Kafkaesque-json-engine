package org.json.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.VariableCache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonTransformer {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Map<String, TransformationEngine> engineMap = new HashMap<>();

    static {
        engineMap.put("object-array", new ObjectArrayTransformationEngine());
        engineMap.put("straight-pull", new StraightPullTransformationEngine());
        engineMap.put("hard-coded", new HardCodedTransformationEngine());
        engineMap.put("string-operation", new StringFunctionTransformationEngine());
        engineMap.put("math-operation", new MathOperationTransformationEngine());
        engineMap.put("conditional-operation", new ConditionalTransformationEngine());
    }

    public static ObjectNode transformJson(String schemaJson, String inputJson) throws Exception {
        ObjectNode outputJson = objectMapper.createObjectNode();
        Map<String, Object> schema = objectMapper.readValue(schemaJson, Map.class);
        List<Map<String, Object>> forms = (List<Map<String, Object>>) schema.get("forms");

        for (Map<String, Object> xform : forms) {
            processTransformation(xform, inputJson, outputJson);
        }
        VariableCache.clearAllVariables();
        return outputJson;
    }

    public static void processTransformation(Map<String, Object> xform, String inputJson, ObjectNode outputJson) throws Exception {
        String xformEngine = (String) xform.get("engine");
        TransformationEngine engine = engineMap.get(xformEngine);

        if (engine != null) {
            engine.process(xform, inputJson, outputJson);
        } else {
            System.out.println("Warning: Unknown transformation engine -> " + xformEngine);
        }
    }
}