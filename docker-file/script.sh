#!/bin/bash

set -e

echo "🧹 Cleaning up old containers (if any)..."
docker rm -f broker kafka mongo zookeeper 2>/dev/null || true

echo "📦 Generating docker-compose.yml..."
cat > docker-compose.yml <<EOF
version: '3.8'

services:
  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    container_name: zookeeper
    ports:
      - "2181:2181"
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000

  broker:
    image: confluentinc/cp-kafka:latest
    container_name: broker
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS: 0
      KAFKA_AUTO_CREATE_TOPICS_ENABLE: "false"

  mongo:
    image: mongo:6.0
    container_name: mongo
    ports:
      - "27017:27017"
    volumes:
      - mongo_data:/data/db

volumes:
  mongo_data:
EOF

echo "🚀 Starting Kafka + MongoDB containers..."
docker-compose up -d

echo "⏳ Waiting for Kafka to be ready..."
sleep 15  # Wait a bit for Kafka to fully start

echo "🔄 Creating Kafka topic: incoming-events..."
docker exec -it broker kafka-topics --create \
  --bootstrap-server localhost:9092 \
  --topic incoming-events \
  --partitions 3 \
  --replication-factor 1 || echo "Topic 'incoming-events' may already exist"

docker exec -it broker kafka-topics --create \
  --bootstrap-server localhost:9092 \
  --topic client-a-events \
  --partitions 3 \
  --replication-factor 1 || echo "Topic 'client-a-events' may already exist"

docker exec -it broker kafka-topics --create \
  --bootstrap-server localhost:9092 \
  --topic client-b-events \
  --partitions 3 \
  --replication-factor 1 || echo "Topic 'client-b-events' may already exist"

docker exec -it broker kafka-topics --create \
  --bootstrap-server localhost:9092 \
  --topic error-events \
  --partitions 1 \
  --replication-factor 1 || echo "Topic 'error-events' may already exist"

echo "✅ Setup complete!"
echo "➡️ Kafka Broker: PLAINTEXT at localhost:9092"
echo "➡️ MongoDB: mongodb://localhost:27017"

