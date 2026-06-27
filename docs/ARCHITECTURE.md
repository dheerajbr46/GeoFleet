# Architecture

GeoFleet is planned as a distributed parcel dispatch platform made of small Spring Boot services and shared infrastructure.

## Current Services

- `rider-service`: Owns rider identity, rider status, and rider location learning flows.

## Planned Services

- `order-service`: Will own parcel/order lifecycle.
- `tracking-service`: Will own real-time tracking projections.
- `dispatch-service`: Will coordinate rider assignment experiments.

## Current Infrastructure

- PostgreSQL 16 for durable relational state.
- Redis 7 for future real-time geospatial and low-latency state experiments.

## API Versioning

- All HTTP APIs must be versioned from the first endpoint.
- Use path-based versioning: `/api/v1/...`.
- `rider-service` starts with `/api/v1/riders`.
- Do not introduce unversioned API routes like `/api/riders`.
- Breaking contract changes should move to a future path such as `/api/v2/...`.

## Engineering Practices

- Keep PostgreSQL as the source of truth for durable business entities.
- Keep Redis for future high-churn, low-latency, and disposable operational state.
- Keep controller methods thin: validate input, call the service, return HTTP responses.
- Keep distributed-systems learning logic visible and manually implemented.
- Add TODO comments where Redis, Kafka, locking, Lua, idempotency, or load behavior will be implemented manually.

## Learning Questions

- Which state belongs in PostgreSQL versus Redis?
- What consistency guarantees are required for dispatch?
- How does the system behave when Redis is stale or unavailable?
- What data should be event-driven through Kafka later?
