package com.kafkaesque.transformer.model.mongodb;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@Document(collection = "rawInputEvent")
public class RawInputEvent {
    @Id
    private String id;
    private String clientId;
    private String transactionId;
    private String encryptedPayload; // Optional: store encrypted form
    private Instant receivedAt;
}
