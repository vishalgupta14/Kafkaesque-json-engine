package large.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.json.engine.JsonTransformer.transformJson;
import static org.junit.jupiter.api.Assertions.*;

public class JsonTransformEngineTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private String schemaJson;
    private String inputJson;

    @BeforeEach
    void setUp() {
        // Generate JSON schema dynamically with 1000 fields
        String fieldsSchema = IntStream.rangeClosed(1, 1000)
                .mapToObj(i -> """
                        {
                          "name": "field%d",
                          "engine": "straight-pull",
                          "output": "/field%d",
                          "input": ["$.field%d"],
                          "inputDataType": "integer",
                          "outputDataType": "double"
                        }
                        """.formatted(i, i, i))
                .collect(Collectors.joining(",\n"));

        schemaJson = """
                {
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
                      "value": "4"
                    },
                    {
                      "name": "data",
                      "engine": "object-array",
                      "output": "/transformed",
                      "input": [""],
                      "schema": [
                        """ + fieldsSchema + """
                      ],
                      "inputDataType": "object",
                      "outputDataType": "array"
                    }
                  ]
                }
                """;

        // Generate input JSON with 1000 fields
        String fieldsInput = IntStream.rangeClosed(1, 1000)
                .mapToObj(i -> "\"field%d\": %d".formatted(i, i))
                .collect(Collectors.joining(",\n"));

        inputJson = """
                {
                  """ + fieldsInput + """
                }
                """;
    }

    @Test
    void testJsonTransformation() throws Exception {
        // Execute transformation
        ObjectNode result = transformJson(schemaJson, inputJson);
        System.out.println(result.toPrettyString());
        // Assertions
        assertNotNull(result);
        assertTrue(result.has("transformed"));
    }
}
