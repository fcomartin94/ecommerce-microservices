# Order Service - Recruiter Overview (EN)

`order-service` is the business orchestration layer for reservation flow.

## Recruiter snapshot

- Pattern: service-to-service orchestration with explicit business rules
- Product signal: reserve only when stock is sufficient
- API signal: clear outcomes for success (`200`) and conflict (`409`)

## Business value

This module turns stock validation into a predictable order decision, improving reliability for checkout-like scenarios.
