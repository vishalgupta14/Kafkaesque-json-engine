package org.json.parser;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface InputFormatParser {
    ObjectNode parse(String input) throws Exception;
}
