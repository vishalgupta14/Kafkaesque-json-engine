import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.json.engine.JsonTransformer.transformJson;
import static org.junit.jupiter.api.Assertions.*;

public class JsonTransformEngineHardCodedTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private String schemaJson;

    @BeforeEach
    void setUp() {
        schemaJson = """
                {
                   "forms": [
                     {
                       "name": "hardcodedInteger",
                       "engine": "hard-coded",
                       "output": "/staticInteger",
                       "value": 100
                     },
                     {
                       "name": "hardcodedDouble",
                       "engine": "hard-coded",
                       "output": "/staticDouble",
                       "value": 99.99
                     },
                     {
                       "name": "hardcodedBoolean",
                       "engine": "hard-coded",
                       "output": "/staticBoolean",
                       "value": true
                     },
                     {
                       "name": "hardcodedString",
                       "engine": "hard-coded",
                       "output": "/staticString",
                       "value": "Hello, World!"
                     },
                     {
                       "name": "hardcodedArray",
                       "engine": "hard-coded",
                       "output": "/staticArray",
                       "value": [10, 20, 30, 40, 50]
                     },
                     {
                       "name": "hardcodedObject",
                       "engine": "hard-coded",
                       "output": "/staticObject",
                       "value": "{\\"key1\\": \\"value1\\", \\"key2\\": \\"value2\\"}"
                     },
                     {
                       "name": "hardcodedTimestamp",
                       "engine": "hard-coded",
                       "output": "/staticTimestamp",
                       "value": "1741797330000"
                     },
                     {
                       "name": "hardcodedISODate",
                       "engine": "hard-coded",
                       "output": "/staticISODate",
                       "value": "2025-03-12T10:15:30Z"
                     },
                     {
                       "name": "integerToDouble",
                       "engine": "hard-coded",
                       "output": "/convertedIntegerToDouble",
                       "value": 42,
                       "outputDataType": "double"
                     },
                     {
                       "name": "doubleToInteger",
                       "engine": "hard-coded",
                       "output": "/convertedDoubleToInteger",
                       "value": 42.75,
                       "outputDataType": "integer"
                     },
                     {
                       "name": "bigIntegerToBigDecimal",
                       "engine": "hard-coded",
                       "output": "/convertedBigIntegerToBigDecimal",
                       "value": 12345678901234567890,
                       "outputDataType": "bigDecimal"
                     },
                     {
                       "name": "bigDecimalToBigInteger",
                       "engine": "hard-coded",
                       "output": "/convertedBigDecimalToBigInteger",
                       "value": 9876543210.12345,
                       "outputDataType": "bigInteger"
                     },
                     {
                       "name": "timestampToISO",
                       "engine": "hard-coded",
                       "output": "/convertedTimestampToISO",
                       "value": 1741797330000,
                       "inputDataType": "timestamp",
                       "outputDataType": "ISODate"
                     },
                     {
                       "name": "ISOToTimestamp",
                       "engine": "hard-coded",
                       "output": "/convertedISOToTimestamp",
                       "value": "2025-03-12T10:15:30Z",
                       "inputDataType": "ISODate",
                       "outputDataType": "timestamp"
                     },
                     {
                       "name": "booleanToString",
                       "engine": "hard-coded",
                       "output": "/convertedBooleanToString",
                       "value": true,
                       "outputDataType": "string"
                     },
                     {
                       "name": "stringToBoolean",
                       "engine": "hard-coded",
                       "output": "/convertedStringToBoolean",
                       "value": false,
                       "outputDataType": "boolean"
                     },
                      {
                       "name": "jsonStart",
                       "engine": "hard-coded",
                       "output": "#storedValue",
                       "value": 0
                     },
                     {
                       "name": "hardcodedFromVariable",
                       "engine": "hard-coded",
                       "output": "/staticVariable",
                       "value": "#storedValue"
                     },
                     {
                       "name": "hardcodedFromJsonPath",
                       "engine": "hard-coded",
                       "output": "/staticJsonPath",
                       "value": "test"
                     }
                   ]
                 }
                
                """;
    }

    @Test
    void testHardcodedJsonTransformation() throws Exception {
        // Execute transformation (no input JSON needed for hardcoded values)
        ObjectNode result = transformJson(schemaJson, "{}");

        // Print result for debugging
        System.out.println(result.toPrettyString());

        // Assertions to verify transformation
        assertNotNull(result);
    }
}
