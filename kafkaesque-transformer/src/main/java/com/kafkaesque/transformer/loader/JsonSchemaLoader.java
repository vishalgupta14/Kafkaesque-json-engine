package com.kafkaesque.transformer.loader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafkaesque.transformer.model.ClientSchema;
import com.kafkaesque.transformer.registry.ClientSchemaRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class JsonSchemaLoader implements ApplicationRunner {

    @Value("classpath:schemas/*.json")
    private Resource[] schemaFiles;

    private final ClientSchemaRegistry registry;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonSchemaLoader(ClientSchemaRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void run(ApplicationArguments args) {
        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (Resource resource : schemaFiles) {
            executor.submit(() -> loadSchemaFromFile(resource));
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                System.err.println("Schema loading timed out.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Total schemas loaded: " + registry.size());
    }

    private void loadSchemaFromFile(Resource resource) {
        try (InputStream is = resource.getInputStream()) {
            JsonNode root = objectMapper.readTree(is);

            String clientId = root.get("clientId").asText();  // Ensure this field exists in your JSON

            Map<String, Object> schemaMap = objectMapper.convertValue(
                    root, new TypeReference<Map<String, Object>>() {}
            );

            ClientSchema clientSchema = new ClientSchema(clientId, schemaMap);
            registry.register(clientId, clientSchema);

            System.out.println("Loaded schema for client: " + clientId);
        } catch (Exception e) {
            System.err.println("Failed to load schema from file " +
                    resource.getFilename() + ": " + e.getMessage());
        }
    }
}
