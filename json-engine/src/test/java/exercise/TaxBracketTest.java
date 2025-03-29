package exercise;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.json.engine.JsonTransformer.transformJson;
import static org.junit.jupiter.api.Assertions.*;

public class TaxBracketTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private String schemaJson;
    private String inputJson;

    @BeforeEach
    void setUp() {
        schemaJson = """
                {
                   "forms": [
                     {
                       "name": "setTaxRateLow",
                       "engine": "hard-coded",
                       "output": "#lowTaxRate",
                       "value": 0.10
                     },
                     {
                       "name": "setTaxRateMedium",
                       "engine": "hard-coded",
                       "output": "#mediumTaxRate",
                       "value": 0.20
                     },
                     {
                       "name": "setTaxRateHigh",
                       "engine": "hard-coded",
                       "output": "#highTaxRate",
                       "value": 0.30
                     },
                     {
                       "name": "determineTaxRate",
                       "engine": "conditional-operation",
                       "output": "#taxRate",
                       "input": ["$.person.income"],
                       "criteria": [
                         { "condition": "$1 >= 500000", "outcome": "#highTaxRate" },
                         { "condition": "$1 >= 200000", "outcome": "#mediumTaxRate" }
                       ],
                       "else": "#lowTaxRate"
                     },
                     {
                       "name": "calculateTaxAmount",
                       "engine": "math-operation",
                       "output": "/taxAmount",
                       "input": ["$.person.income", "#taxRate"],
                       "operation": "multiply"
                     }
                   ]
                 }
                """;

        inputJson = """
                {
                   "person": {
                      "income": 250000
                   }
                }
                """;
    }

    @Test
    void testTaxBracket() throws Exception {
        ObjectNode result = transformJson(schemaJson, inputJson);

        System.out.println(result.toPrettyString());

        assertNotNull(result);
        assertTrue(result.has("taxAmount"));
        assertEquals(50000, result.get("taxAmount").asInt());
    }
}
