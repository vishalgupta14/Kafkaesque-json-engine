package com.kafkaesque.client.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FinanceEventPayload {
    private String clientId;
    private String transactionId;
    private String userName;
    private String email;
    private String accountType;
    private String transactionType;
    private String currency;
    private double amount;
    private double fee;
    private double tax;
    private String description;
    private String timestamp;
    private String region;
}