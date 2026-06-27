# Redis Key Design

This document captures planned Redis key patterns. Do not treat this as final implementation.

## Planned Areas

- Rider location indexing.
- Rider availability lookup.
- Lightweight rider state cache.
- Time-bound dispatch coordination experiments.

## Candidate Key Names

```text
geo:riders:{city}
rider:{riderId}:status
rider:{riderId}:location
riders:available:{city}
```

## Open Questions

- Should availability and location be separate keys?
- What TTL strategy prevents stale location from surviving too long?
- How should city partitioning evolve under high traffic?

