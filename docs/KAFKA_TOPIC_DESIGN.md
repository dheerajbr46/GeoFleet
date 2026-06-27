# Kafka Topic Design

Kafka is intentionally not implemented yet. This file is a planning surface for future event-driven experiments.

## Candidate Topics

```text
rider.registered
rider.status-updated
rider.location-updated
order.created
dispatch.assignment-requested
dispatch.assignment-completed
```

## Learning Questions

- Which events are facts versus commands?
- What keys preserve useful ordering?
- Which consumers need replayability?
- What happens when consumers lag during dispatch spikes?

