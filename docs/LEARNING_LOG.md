# Learning Log

Use this file to record design decisions, experiments, failures, and surprises.

## Entries

### 2026-06-27

- Created initial scaffold for GeoFleet.
- Started with infrastructure-only Docker Compose.
- Kept `rider-service` free of core Redis, Kafka, dispatch, and nearby-search logic.
- Added thin PostgreSQL rider registration/read boilerplate while leaving distributed-systems logic as TODOs.
- Switched database schema ownership to Flyway after Hibernate `ddl-auto=update` failed on an existing table with a new non-null `rider_status` column.
- Added Phase 1B live rider status API boilerplate with Redis Hash TODOs only.

## Phase 1B — Redis Hash for Rider Live State

### What I built

- Added PATCH /riders/{riderId}/status.
- Added GET /riders/{riderId}/live-state.
- Used Redis Hash to store live rider state.
- PostgreSQL validates that the rider exists.
- Redis stores fast-changing dispatch state.

### Redis key used

```text
rider:{riderId}
```

### Commands I used to inspect Redis
```text
HGETALL rider:1
HGET rider:1 status
TYPE rider:1
```