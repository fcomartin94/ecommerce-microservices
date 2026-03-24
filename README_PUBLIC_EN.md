# Ecommerce - Spring Boot Microservices (EN)

[![Open in GitHub Codespaces](https://github.com/codespaces/badge.svg)](https://codespaces.new/fcomartin94/ecommerce-microservices)

Language (recruiter **equivalent** pair):
- EN (this file): `README_PUBLIC_EN.md`
- ES: `README_PUBLIC_ES.md`

Technical (bilingual **equivalent** pair):
- EN: `README_EN.md`
- ES: `README_ES.md`
- GitHub landing: `README.md`

> Mini e-commerce implementation using **microservices** (Spring Boot + Spring Cloud Gateway + Eureka) with real persistence in **PostgreSQL** (catalog/stock). Includes **JWT auth** in `user-service` and a "reserve order" flow that validates stock against `product-service`.

---

## Status

Working end-to-end (API Gateway -> services):

- `eureka-server` (service discovery)
- `api-gateway` (routing via `/api/**` routes)
- `user-service` (register/login with JWT, H2)
- `product-service` (catalog + stock in PostgreSQL)
- `order-service` (order reservation validating stock via call to `product-service`)

Ports (convention):

- Eureka: `8761`
- API Gateway: `8080`
- user-service: `8081`
- product-service: `8082`
- order-service: `8083`

---

## Architecture (high level)

```text
curl/Thunder Client
        |
        v
API Gateway (8080)
        |
        v (lb://order-service)
order-service (8083)
        |
        v (HTTP via lb://product-service)
product-service (8082)
```

---

## What each microservice does

- `api-gateway`
  - Exposes a single entry point at `http://localhost:8080`
  - Routes:
    - `/api/users/**` -> `user-service`
    - `/api/products/**` -> `product-service`
    - `/api/orders/**` -> `order-service`

- `eureka-server`
  - Eureka on `8761` so gateway/services can resolve `lb://...` URIs

- `user-service`
  - `POST /api/users/register`: creates user in H2 and returns `token` (JWT)
  - `POST /api/users/login`: validates credentials and returns `token` (JWT)
  - Stateless security with `JwtAuthFilter` (`Authorization: Bearer <token>`)

- `product-service`
  - Catalog:
    - `GET /api/products`
    - `GET /api/products/{id}`
  - Stock (used by `order-service`):
    - `GET /api/products/stock/{productId}`
  - Persistence with JPA/Hibernate in PostgreSQL
  - Seeds sample data (2 products) when table is empty

- `order-service`
  - `POST /api/orders/reserve`
  - Calls `product-service` to fetch stock
  - Responds with:
    - `200` and `remainingStock` if enough stock
    - `409` if stock is insufficient
    - If `productId` does not exist: `product-service` throws `RuntimeException` (typically `500`).
      `order-service` only emits `404` when `RestTemplate` returns `null` (specific edge case).

---

## Main endpoints (through API Gateway)

Base URL: `http://localhost:8080`

- Users
  - `POST /api/users/register`
  - `POST /api/users/login`
  - `GET  /api/users/health`

- Products
  - `GET /api/products`
  - `GET /api/products/{id}`
  - `GET /api/products/stock/{productId}`

- Orders
  - `POST /api/orders/reserve`

---

## Technical highlights for portfolio

- Eureka service discovery (`lb://...`) + Spring Cloud Gateway routing
- JWT auth in `user-service` with:
  - `BCryptPasswordEncoder`
  - `JwtAuthFilter` + `JwtService` (HS256, configurable expiration)
  - `jakarta.validation` DTO validation for register/login
- Catalog and stock persisted with JPA/Hibernate + PostgreSQL
- Inter-service integration using `@LoadBalanced RestTemplate` and service names

---

## Quick local startup

Requirement:

- PostgreSQL running for `product-service` (defaults: `DB_HOST=localhost`, `DB_PORT=5432`, `DB_NAME=ecommerce`, `DB_USER=postgres`, `DB_PASSWORD=postgres`)

Recommended startup order (5 terminals):

```bash
# 1) Eureka
cd eureka-server
mvn spring-boot:run -DskipTests

# 2) user-service
cd ../user-service
mvn spring-boot:run -DskipTests

# 3) product-service
cd ../product-service
mvn spring-boot:run -DskipTests

# 4) order-service
cd ../order-service
mvn spring-boot:run -DskipTests

# 5) api-gateway (last)
cd ../api-gateway
mvn spring-boot:run -DskipTests
```

---

## Note

The repo includes:

- `README_EN.md` / `README_ES.md` for full operational guides (curl + troubleshooting), bilingual equivalents
- `README_PUBLIC_ES.md` as the Spanish recruiter overview (equivalent to this file)

Module overviews (recruiter):
- `api-gateway/OVERVIEW_EN.md`
- `eureka-server/OVERVIEW_EN.md`
- `user-service/OVERVIEW_EN.md`
- `product-service/OVERVIEW_EN.md`
- `order-service/OVERVIEW_EN.md`

---

## GitHub Codespaces (recruiter mode)

If a recruiter uses "Open in Codespaces", they only need to:

- Open terminal in Codespaces and run:

  ```bash
  bash scripts/start-codespaces.sh
  ```

- Then test in browser:

  - `http://localhost:8080`

The script starts PostgreSQL in Docker and runs all microservices. Relevant ports (including `8080`) are auto-forwarded by `devcontainer.json`.
