package com.kafkaesque.transformer.model.mongodb;


import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Data
@Document(collection = "failed_transformations")
public class TransformationError {
    @Id
    private String id;
    private String clientId;
    private JsonNode originalMessage;
    private String errorMessage;
    private Instant failedAt;
}
