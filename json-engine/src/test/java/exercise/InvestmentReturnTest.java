package exercise;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.json.engine.JsonTransformer.transformJson;
import static org.junit.jupiter.api.Assertions.*;

public class InvestmentReturnTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private String schemaJson;
    private String inputJson;

    @BeforeEach
    void setUp() {
        schemaJson = """
                {
                   "forms": [
                     {
                       "name": "setDefaultReturnRate",
                       "engine": "hard-coded",
                       "output": "#defaultReturnRate",
                       "value": 0.05
                     },
                     {
                       "name": "setLongTermReturnRate",
                       "engine": "hard-coded",
                       "output": "#longTermReturnRate",
                       "value": 0.08
                     },
                     {
                       "name": "setVipReturnRate",
                       "engine": "hard-coded",
                       "output": "#vipReturnRate",
                       "value": 0.12
                     },
                     {
                       "name": "determineReturnRate",
                       "engine": "conditional-operation",
                       "output": "#returnRate",
                       "input": ["$.investor.type", "$.investment.years"],
                       "criteria": [
                         { "condition": "$1 == 'VIP'", "outcome": "#vipReturnRate" },
                         { "condition": "$2 >= 10", "outcome": "#longTermReturnRate" }
                       ]
                     },
                     {
                       "name": "calculateTotalReturn",
                       "engine": "math-operation",
                       "output": "/totalReturn",
                       "input": ["$.investment.amount", "#returnRate", "$.investment.years"],
                       "operation": "multiply"
                     }
                   ]
                 }
                """;

        inputJson = """
                {
                   "investor": {
                      "type": "VIP"
                   },
                   "investment": {
                      "amount": 50000,
                      "years": 5
                   }
                }
                """;
    }

    @Test
    void testInvestmentReturn() throws Exception {
        ObjectNode result = transformJson(schemaJson, inputJson);

        System.out.println(result.toPrettyString());

        assertNotNull(result);
        assertTrue(result.has("totalReturn"));
        assertEquals(30000, result.get("totalReturn").asInt());
    }
}
