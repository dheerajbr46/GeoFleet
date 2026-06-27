# Redis Key Design

This document captures planned Redis key patterns. Do not treat this as final implementation.

## Planned Areas

- Rider location indexing.
- Rider availability lookup.
- Lightweight rider state cache.
- Time-bound dispatch coordination experiments.
- Phase 1B live rider status stored as Redis Hash state.
- Phase 1C rider location indexing stored in Redis GEO by city.

## Candidate Key Names

```text
rider:{riderId}:status
rider:{riderId}:location
riders:available:{city}
rider:{riderId}:live
riders:geo:{city}
```

## Phase 1B Live State

`rider:{riderId}:live` is reserved for rider dispatch-facing live state.

Planned hash fields:

```text
status
updatedAt
```

Allowed rider-set statuses in Phase 1B:

```text
AVAILABLE
OFFLINE
```

Reserved statuses:

```text
BUSY       # dispatch logic later
SUSPENDED  # admin/account logic later
```

## Phase 1C GEO Location

`riders:geo:{city}` is reserved for Redis GEO rider location indexing.

Rules:

- City comes from the PostgreSQL rider profile during location update.
- Nearby search should eventually combine Redis GEO results with Redis Hash live state.
- Nearby search should eventually return only `AVAILABLE` and fresh riders.
- Redis GEO write/read implementation is intentionally manual learning work.

## Open Questions

- Should availability and location be separate keys?
- What TTL strategy prevents stale location from surviving too long?
- How should city partitioning evolve under high traffic?
