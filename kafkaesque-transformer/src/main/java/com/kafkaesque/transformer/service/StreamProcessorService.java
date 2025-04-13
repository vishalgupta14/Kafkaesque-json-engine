package com.kafkaesque.transformer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kafkaesque.transformer.model.ClientSchema;
import com.kafkaesque.transformer.model.mongodb.RawInputEvent;
import com.kafkaesque.transformer.model.mongodb.TransformationError;
import com.kafkaesque.transformer.model.mongodb.TransformedEvent;
import com.kafkaesque.transformer.registry.ClientSchemaRegistry;
import com.kafkaesque.transformer.repository.TransformationErrorRepository;
import com.kafkaesque.transformer.repository.TransformedEventRepository;
import com.kafkaesque.transformer.repository.RawInputEventRepository;
import com.kafkaesque.transformer.utils.AESUtil;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.ValueTransformerWithKey;
import org.apache.kafka.streams.kstream.ValueTransformerWithKeySupplier;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.json.engine.JsonTransformer;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Service
public class StreamProcessorService {

    private final ObjectMapper mapper = new ObjectMapper();
    private final ClientSchemaRegistry schemaRegistry;
    private final TransformationErrorRepository errorRepository;
    private final TransformedEventRepository transformedEventRepository;
    private final RawInputEventRepository rawInputRepository;

    private static final String STORE_NAME = "seen-transactions";
    private static final long RETENTION_MS = TimeUnit.HOURS.toMillis(24);

    public StreamProcessorService(ClientSchemaRegistry schemaRegistry, TransformationErrorRepository errorRepository, TransformedEventRepository transformedEventRepository, RawInputEventRepository rawInputRepository) {
        this.schemaRegistry = schemaRegistry;
        this.errorRepository = errorRepository;
        this.transformedEventRepository = transformedEventRepository;
        this.rawInputRepository = rawInputRepository;
    }

    public void buildPipeline(StreamsBuilder builder) {
        KStream<String, String> stream = builder.stream("incoming-events", Consumed.with(Serdes.String(), Serdes.String()));


        stream.filter((key, value) -> value != null)
              .mapValues(this::processEvent)
                .to((key, value, context) -> {
                    try {
                        JsonNode json = mapper.readTree(value);
                        if (json.has("error")) return "error-events";

                        String clientId = json.get("clientId").asText();
                        ClientSchema schema = schemaRegistry.getSchema(clientId);

                        if (schema != null && schema.getSchema().get("clientTopic") != null) {
                            return schema.getSchema().get("clientTopic").toString().replaceAll("\"", "");
                        } else {
                            return "default-client-events";
                        }
                    } catch (Exception e) {
                        return "error-events";
                    }
                }, Produced.with(Serdes.String(), Serdes.String()));
    }

    private ValueTransformerWithKeySupplier<String, String, String> deduplicationTransformer() {
        return () -> new ValueTransformerWithKey<String, String, String>() {
            private KeyValueStore<String, Long> store;

            @Override
            public void init(ProcessorContext context) {
                this.store = context.getStateStore(STORE_NAME);
            }

            @Override
            public String transform(String key, String value) {
                try {
                    JsonNode json = mapper.readTree(value);
                    String txnId = json.get("transactionId").asText();
                    long now = System.currentTimeMillis();
                    Long lastSeen = store.get(txnId);
                    if (lastSeen != null && (now - lastSeen) < RETENTION_MS) {
                        return null;
                    }
                    store.put(txnId, now);
                    return value;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void close() {}
        };
    }

    private String processEvent(String value) {
        try {
            JsonNode inputJson = mapper.readTree(value);
            String clientId = inputJson.get("clientId").asText();
            String txnId = inputJson.get("transactionId").asText();

            // 🟢 Save raw input immediately
            RawInputEvent raw = new RawInputEvent();
            raw.setClientId(clientId);
            raw.setTransactionId(txnId);
            raw.setEncryptedPayload(AESUtil.encrypt(value)); // or store as-is
            raw.setReceivedAt(Instant.now());
            rawInputRepository.save(raw);

            ClientSchema clientSchema = schemaRegistry.getSchema(clientId);
            if (clientSchema == null) throw new RuntimeException("No schema found for client: " + clientId);
            String schemaJson = mapper.writeValueAsString(clientSchema.getSchema());
            ObjectNode transformed = JsonTransformer.transformJson(schemaJson, inputJson.toString());
            TransformedEvent event = new TransformedEvent();
            event.setClientId(clientId);
            event.setTransactionId(txnId);
            event.setTransformedPayload(transformed);
            event.setProcessedAt(Instant.now());
            transformedEventRepository.save(event);
            return mapper.writeValueAsString(transformed);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                JsonNode originalJson = mapper.readTree(value);
                TransformationError error = new TransformationError();
                error.setClientId(originalJson.path("clientId").asText());
                error.setOriginalMessage(originalJson);
                error.setErrorMessage(e.getMessage());
                error.setFailedAt(Instant.now());
                errorRepository.save(error);
            } catch (Exception ignored) {}
            return value;
        }
    }
}
