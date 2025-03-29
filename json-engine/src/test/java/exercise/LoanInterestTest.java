package exercise;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.json.engine.JsonTransformer.transformJson;
import static org.junit.jupiter.api.Assertions.*;

public class LoanInterestTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private String schemaJson;
    private String inputJson;

    @BeforeEach
    void setUp() {
        schemaJson = """
                {
                   "forms": [
                     {
                       "name": "setLowInterestRate",
                       "engine": "hard-coded",
                       "output": "#lowInterestRate",
                       "value": 0.05
                     },
                     {
                       "name": "setMediumInterestRate",
                       "engine": "hard-coded",
                       "output": "#mediumInterestRate",
                       "value": 0.07
                     },
                     {
                       "name": "setHighInterestRate",
                       "engine": "hard-coded",
                       "output": "#highInterestRate",
                       "value": 0.10
                     },
                     {
                       "name": "determineInterestRate",
                       "engine": "conditional-operation",
                       "output": "#interestRate",
                       "input": ["$.loan.loanTerm"],
                       "criteria": [
                         { "condition": "$1 > 10", "outcome": "#highInterestRate" },
                         { "condition": "$1 >= 6", "outcome": "#mediumInterestRate" }
                       ]
                     },
                     {
                       "name": "calculateInterestAmount",
                       "engine": "math-operation",
                       "output": "/interestAmount",
                       "input": ["$.loan.loanAmount", "#interestRate"],
                       "operation": "multiply"
                     }
                   ]
                 }
                """;

        inputJson = """
                {
                   "loan": {
                      "loanAmount": 100000,
                      "loanTerm": 7
                   }
                }
                """;
    }

    @Test
    void testLoanInterestCalculation() throws Exception {
        ObjectNode result = transformJson(schemaJson, inputJson);

        System.out.println(result.toPrettyString());

        assertNotNull(result);
        assertTrue(result.has("interestAmount"));
        assertEquals(7000, result.get("interestAmount").asInt());
    }
}
