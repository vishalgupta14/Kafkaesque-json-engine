package com.kafkaesque.transformer.utils;

import java.security.SecureRandom;

public class SecureRandomGenerator {
    private static final SecureRandom secureRandom = new SecureRandom();

    public static byte[] randomBytes(int length) {
        byte[] iv = new byte[length];
        secureRandom.nextBytes(iv);
        return iv;
    }
}
