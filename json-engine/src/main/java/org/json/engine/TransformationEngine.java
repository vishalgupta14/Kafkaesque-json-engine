package org.json.engine;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Map;

interface TransformationEngine {
    void process(Map<String, Object> xform, String inputJson, ObjectNode outputJson) throws Exception;
}