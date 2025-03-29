package schemas;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.json.engine.JsonTransformer.transformJson;
import static org.junit.jupiter.api.Assertions.*;

public class ClientAFinanceEventPayloadTransformTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private String schemaJson;
    private String inputJson;

    @BeforeEach
    void setUp() {
        schemaJson = """
            {
              "clientId": "client-a",
              "version": "1.0.0",
              "description": "Schema for transforming FinanceEventPayload using string, math, conditional, and datatype operations.",
              "forms": [
                {
                  "name": "jsonStart",
                  "engine": "hard-coded",
                  "output": "#jsonStart",
                  "value": 0
                },
                {
                  "name": "jsonEnd",
                  "engine": "hard-coded",
                  "output": "#jsonEnd",
                  "value": 4
                },
                {
                  "name": "toUpperCase_userName",
                  "engine": "string-operation",
                  "output": "/userNameUpper",
                  "input": ["$.userName"],
                  "function": "toUpperCase"
                },
                {
                  "name": "toLowerCase_email",
                  "engine": "string-operation",
                  "output": "/emailLower",
                  "input": ["$.email"],
                  "function": "toLowerCase"
                },
                {
                  "name": "substring_description",
                  "engine": "string-operation",
                  "output": "/descPart",
                  "input": ["$.description"],
                  "function": "substring",
                  "start": "#jsonStart",
                  "end": "#jsonEnd"
                },
                {
                  "name": "replace_currency",
                  "engine": "string-operation",
                  "output": "/currencyUpdated",
                  "input": ["$.currency"],
                  "function": "replace",
                  "target": "USD",
                  "replacement": "INR"
                },
                {
                  "name": "equals_transactionType",
                  "engine": "string-operation",
                  "output": "/isDeposit",
                  "input": ["$.transactionType"],
                  "function": "equals",
                  "compareTo": "deposit"
                },
                {
                  "name": "notEquals_accountType",
                  "engine": "string-operation",
                  "output": "/isNotSavings",
                  "input": ["$.accountType"],
                  "function": "notEquals",
                  "compareTo": "savings"
                },
                {
                  "name": "length_description",
                  "engine": "string-operation",
                  "output": "/descLength",
                  "input": ["$.description"],
                  "function": "length"
                },
                {
                  "name": "indexOf_userName",
                  "engine": "string-operation",
                  "output": "/indexJson",
                  "input": ["$.userName"],
                  "function": "indexOf",
                  "search": "json"
                },
                {
                  "name": "regexReplace_email",
                  "engine": "string-operation",
                  "output": "/emailSanitized",
                  "input": ["$.email"],
                  "function": "regexReplace",
                  "regex": "@.*",
                  "replacement": "@domain.com"
                },
                {
                  "name": "removeWhitespace_description",
                  "engine": "string-operation",
                  "output": "/descNoSpace",
                  "input": ["$.description"],
                  "function": "removeWhitespace"
                },
                {
                  "name": "add_amount_fee",
                  "engine": "math-operation",
                  "output": "/amountPlusFee",
                  "input": ["$.amount", "$.fee"],
                  "operation": "add",
                  "convert": "bigDecimal"
                },
                {
                  "name": "subtract_amount_tax",
                  "engine": "math-operation",
                  "output": "/amountMinusTax",
                  "input": ["$.amount", "$.tax"],
                  "operation": "subtract",
                  "convert": "bigDecimal"
                },
                {
                  "name": "multiply_fee_tax",
                  "engine": "math-operation",
                  "output": "/feeTimesTax",
                  "input": ["$.fee", "$.tax"],
                  "operation": "multiply",
                  "convert": "bigDecimal"
                },
                {
                  "name": "divide_amount_fee",
                  "engine": "math-operation",
                  "output": "/amountDividedByFee",
                  "input": ["$.amount", "$.fee"],
                  "operation": "divide",
                  "convert": "bigDecimal"
                },
                {
                  "name": "conditional_region_bonus",
                  "engine": "conditional-operation",
                  "output": "/regionBonus",
                  "input": ["$.region"],
                  "criteria": [
                    { "condition": "$1 == 'north-america'", "outcome": 5 },
                    { "condition": "$1 == 'europe'", "outcome": 10 },
                    { "condition": "true", "outcome": 2 }
                  ]
                },
                {
                  "name": "timestampToISO_conversion",
                  "engine": "straight-pull",
                  "output": "/timestampISO",
                  "input": ["$.timestamp"],
                  "inputDataType": "timestamp",
                  "outputDataType": "ISODate"
                },
                {
                  "name": "hardcoded_status",
                  "engine": "hard-coded",
                  "output": "/status",
                  "value": "processed"
                },
                {
                  "name": "booleanFromAmount",
                  "engine": "conditional-operation",
                  "output": "/isHighValue",
                  "input": ["$.amount"],
                  "criteria": [
                    { "condition": "$1 > 5000", "outcome": true },
                    { "condition": "true", "outcome": false }
                  ]
                }
              ]
            }
        """;

        inputJson = """
            {
              "userName": "jsonUser",
              "email": "jsonUser@example.com",
              "description": "Finance transaction test string",
              "currency": "USD",
              "transactionType": "deposit",
              "accountType": "current",
              "amount": 10000,
              "fee": 25.5,
              "tax": 15.25,
              "region": "europe",
              "timestamp": 1741000000000
            }
        """;
    }

    @Test
    void testFinancePayloadTransformation() throws Exception {
        ObjectNode result = transformJson(schemaJson, inputJson);

        System.out.println(result.toPrettyString());

        assertNotNull(result);
        assertTrue(result.has("userNameUpper"));
        assertEquals("JSONUSER", result.get("userNameUpper").asText());
    }
}
