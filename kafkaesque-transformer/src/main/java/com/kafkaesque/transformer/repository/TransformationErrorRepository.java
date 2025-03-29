package com.kafkaesque.transformer.repository;

import com.kafkaesque.transformer.model.mongodb.TransformationError;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransformationErrorRepository extends MongoRepository<TransformationError, String> {
}
