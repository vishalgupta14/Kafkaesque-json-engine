package exercise;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.json.engine.JsonTransformer.transformJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CurrencyArbitrageTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private String schemaJson;
    private String inputJson;

    @BeforeEach
    void setUp() {
        schemaJson = """
                {
                   "forms": [
                     {
                       "name": "convertUSDToEUR",
                       "engine": "math-operation",
                       "output": "#amountInEUR",
                       "input": ["1000", "$.exchangeRates.usdToEur"],
                       "operation": "multiply"
                     },
                     {
                       "name": "convertEURToGBP",
                       "engine": "math-operation",
                       "output": "#amountInGBP",
                       "input": ["#amountInEUR", "$.exchangeRates.eurToGbp"],
                       "operation": "multiply"
                     },
                     {
                       "name": "arbitrageProfit",
                       "engine": "conditional-operation",
                       "output": "/arbitrageResult",
                       "input": ["#amountInGBP"],
                       "criteria": [{ "condition": "$1 > 650", "outcome": "Profitable Arbitrage" },
                       {
                           "condition": "true",
                           "outcome": "Not Profitable"
                         }
                       ]
                     }
                   ]
                 }
                """;

        inputJson = """
                {
                   "exchangeRates": {
                      "usdToEur": 0.85,
                      "eurToGbp": 0.78
                   }
                }
                """;
    }

    @Test
    void testCurrencyArbitrage() throws Exception {
        ObjectNode result = transformJson(schemaJson, inputJson);

        System.out.println(result.toPrettyString());

        assertNotNull(result);
    }
}
