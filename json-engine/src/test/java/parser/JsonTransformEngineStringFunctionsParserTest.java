package parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;
import java.util.Map;

import static org.json.engine.JsonTransformer.transformJson;
import static org.junit.jupiter.api.Assertions.*;

public class JsonTransformEngineStringFunctionsParserTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final XmlMapper xmlMapper = new XmlMapper();
    private static final CsvMapper csvMapper = new CsvMapper();

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
    void testStringFunctionTransformation_andConvertToCsvXml() throws Exception {
        // 1. Perform transformation
        ObjectNode result = transformJson(schemaJson, inputJson);
        System.out.println("===== JSON OUTPUT =====");
        System.out.println(result.toPrettyString());
        assertNotNull(result);

        // 2. Convert to XML
        String xml = xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
        System.out.println("===== XML OUTPUT =====");
        System.out.println(xml);

        // 3. Convert to CSV (flattened map)
        Map<String, Object> map = objectMapper.convertValue(result, Map.class);
        CsvSchema.Builder schemaBuilder = CsvSchema.builder();
        map.keySet().forEach(schemaBuilder::addColumn);
        CsvSchema schema = schemaBuilder.build().withHeader();

        StringWriter csvWriter = new StringWriter();
        csvMapper.writer(schema).writeValue(csvWriter, map);

        System.out.println("===== CSV OUTPUT =====");
        System.out.println(csvWriter);
    }
}
