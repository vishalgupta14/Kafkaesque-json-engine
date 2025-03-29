import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.json.engine.JsonTransformer.transformJson;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigInteger;

public class JsonTransformEngineMathOperationsTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private String schemaJson;
    private String inputJson;

    @BeforeEach
    void setUp() {
        schemaJson = """
                {
                    "forms": [
                      {
                        "name": "addition",
                        "engine": "math-operation",
                        "output": "/sumResult",
                        "input": ["$.num1", "$.num2"],
                        "operation": "add",
                        "convert": "bigDecimal"
                      },
                      {
                        "name": "subtraction",
                        "engine": "math-operation",
                        "output": "/subResult",
                        "input": ["$.num1", "$.num2"],
                        "operation": "subtract",
                        "convert": "bigDecimal"
                      },
                      {
                        "name": "multiplication",
                        "engine": "math-operation",
                        "output": "/mulResult",
                        "input": ["$.num1", "$.num2"],
                        "operation": "multiply",
                        "convert": "bigDecimal"
                      },
                      {
                        "name": "division",
                        "engine": "math-operation",
                        "output": "/divResult",
                        "input": ["$.num1", "$.num2"],
                        "operation": "divide",
                        "convert": "bigDecimal"
                      }
                    ]
                  }
                """;

        inputJson = """
                 {
                    "num1": 500000000000000000000000,
                    "num2": 100000000000000000000000
                  }
                """;
    }

    @Test
    void testMathOperationsTransformation() throws Exception {
        // Execute transformation
        ObjectNode result = transformJson(schemaJson, inputJson);

        // Print result for debugging
        System.out.println(result.toPrettyString());

        // Expected BigInteger calculations
        BigInteger num1 = new BigInteger("500000000000000000000000");
        BigInteger num2 = new BigInteger("100000000000000000000000");

        BigInteger expectedSum = num1.add(num2); // 600000000000000000000000
        BigInteger expectedSub = num1.subtract(num2); // 400000000000000000000000
        BigInteger expectedMul = num1.multiply(num2); // 50000000000000000000000000000000000000000

        // Assertions to verify transformation outputs
        assertNotNull(result);
        assertEquals(expectedSum.toString(), result.get("sumResult").asText());
        assertEquals(expectedSub.toString(), result.get("subResult").asText());
        assertEquals(expectedMul.toString(), result.get("mulResult").asText());
    }
}
