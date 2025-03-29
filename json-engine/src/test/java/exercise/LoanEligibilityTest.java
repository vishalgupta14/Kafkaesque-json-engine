package exercise;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.json.engine.JsonTransformer.transformJson;
import static org.junit.jupiter.api.Assertions.*;

public class LoanEligibilityTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private String schemaJson;
    private String inputJson;

    @BeforeEach
    void setUp() {
        schemaJson = """
                {
                   "forms": [
                     {
                       "name": "calculateDTI",
                       "engine": "math-operation",
                       "output": "#debtToIncomeRatio",
                       "input": ["$.applicant.monthlyDebt", "$.applicant.monthlyIncome"],
                       "operation": "divide"
                     },
                     {
                       "name": "loanEligibility",
                       "engine": "conditional-operation",
                       "output": "/loanApproval",
                       "input": ["$.applicant.creditScore", "#debtToIncomeRatio"],
                       "criteria": [
                         { "condition": "$1 >= 750 && $2 <= 0.40", "outcome": "Approved" },
                         { "condition": "$1 >= 700 && $2 <= 0.50", "outcome": "Approved with Conditions" }
                       ],
                       "else": "Rejected"
                     }
                   ]
                 }
                """;

        inputJson = """
                {
                   "applicant": {
                      "creditScore": 720,
                      "monthlyIncome": 10000,
                      "monthlyDebt": 4000
                   }
                }
                """;
    }

    @Test
    void testLoanEligibility() throws Exception {
        ObjectNode result = transformJson(schemaJson, inputJson);

        System.out.println(result.toPrettyString());

        assertNotNull(result);
        assertTrue(result.has("loanApproval"));
        assertEquals("Approved with Conditions", result.get("loanApproval").asText());
    }
}
