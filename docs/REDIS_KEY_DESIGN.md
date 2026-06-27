# Redis Key Design

This document captures planned Redis key patterns. Do not treat this as final implementation.

## Planned Areas

- Rider location indexing.
- Rider availability lookup.
- Lightweight rider state cache.
- Time-bound dispatch coordination experiments.
- Phase 1B live rider status stored as Redis Hash state.

## Candidate Key Names

```text
geo:riders:{city}
rider:{riderId}:status
rider:{riderId}:location
riders:available:{city}
rider:{riderId}:live
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

## Open Questions

- Should availability and location be separate keys?
- What TTL strategy prevents stale location from surviving too long?
- How should city partitioning evolve under high traffic?
