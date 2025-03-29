package com.kafkaesque.consumer.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ClientEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(ClientEventConsumer.class);

    @KafkaListener(topics = "client-a-events", groupId = "kafkaesque-client-validator")
    public void listenClientA(ConsumerRecord<String, String> record) {
        log.info("[client-a-events] Received Key: {}, Value: {}", record.key(), record.value());
    }

    @KafkaListener(topics = "client-b-events", groupId = "kafkaesque-client-validator")
    public void listenClientB(ConsumerRecord<String, String> record) {
        log.info("[client-b-events] Received Key: {}, Value: {}", record.key(), record.value());
    }

    // You can duplicate the above method for more client topics as needed
}