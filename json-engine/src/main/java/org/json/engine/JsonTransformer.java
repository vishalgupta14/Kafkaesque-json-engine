package org.json.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.VariableCache;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

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
        Map<String, Object> schema = objectMapper.readValue(schemaJson, LinkedHashMap.class);
        List<Map<String, Object>> forms = (List<Map<String, Object>>) schema.get("forms");

        List<Map<String, Object>> orderedForms = new ArrayList<>();
        ConcurrentLinkedQueue<Map<String, Object>> parallelQueue = new ConcurrentLinkedQueue<>();

        // Phase 1: Run hard-coded variable producers first (to resolve variable dependencies)
        forms.forEach(form -> {
            String output = (String) form.get("output");
            String engine = (String) form.get("engine");
            if (output != null && output.startsWith("#") && "hard-coded".equals(engine)) {
                parallelQueue.add(form);
            } else {
                orderedForms.add(form);
            }
        });

        // Execute Phase 1 transformations in parallel (safely)
        parallelQueue.parallelStream().forEach(form -> {
            synchronized (VariableCache.class) {
                try {
                    processTransformation(form, inputJson, outputJson);
                } catch (Exception e) {
                    throw new RuntimeException("Error in Phase 1 transformation", e);
                }
            }
        });

        // Phase 2: Process the rest in the original order
        for (Map<String, Object> form : orderedForms) {
            processTransformation(form, inputJson, outputJson);
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
            System.out.println("[WARN] Unknown transformation engine: " + xformEngine);
        }
    }
}
