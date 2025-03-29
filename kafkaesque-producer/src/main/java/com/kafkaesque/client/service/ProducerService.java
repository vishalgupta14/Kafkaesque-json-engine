package com.kafkaesque.client.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafkaesque.client.model.FinanceEventPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProducerService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper mapper = new ObjectMapper();

    public void sendEvent(FinanceEventPayload payload) {
        try {
            String json = mapper.writeValueAsString(payload);
            kafkaTemplate.send("incoming-events", payload.getClientId(), json);
            System.out.println("✅ Sent: " + json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}