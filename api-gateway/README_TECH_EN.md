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

- Spanish technical version: `README_TECH_ES.md`
- Recruiter overview (EN): `OVERVIEW_EN.md`
- Recruiter overview (ES): `OVERVIEW_ES.md`
