# Order Service - Recruiter Overview (EN)

`order-service` represents business orchestration: it checks stock and returns reservation outcomes.

## Why it matters

- Demonstrates service-to-service communication
- Encodes a concrete business rule (reserve only if stock is enough)
- Returns explicit outcomes for success (`200`) and conflict (`409`)
