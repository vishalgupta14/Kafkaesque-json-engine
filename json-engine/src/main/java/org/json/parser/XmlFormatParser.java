package org.json.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class XmlFormatParser implements InputFormatParser {
    private final XmlMapper xmlMapper = new XmlMapper();

    @Override
    public ObjectNode parse(String input) throws Exception {
        JsonNode jsonNode = xmlMapper.readTree(input);
        return (ObjectNode) jsonNode;
    }
}
