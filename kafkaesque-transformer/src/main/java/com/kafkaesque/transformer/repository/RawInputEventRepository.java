package com.kafkaesque.transformer.repository;

import com.kafkaesque.transformer.model.mongodb.RawInputEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface RawInputEventRepository extends MongoRepository<RawInputEvent, String> {
    // optional: findByClientId, transactionId, etc.
}
