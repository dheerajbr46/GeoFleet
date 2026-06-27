# GeoFleet

GeoFleet is a learning-focused distributed systems project for exploring high-throughput, real-time parcel dispatch behavior with Spring Boot, PostgreSQL, Redis, Kafka, and load-oriented design.

The project is intentionally scaffold-first. Core business logic should be implemented gradually as part of the learning process.

## Current Scope

- `rider-service`: Spring Boot service skeleton for rider registration, status, location, and nearby rider learning flows.
- `docker-compose.yml`: infrastructure only, with PostgreSQL 16 and Redis 7.
- `docs/`: design notes and learning logs.

## Run Infrastructure

```bash
docker compose up -d
```

## Run Rider Service

```bash
cd services/rider-service
mvn spring-boot:run
```

## Health Check

```bash
curl http://localhost:8081/actuator/health
```
