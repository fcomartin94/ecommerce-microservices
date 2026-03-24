# API Gateway - Recruiter Overview (EN)

`api-gateway` is the platform entry point: one public URL (`:8080`) that routes traffic to all backend services.

## Recruiter snapshot

- Pattern: API Gateway in a real microservices setup
- Outcome: simpler client integration and cleaner service boundaries
- Reliability signal: discovery-aware routing through Eureka (`lb://...`)

## Business value

This module centralizes traffic for users, products, and orders, reducing integration friction and making the platform easier to scale.
