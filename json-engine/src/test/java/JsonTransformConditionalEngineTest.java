import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.json.engine.JsonTransformer.transformJson;
import static org.junit.jupiter.api.Assertions.*;

public class JsonTransformConditionalEngineTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private String schemaJson;
    private String inputJson;

    @BeforeEach
    void setUp() {
        schemaJson = """
                {
                   "forms": [
                     {
                       "name": "hardcodedDefaultDiscount",
                       "engine": "hard-coded",
                       "output": "#defaultDiscount",
                       "value": 5
                     },
                     {
                       "name": "hardcodedVIPDiscount",
                       "engine": "hard-coded",
                       "output": "#vipDiscount",
                       "value": 15
                     },
                     {
                       "name": "applyDiscount",
                       "engine": "conditional-operation",
                       "output": "/finalDiscount",
                       "input": ["$.customerType", "$.orderAmount"],
                       "criteria": [
                         {
                           "condition": "$1 == 'VIP' && $2 > 100",
                           "outcome": "#vipDiscount"
                         },
                         {
                           "condition": "$2 > 500",
                           "outcome": 10
                         },
                         {
                           "condition": "true",
                           "outcome": "#defaultDiscount"
                         }
                       ]
                     }
                   ]
                 }
                """;

        inputJson = """
                {
                   "customerType": "VIP",
                   "orderAmount": 120
                 }
                """;
    }

    @Test
    void testJsonTransformation() throws Exception {
        // Execute transformation
        ObjectNode result = transformJson(schemaJson, inputJson);

        // Print result for debugging
        System.out.println(result.toPrettyString());

        // Assertions to verify transformation
        assertNotNull(result);
        assertTrue(result.has("finalDiscount"));
        assertEquals(15, result.get("finalDiscount").asInt());
    }
}
