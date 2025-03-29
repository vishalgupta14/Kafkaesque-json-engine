package org.json.model;

import java.util.Map;

public class ClientSchema {
    private String clientId;
    private Map<String, Object> schema;

    public ClientSchema() {}

    public ClientSchema(String clientId, Map<String, Object> schema) {
        this.clientId = clientId;
        this.schema = schema;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Map<String, Object> getSchema() {
        return schema;
    }

    public void setSchema(Map<String, Object> schema) {
        this.schema = schema;
    }
}
