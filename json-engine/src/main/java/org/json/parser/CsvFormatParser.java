package org.json.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.util.List;
import java.util.Map;

public class CsvFormatParser implements InputFormatParser {
    private final CsvSchema schema = CsvSchema.emptySchema().withHeader();
    private final CsvMapper csvMapper = new CsvMapper();

    @Override
    public ObjectNode parse(String input) throws Exception {
        // Read CSV as list of Map<String, String>
        List<Map<String, String>> records = (List<Map<String, String>>) (List<?>) csvMapper
                .readerFor(Map.class)
                .with(schema)
                .readValues(input)
                .readAll();

        // Convert list to JSON array under the key "records"
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode result = objectMapper.createObjectNode();
        result.set("records", objectMapper.valueToTree(records));

        return result;
    }
}
