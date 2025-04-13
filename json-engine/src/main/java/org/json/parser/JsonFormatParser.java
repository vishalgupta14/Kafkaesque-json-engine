package org.json.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonFormatParser implements InputFormatParser {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public ObjectNode parse(String input) throws Exception {
        return (ObjectNode) mapper.readTree(input);
    }
}
