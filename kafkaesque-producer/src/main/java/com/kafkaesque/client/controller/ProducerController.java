package com.kafkaesque.client.controller;

import com.kafkaesque.client.model.FinanceEventPayload;
import com.kafkaesque.client.service.ProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/produce")
public class ProducerController {

    private static final Logger logger = LoggerFactory.getLogger(ProducerController.class);

    @Autowired
    private ProducerService producerService;

    private final ExecutorService executor = Executors.newCachedThreadPool();
   //curl -X POST "http://localhost:7072/api/produce/burst/5"
    @PostMapping("/burst/{rate}")
    public String sendBurst(@PathVariable int rate) {
        executor.submit(() -> {
            while (true) {
                for (int i = 0; i < rate; i++) {
                    FinanceEventPayload payload = generatePayload();
                    producerService.sendEvent(payload);
                    logger.info("✅ Sent: {}", payload);
                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        return "Started sending " + rate + " messages per second";
    }

    private FinanceEventPayload generatePayload() {
        FinanceEventPayload payload = new FinanceEventPayload();
        payload.setClientId("client-a");
        payload.setTransactionId("txn-" + UUID.randomUUID());
        payload.setUserName("financeUser" + UUID.randomUUID().toString().substring(0, 5));
        payload.setEmail("user@example.com");
        payload.setAccountType("savings");
        payload.setTransactionType("deposit");
        payload.setCurrency("USD");
        payload.setAmount(Math.random() * 10000);
        payload.setFee(Math.random() * 50);
        payload.setTax(Math.random() * 20);
        payload.setDescription("Auto-generated finance transaction");
        payload.setTimestamp(Instant.now().toString());
        payload.setRegion("north-america");
        return payload;
    }
}