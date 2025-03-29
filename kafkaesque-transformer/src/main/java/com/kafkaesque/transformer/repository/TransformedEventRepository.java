package com.kafkaesque.transformer.repository;


import com.kafkaesque.transformer.model.mongodb.TransformedEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransformedEventRepository extends MongoRepository<TransformedEvent, String> {
}
