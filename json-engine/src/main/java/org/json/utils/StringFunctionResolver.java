package org.json.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.jayway.jsonpath.JsonPath;
import org.json.VariableCache;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.Character.isDigit;

public class StringFunctionResolver {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Object applyStringFunction(List<String> values, String function, Map<String, Object> xform, String inputJson) {
        List<Object> transformedValues;

        switch (function) {
            case "toUpperCase":
                transformedValues = values.stream().map(String::toUpperCase).collect(Collectors.toList());
                break;
            case "toLowerCase":
                transformedValues = values.stream().map(String::toLowerCase).collect(Collectors.toList());
                break;
            case "trim":
                transformedValues = values.stream().map(String::trim).collect(Collectors.toList());
                break;
            case "substring":
                int start = resolveIntegerValue(xform.get("start"), inputJson);
                int end = resolveIntegerValue(xform.get("end"), inputJson);

                transformedValues = values.stream()
                        .map(s -> s.substring(Math.max(0, start), Math.min(s.length(), end)))
                        .collect(Collectors.toList());
                break;
            case "replace":
                String target = (String) xform.get("target");
                String replacement = (String) xform.get("replacement");
                transformedValues = values.stream()
                        .map(s -> s.replace(target, replacement))
                        .collect(Collectors.toList());
                break;
            case "equals":
                return values.stream().distinct().count() == 1;
            case "notEquals":
                return values.stream().distinct().count() > 1;
            case "equalsIgnoreCase":
                return values.stream()
                        .map(String::toLowerCase)
                        .distinct()
                        .count() == 1;
            case "contains":
                return values.stream().anyMatch(s -> s.contains((String) xform.get("substring")));
            case "split":
                String delimiter = (String) xform.getOrDefault("delimiter", ",");
                ArrayNode arrayNode = objectMapper.createArrayNode();
                for (String s : values) {
                    for (String part : s.split(delimiter)) {
                        arrayNode.add(part);
                    }
                }
                return arrayNode;
            case "length":
                transformedValues = values.stream().map(String::length).collect(Collectors.toList());
                break;
            case "startsWith":
                return values.stream().allMatch(s -> s.startsWith((String) xform.get("prefix")));
            case "endsWith":
                return values.stream().allMatch(s -> s.endsWith((String) xform.get("suffix")));
            case "concat":
                return String.join((String) xform.getOrDefault("delimiter", ""), values);
            case "indexOf":
                String searchString = resolveStringValue(xform.get("search"), inputJson);
                transformedValues = values.stream()
                        .map(s -> s.indexOf(searchString))
                        .collect(Collectors.toList());
                break;
            case "capitalize":
                transformedValues = values.stream()
                        .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase())
                        .collect(Collectors.toList());
                break;
            case "regexReplace":
                String regex = (String) xform.get("regex");
                String replacementRegex = (String) xform.get("replacement");
                transformedValues = values.stream()
                        .map(s -> s.replaceAll(regex, replacementRegex))
                        .collect(Collectors.toList());
                break;
            case "removeWhitespace":
                transformedValues = values.stream()
                        .map(s -> s.replaceAll("\\s+", ""))
                        .collect(Collectors.toList());
                break;
            default:
                return values; // Return original values if no function matches
        }

        return transformedValues.size() == 1 ? transformedValues.get(0) : transformedValues;
    }

    /**
     * Resolves an integer value from:
     * - Direct number input
     * - Variable store
     * - JSON path
     * - Defaults to 0 if not found
     */
    public static int resolveIntegerValue(Object value, String inputJson) {
        if (value == null) return 0; // Default if missing

        // Direct integer value
        if (value instanceof Integer) {
            return (int) value;
        }

        // If it's a valid digit string, parse it
        if (value instanceof String && ((String) value).matches("\\d+")) {
            return Integer.parseInt((String) value);
        }

        // If it's a variable, fetch from VariableCache
        if (value instanceof String && ((String) value).startsWith("#")) {
            Object varValue = VariableCache.getVariable((String) value);
            if (varValue instanceof Integer) {
                return (int) varValue;
            }
            if (varValue instanceof String && ((String) varValue).matches("\\d+")) {
                return Integer.parseInt((String) varValue);
            }
        }

        // If it's a JSON path, try to fetch from inputJson
        if (value instanceof String && ((String) value).startsWith("$.") ) {
            try {
                Object jsonValue = JsonPath.read(inputJson, (String) value);
                if (jsonValue instanceof Integer) {
                    return (int) jsonValue;
                }
                if (jsonValue instanceof String && ((String) jsonValue).matches("\\d+")) {
                    return Integer.parseInt((String) jsonValue);
                }
            } catch (Exception e) {
                System.out.println("Warning: Unable to resolve integer value for '" + value + "'");
            }
        }

        return 0; // Default if not found
    }

    /**
     * Resolves a string value from a given input.
     * Supports JSON paths ($.), variable lookups (#), and direct string values.
     */
    public static String resolveStringValue(Object value, String inputJson) {
        if (value == null) return ""; // Default to empty string if missing

        // If it's a variable (#), fetch from VariableCache
        if (value instanceof String && ((String) value).startsWith("#")) {
            Object varValue = VariableCache.getVariable((String) value);
            if (varValue instanceof String) {
                return (String) varValue;
            } else if (varValue != null) {
                return varValue.toString(); // Convert non-string variables to string
            }
        }

        // If it's a JSON path ($.), try to fetch from inputJson
        if (value instanceof String && ((String) value).startsWith("$.") ) {
            try {
                Object jsonValue = JsonPath.read(inputJson, (String) value);
                if (jsonValue instanceof String) {
                    return (String) jsonValue;
                } else if (jsonValue != null) {
                    return jsonValue.toString(); // Convert JSON values to string
                }
            } catch (Exception e) {
                System.out.println("Warning: Unable to resolve JSON path for '" + value + "'");
            }
        }

        return value.toString();
    }



}
