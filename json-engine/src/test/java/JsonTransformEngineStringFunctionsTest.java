import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.json.engine.JsonTransformer.transformJson;
import static org.junit.jupiter.api.Assertions.*;

public class JsonTransformEngineStringFunctionsTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private String schemaJson;
    private String inputJson;

    @BeforeEach
    void setUp() {
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
                       "value": 4
                     },
                     {
                       "name": "AdminUser",
                       "engine": "hard-coded",
                       "output": "#AdminUser",
                       "value": "AdminUser"
                     },
                     {
                       "name": "Guest",
                       "engine": "hard-coded",
                       "output": "#Guest",
                       "value": "Guest"
                     },
                     {
                       "name": "adminuser",
                       "engine": "hard-coded",
                       "output": "#adminuser",
                       "value": "adminuser"
                     },
                     {
                       "name": "toUpperCaseTest",
                       "engine": "string-operation",
                       "output": "/uppercaseValue",
                       "input": ["$.username"],
                       "function": "toUpperCase"
                     },
                     {
                       "name": "toLowerCaseTest",
                       "engine": "string-operation",
                       "output": "/lowercaseValue",
                       "input": ["$.username"],
                       "function": "toLowerCase"
                     },
                     {
                       "name": "substringTest",
                       "engine": "string-operation",
                       "output": "/substringValue",
                       "input": ["$.username"],
                       "function": "substring",
                       "start": "#jsonStart",
                       "end": "#jsonEnd"
                     },
                     {
                       "name": "replaceTest",
                       "engine": "string-operation",
                       "output": "/replacedValue",
                       "input": ["$.sentence"],
                       "function": "replace",
                       "target": "Hello",
                       "replacement": "Hi"
                     },
                     {
                       "name": "equalsMultiple",
                       "engine": "string-operation",
                       "output": "/equalsResults",
                       "input": ["$.username", "$.username"],
                       "function": "equals"
                     },
                     {
                       "name": "notEqualsMultiple",
                       "engine": "string-operation",
                       "output": "/notEqualsResults",
                       "input": ["$.username", "$.city"],
                       "function": "notEquals",
                       "compareTo": "#Guest"
                     },
                     {
                       "name": "equalsIgnoreCaseMultiple",
                       "engine": "string-operation",
                       "output": "/equalsIgnoreCaseResults",
                       "input": ["$.username", "$.city"],
                       "function": "equalsIgnoreCase",
                       "compareTo": "#adminuser"
                     },
                     {
                       "name": "splitBatch",
                       "engine": "string-operation",
                       "output": "/splitResults",
                       "input": ["$.commaSeparated1", "$.commaSeparated2"],
                       "function": "split",
                       "delimiter": ","
                     },
                     {
                       "name": "lengthBatch",
                       "engine": "string-operation",
                       "output": "/lengthResults",
                       "input": ["$.username", "$.city"],
                       "function": "length"
                     },
                     {
                       "name": "indexOfTestSingle",
                       "engine": "string-operation",
                       "output": "/indexOfSingle",
                       "input": ["$.sentence"],
                       "function": "indexOf",
                       "search": "world"
                     },
                     {
                       "name": "indexOfTestMultiple",
                       "engine": "string-operation",
                       "output": "/indexOfMultiple",
                       "input": ["$.sentence", "$.username"],
                       "function": "indexOf",
                       "search": "json"
                     },
                     {
                       "name": "regexReplaceTestSingle",
                       "engine": "string-operation",
                       "output": "/regexReplaceSingle",
                       "input": ["$.sentence"],
                       "function": "regexReplace",
                       "regex": "\\\\d+",
                       "replacement": "#"
                     },
                     {
                       "name": "regexReplaceTestMultiple",
                       "engine": "string-operation",
                       "output": "/regexReplaceMultiple",
                       "input": ["$.sentence", "$.username"],
                       "function": "regexReplace",
                       "regex": "json",
                       "replacement": "XML"
                     },
                     {
                       "name": "removeWhitespaceTestSingle",
                       "engine": "string-operation",
                       "output": "/removeWhitespaceSingle",
                       "input": ["$.sentence"],
                       "function": "removeWhitespace"
                     },
                     {
                       "name": "removeWhitespaceTestMultiple",
                       "engine": "string-operation",
                       "output": "/removeWhitespaceMultiple",
                       "input": ["$.sentence", "$.username"],
                       "function": "removeWhitespace"
                     },
                     {
                       "name": "substringTest_JSONPath",
                       "engine": "string-operation",
                       "output": "/substringJSONPath",
                       "input": ["$.username"],
                       "function": "substring",
                       "start": "#jsonStart",
                       "end": "#jsonEnd"
                     }
                   ]
                 }
                """;

        inputJson = """
                 {
                   "extraSpaces": "   Trim me!   ",
                   "commaSeparated": "apple,banana,grape,orange",
                     "city": "NewYork",
                     "extraSpaces2": "   More Spaces   ",
                     "sentence1": "Hello world!",
                     "sentence2": "Hello everyone!",
                     "commaSeparated1": "apple,banana,orange",
                     "commaSeparated2": "car,bike,train",
                     "domain": "example.com",
                     "sentence": "hello world 123, welcome to JSON transformation!",
                       "username": "jsonDeveloper",
                       "jsonStart": "1",
                         "jsonEnd": "5"
                 }
                """;
    }

    @Test
    void testStringFunctionTransformation() throws Exception {
        // Execute transformation
        ObjectNode result = transformJson(schemaJson, inputJson);

        // Print result for debugging
        System.out.println(result.toPrettyString());

        // Assertions to verify transformation outputs
        assertNotNull(result);
    }
}
