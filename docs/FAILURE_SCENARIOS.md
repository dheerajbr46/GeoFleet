# Failure Scenarios

Use this document to design experiments before implementing recovery behavior.

## Redis

- Redis is unavailable while location updates arrive.
- Redis contains stale rider location.
- Redis eviction removes rider availability data.

## PostgreSQL

- Database is slow under write-heavy rider registration.
- Connection pool is exhausted.
- Transaction succeeds but downstream cache update fails.

## Kafka

- Producer cannot publish an event after a database write.
- Consumer is delayed or reprocesses an event.
- Topic partitioning causes unexpected hot partitions.

## Service Behavior

- Rider service restarts during high request volume.
- Multiple updates for the same rider arrive out of order.
- Health checks pass while a dependency is degraded.

