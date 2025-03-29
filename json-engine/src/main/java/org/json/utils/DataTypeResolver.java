package org.json.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.VariableCache;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DataTypeResolver {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    public static void setJsonValue(ObjectNode root, String path, Object value, String outputType) {
        // If path starts with $, store in variableStore instead of JSON
        if (path.startsWith("#")) {
            VariableCache.setVariable(path, value);
            return;
        }

        // If output path is empty, merge into the current JSON object
        if (path == null || path.isEmpty()) {
            if (value instanceof ObjectNode) {
                root.setAll((ObjectNode) value);
            } else if (value instanceof ArrayNode) {
                root.set("array", (ArrayNode) value);
            } else if (value instanceof List) {
                ArrayNode arrayNode = objectMapper.createArrayNode();
                for (Object item : (List<?>) value) {
                    arrayNode.add(objectMapper.valueToTree(item));
                }
                root.set("array", arrayNode);
            } else if (value instanceof Integer) {
                root.put("integer", (Integer) value);
            } else if (value instanceof Long) {
                root.put("long", (Long) value);
            } else if (value instanceof Double) {
                root.put("double", (Double) value);
            } else if (value instanceof Boolean) {
                root.put("boolean", (Boolean) value);
            } else if (value instanceof BigDecimal) {
                root.put("bigDecimal", ((BigDecimal) value).toPlainString());
            } else if (value instanceof BigInteger) {
                root.put("bigInteger", value.toString());
            } else {
                root.put("string", value.toString());
            }
            return;
        }

        String[] keys = path.substring(1).split("/");
        ObjectNode currentNode = root;

        for (int i = 0; i < keys.length - 1; i++) {
            if (!currentNode.has(keys[i])) {
                currentNode.set(keys[i], objectMapper.createObjectNode());
            }
            currentNode = (ObjectNode) currentNode.get(keys[i]);
        }

        // Assign the value at the correct path
        if (value instanceof ObjectNode) {
            currentNode.set(keys[keys.length - 1], (ObjectNode) value);
        } else if (value instanceof ArrayNode) {
            currentNode.set(keys[keys.length - 1], (ArrayNode) value);
        } else if (value instanceof List) {
            ArrayNode arrayNode = objectMapper.createArrayNode();
            for (Object item : (List<?>) value) {
                arrayNode.add(objectMapper.valueToTree(item));
            }
            currentNode.set(keys[keys.length - 1], arrayNode);
        } else if (value instanceof Integer) {
            currentNode.put(keys[keys.length - 1], (Integer) value);
        } else if (value instanceof Long) {
            currentNode.put(keys[keys.length - 1], (Long) value);
        } else if (value instanceof Double) {
            currentNode.put(keys[keys.length - 1], (Double) value);
        } else if (value instanceof Boolean) {
            currentNode.put(keys[keys.length - 1], (Boolean) value);
        } else if (value instanceof BigDecimal) {
            currentNode.put(keys[keys.length - 1], ((BigDecimal) value).toPlainString());
        } else if (value instanceof BigInteger) {
            currentNode.put(keys[keys.length - 1], value.toString());
        } else {
            currentNode.put(keys[keys.length - 1], value.toString());
        }
    }


    public static String determineDataType(Object value) {
        if (value == null) return "nullValue";
        if (value instanceof List) {
            if (!((List<?>) value).isEmpty()) {
                Object firstElement = ((List<?>) value).get(0);
                return determineDataType(firstElement) + "Array";
            }
            return "array";
        }
        if (value instanceof Integer || value instanceof Long) return "integer";
        if (value instanceof BigInteger) return "bigInteger";
        if (value instanceof Double || value instanceof Float) return "double";
        if (value instanceof BigDecimal) return "bigDecimal";
        if (value instanceof Boolean) return "boolean";
        if (value instanceof Map) return "object";
        if (value instanceof String) {
            String strVal = value.toString().trim();
            try {
                Instant.parse(strVal);
                return "ISODate";
            } catch (Exception e) {
                try {
                    Long.parseLong(strVal);
                    return "timestamp";
                } catch (Exception ex) {
                    return "string";
                }
            }
        }
        return "string";
    }

    public static Object convertDataType(Object value, String inputType, String outputType, String inputFormat, String outputFormat) throws ParseException {
        if (inputType.equals(outputType)) {
            return value;
        } else if (inputType.endsWith("Array") && outputType.endsWith("Array")) {
            List<Object> inputList = (List<Object>) value;
            ArrayNode outputArray = objectMapper.createArrayNode();
            for (Object element : inputList) {
                Object convertedElement = convertDataType(element, inputType.replace("Array", ""), outputType.replace("Array", ""), inputFormat, outputFormat);
                if (convertedElement instanceof Integer) {
                    outputArray.add((Integer) convertedElement);
                } else if (convertedElement instanceof Long) {
                    outputArray.add((Long) convertedElement);
                } else if (convertedElement instanceof Double) {
                    outputArray.add((Double) convertedElement);
                } else if (convertedElement instanceof Boolean) {
                    outputArray.add((Boolean) convertedElement);
                } else {
                    outputArray.add(convertedElement.toString());
                }
            }
            return outputArray;
        } else if (inputType.equals("timestamp") && outputType.equals("ISODate")) {
            return Instant.ofEpochMilli(Long.parseLong(value.toString())).toString();
        } else if (inputType.equals("timestamp") && outputType.equals("date")) {
            return new SimpleDateFormat(outputFormat).format(new Date(Long.parseLong(value.toString())));
        } else if (inputType.equals("timestamp") && outputType.equals("mongodbDate")) {
            return objectMapper.createObjectNode().put("$date", Instant.ofEpochMilli(Long.parseLong(value.toString())).toString());
        } else if (inputType.equals("ISODate") && outputType.equals("timestamp")) {
            return Instant.parse(value.toString()).toEpochMilli();
        } else if (inputType.equals("ISODate") && outputType.equals("date")) {
            return DateTimeFormatter.ofPattern(outputFormat).withZone(ZoneId.systemDefault()).format(Instant.parse(value.toString()));
        } else if (inputType.equals("ISODate") && outputType.equals("mongodbDate")) {
            return objectMapper.createObjectNode().put("$date", value.toString());
        } else if (inputType.equals("date") && outputType.equals("ISODate")) {
            SimpleDateFormat sdf = new SimpleDateFormat(inputFormat);
            Date date = sdf.parse(value.toString());
            return Instant.ofEpochMilli(date.getTime()).toString();
        } else if (inputType.equals("date") && outputType.equals("timestamp")) {
            SimpleDateFormat sdf = new SimpleDateFormat(inputFormat);
            Date date = sdf.parse(value.toString());
            return date.getTime();
        } else if (inputType.equals("date") && outputType.equals("mongodbDate")) {
            SimpleDateFormat sdf = new SimpleDateFormat(inputFormat);
            Date date = sdf.parse(value.toString());
            return objectMapper.createObjectNode().put("$date", Instant.ofEpochMilli(date.getTime()).toString());
        } else if (inputType.equals("integer") && outputType.equals("double")) {
            return ((Number) value).doubleValue();
        } else if (inputType.equals("double") && outputType.equals("integer")) {
            return ((Number) value).intValue();
        } else if (inputType.equals("bigInteger") && outputType.equals("bigDecimal")) {
            int scale = outputFormat != null ? Integer.parseInt(outputFormat) : 0;
            return new BigDecimal((BigInteger) value).movePointLeft(scale);
        } else if (inputType.equals("integer") && outputType.equals("bigDecimal")) {
            int scale = outputFormat != null ? Integer.parseInt(outputFormat) : 0;
            return new BigDecimal((BigInteger) value).movePointLeft(scale);
        } else if (inputType.equals("bigDecimal") && outputType.equals("bigInteger")) {
            return new BigDecimal(value.toString()).toBigInteger();
        }else if (inputType.equals("double") && outputType.equals("bigInteger")) {
            return new BigDecimal(value.toString()).toBigInteger();
        } else if (outputType.equals("integer")) {
            return (int) Double.parseDouble(value.toString());
        } else if (outputType.equals("double")) {
            return Double.parseDouble(value.toString());
        } else if (outputType.equals("boolean")) {
            return Boolean.parseBoolean(value.toString());
        } else if (outputType.equals("string")) {
            return String.valueOf(value);
        } else if (outputType.equals("bigInteger")) {
            BigDecimal decimalValue = new BigDecimal(value.toString());
            return decimalValue.scale() > 0 ? decimalValue.toBigInteger() : new BigInteger(value.toString());
        }else if (outputType.equals("bigDecimal")) {
            int scale = outputFormat != null ? Integer.parseInt(outputFormat) : 0;
            return new BigDecimal(value.toString()).movePointLeft(scale);
        }

        return value;
    }

    public static BigDecimal convertToBigDecimal(Object value) {
        try {
            if (value instanceof Integer) {
                return BigDecimal.valueOf((Integer) value);
            } else if (value instanceof Long) {
                return BigDecimal.valueOf((Long) value);
            } else if (value instanceof Double) {
                return BigDecimal.valueOf((Double) value);
            } else if (value instanceof BigDecimal) {
                return (BigDecimal) value;
            } else {
                return new BigDecimal(value.toString());
            }
        } catch (Exception e) {
            return null; // Non-numeric values return null
        }
    }

    public static Object convertToOutputType(BigDecimal value, String outputDataType) {
        if (value == null) return null;

        // Detect if the value is an integer (i.e., has no fractional part)
        boolean isIntegerValue = value.stripTrailingZeros().scale() <= 0;

        // If outputDataType is not specified, determine it dynamically
        if (outputDataType == null || outputDataType.isEmpty()) {
            if (isIntegerValue) {
                if (value.compareTo(BigDecimal.valueOf(Long.MIN_VALUE)) >= 0 &&
                        value.compareTo(BigDecimal.valueOf(Long.MAX_VALUE)) <= 0) {
                    outputDataType = "long"; // Fits within long range
                } else {
                    outputDataType = "bigInteger"; // Too large for long, use BigInteger
                }
            } else {
                outputDataType = "double"; // If not an integer, default to double
            }
        }

        // Convert based on detected or specified outputDataType
        switch (outputDataType) {
            case "integer":
            case "long":
                return isIntegerValue ? value.longValueExact() : value.longValue();
            case "double":
                return value.doubleValue();
            case "bigInteger":
                return isIntegerValue ? value.toBigIntegerExact() : value.toBigInteger();
            case "bigDecimal":
            default:
                return value;
        }
    }

}
