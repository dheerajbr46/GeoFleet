# Learning Log

Use this file to record design decisions, experiments, failures, and surprises.

## Entries

### 2026-06-27

- Created initial scaffold for GeoFleet.
- Started with infrastructure-only Docker Compose.
- Kept `rider-service` free of core Redis, Kafka, dispatch, and nearby-search logic.
- Added thin PostgreSQL rider registration/read boilerplate while leaving distributed-systems logic as TODOs.
- Switched database schema ownership to Flyway after Hibernate `ddl-auto=update` failed on an existing table with a new non-null `rider_status` column.
