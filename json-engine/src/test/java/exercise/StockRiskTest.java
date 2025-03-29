package exercise;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.json.engine.JsonTransformer.transformJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StockRiskTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private String schemaJson;
    private String inputJson;

    @BeforeEach
    void setUp() {
        schemaJson = """
                {
                   "forms": [
                     {
                       "name": "riskLevel",
                       "engine": "conditional-operation",
                       "output": "/riskAssessment",
                       "input": ["$.portfolio.totalValue", "$.portfolio.volatileStocks"],
                       "criteria": [
                         { "condition": "$1 > 1000000 && $2 >= 30", "outcome": "High Risk" },
                         { "condition": "$1 >= 500000 && $2 >= 15", "outcome": "Moderate Risk" }
                       ]
                     }
                   ]
                 }
                """;

        inputJson = """
                {
                   "portfolio": {
                      "totalValue": 1200000,
                      "volatileStocks": 25
                   }
                }
                """;
    }

    @Test
    void testStockRiskAssessment() throws Exception {
        ObjectNode result = transformJson(schemaJson, inputJson);

        System.out.println(result.toPrettyString());

        assertNotNull(result);
        assertTrue(result.has("riskAssessment"));
        assertEquals("Moderate Risk", result.get("riskAssessment").asText());
    }
}
