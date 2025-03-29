# Kafkaesque Project Suite

A modular Kafka Streams-based real-time transformation pipeline with the following components:

---

## 📦 kafkaesque-producer

### Purpose
Simulates incoming raw JSON events and pushes them into the Kafka topic `incoming-events`.

### Usage

```bash
./gradlew bootRun # or mvn spring-boot:run
```

### Key Features
- Accepts command-line or API-based input.
- Pushes structured data to Kafka for transformation by the transformer module.

---

## 🔄 kafkaesque-transformer

### Purpose
Transforms incoming JSON messages using client-specific transformation schemas and stores processed results into MongoDB and pushes them to target Kafka topics.

### Features
- Kafka Streams based real-time transformation.
- Deduplication logic using transactionId.
- Schema-driven transformation using `json-engine`.
- Stores transformation audit logs in MongoDB.
- Dynamic topic routing using schema-defined `clientTopic`.
- Error messages go to `error-events`.

### Kafka Topics
- `incoming-events`: Source topic for raw messages.
- `client-a-events`, `client-b-events`, etc.: Per-client transformed output.
- `error-events`: For transformation failures.

### MongoDB Collections
- `transformedEvent`: Stores successfully transformed records.
- `transformationError`: Stores failed transformation logs.

### Run

```bash
./gradlew bootRun # or mvn spring-boot:run
```

---

## 👂 kafkaesque-consumer

### Purpose
Listens to client-specific topics (like `client-a-events`) and logs or verifies that transformed data is received correctly.

### Features
- Lightweight Spring Boot Kafka listener.
- Logs all incoming transformed messages.
- Helps validate transformation correctness.

### Example Log

```
Received from topic client-a-events: {...transformed JSON...}
```

---

## 🐳 Dockerized Setup

Make sure your Docker environment is ready.

```bash
chmod +x start-kafka-mongo.sh
./start-kafka-mongo.sh
```

Creates:
- Kafka Broker on `localhost:9092`
- MongoDB on `localhost:27017`
- Kafka Topics:
  - `incoming-events`
  - `client-a-events`
  - `client-b-events`
  - `error-events`

---

## 💡 Tips

- Make sure `clientTopic` exists in your schema JSON.
- Each component can run standalone or together as a suite.
- Use MongoDB Compass to inspect transformations.

---

## 📁 Folder Structure

```
kafkaesque-json-engine/
├── kafkaesque-producer/
├── kafkaesque-transformer/
├── kafkaesque-consumer/
├── docker-file/
└── README.md
```

---

## 🧪 Testing

You can publish test events via `kafkaesque-producer` or `kafka-console-producer`:

```bash
docker exec -it broker kafka-console-producer --broker-list localhost:9092 --topic incoming-events
> {"clientId":"client-a", "transactionId":"txn-123", ...}
```

Watch logs from:
- `kafkaesque-transformer` for transformation flow.
- `kafkaesque-consumer` for delivery to final topics.

---

Happy streaming! 🚀