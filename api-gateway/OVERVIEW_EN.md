# API Gateway - Recruiter Overview (EN)

`api-gateway` makes the project easy to consume: one public endpoint (`:8080`) that forwards calls to all microservices.

## Why it matters

- Demonstrates API gateway pattern in a microservices setup
- Uses Eureka-based load-balanced routing (`lb://...`)
- Keeps client apps decoupled from internal service topology

## Quick value statement

This module is the front door of the platform and centralizes routing for users, products, and orders.
