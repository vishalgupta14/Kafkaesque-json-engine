package com.kafkaesque.transformer.model.mongodb;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document(collection = "transformed_events")
public class TransformedEvent {

    @Id
    private String id;

    private String clientId;
    private String transactionId;
    private JsonNode transformedPayload;
    private Instant processedAt;
}
