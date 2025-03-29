package com.kafkaesque.transformer.registry;

import com.kafkaesque.transformer.model.ClientSchema;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ClientSchemaRegistry {
    private final Map<String, ClientSchema> schemaMap = new ConcurrentHashMap<>();

    public void register(String clientId, ClientSchema schema) {
        schemaMap.put(clientId, schema);
    }

    public ClientSchema getSchema(String clientId) {
        return schemaMap.get(clientId);
    }

    public Set<String> getAllClientIds() {
        return schemaMap.keySet();
    }

    public int size() {
        return schemaMap.size();
    }
}
