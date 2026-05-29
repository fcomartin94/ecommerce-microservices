# API Gateway - Technical README (EN)

## Purpose

`api-gateway` is the single HTTP entry point for clients. It routes traffic to internal services using Eureka service discovery.

## Runtime

- Service name: `api-gateway`
- Port: `8080`
- Discovery: Eureka (`http://localhost:8761/eureka/`)

## Routes

- `/api/users/**` -> `lb://user-service`
- `/api/products/**` -> `lb://product-service`
- `/api/orders/**` -> `lb://order-service`

## Local run

```bash
cd api-gateway
mvn spring-boot:run -DskipTests
```

## Related docs

- Overview: [`OVERVIEW_EN.md`](OVERVIEW_EN.md)
- Monorepo: [`../README_EN.md`](../README_EN.md)
