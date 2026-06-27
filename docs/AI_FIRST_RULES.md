# AI First Rules

GeoFleet is a learning project. AI can help scaffold, explain, review, and generate small examples, but the core implementation should be written by the learner.

## Rules

- Prefer scaffolding over finished business logic.
- Keep service boundaries explicit.
- Add learning notes before adding complexity.
- Use AI to ask "why does this design fail under load?"
- Avoid hiding important distributed systems tradeoffs behind abstractions too early.
- Apply best practices incrementally as the project grows.
- Version APIs from the first endpoint using `/api/v1/...`.
- Do not add unversioned HTTP routes.

## Do Not Auto-Implement

- Redis GEO search logic.
- Redis hash models.
- Kafka producers or consumers.
- Dispatch or assignment algorithms.
- Cross-service orchestration.

## Ownership Split

AI may generate:
- Controllers
- DTOs
- Entity skeletons
- Repository interfaces
- Basic CRUD scaffolding
- Exception handling boilerplate
- Configuration files
- Documentation templates
- Test skeletons

I must manually implement and understand:
- Redis key design
- Redis Hash usage
- Redis GEO usage
- TTL and freshness handling
- Idempotency
- Rate limiting
- Distributed locks
- Lua scripts
- Kafka partitioning
- Kafka consumers/producers
- Retry and DLQ handling
- Outbox pattern
- Load testing
- Failure testing
- Performance tuning
