package org.json;

import java.util.HashMap;
import java.util.Map;


public class VariableCache {
    private static final Map<String, Object> variableStore = new HashMap<>();

    // Method to store a variable
    public static void setVariable(String key, Object value) {
        variableStore.put(key, value);
    }

    // Method to retrieve a variable
    public static Object getVariable(String key) {
        return variableStore.get(key);
    }

    // Method to check if a variable exists
    public static boolean containsVariable(String key) {
        return variableStore.containsKey(key);
    }

    // Method to remove a variable
    public static void removeVariable(String key) {
        variableStore.remove(key);
    }

    // Method to clear all stored variables
    public static void clearAllVariables() {
        variableStore.clear();
    }

    // Method to retrieve all stored variables
    public static Map<String, Object> getAllVariables() {
        return new HashMap<>(variableStore);
    }
}

