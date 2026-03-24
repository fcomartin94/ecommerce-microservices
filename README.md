## Repository Navigation (Monorepo)

This repository is a **Maven multi-module monorepo**. It contains five Spring Boot apps:

- `eureka-server`
- `api-gateway`
- `user-service`
- `product-service`
- `order-service`

Documentation convention used in this repo:

- Root technical guide (EN): `README.md`
- Root technical guide (ES): `README_ES.md`
- Root recruiter overview (ES): `README_PUBLIC.md`
- Root recruiter overview (EN): `README_PUBLIC_EN.md`
- Per-module technical docs: `README_TECH_EN.md` / `README_TECH_ES.md`
- Per-module recruiter docs: `OVERVIEW_EN.md` / `OVERVIEW_ES.md`

Quick access by module:

- `eureka-server`: `eureka-server/README_TECH_EN.md`, `eureka-server/README_TECH_ES.md`, `eureka-server/OVERVIEW_EN.md`, `eureka-server/OVERVIEW_ES.md`
- `api-gateway`: `api-gateway/README_TECH_EN.md`, `api-gateway/README_TECH_ES.md`, `api-gateway/OVERVIEW_EN.md`, `api-gateway/OVERVIEW_ES.md`
- `user-service`: `user-service/README_TECH_EN.md`, `user-service/README_TECH_ES.md`, `user-service/OVERVIEW_EN.md`, `user-service/OVERVIEW_ES.md`
- `product-service`: `product-service/README_TECH_EN.md`, `product-service/README_TECH_ES.md`, `product-service/OVERVIEW_EN.md`, `product-service/OVERVIEW_ES.md`
- `order-service`: `order-service/README_TECH_EN.md`, `order-service/README_TECH_ES.md`, `order-service/OVERVIEW_EN.md`, `order-service/OVERVIEW_ES.md`

GitHub Codespaces:

- Root-level button (single source of truth): `README_PUBLIC.md` and `README_PUBLIC_EN.md`
- Target URL verified against current remote repository: `https://codespaces.new/fcomartin94/ecommerce-microservices`

---

# Mini E-Commerce (Microservices with Spring Boot)

## Status
Working end-to-end:
- `user-service` (JWT auth, H2)
- `product-service` (catalog + stock, PostgreSQL)
- `order-service` (reserve order after checking stock in `product-service`)
- `eureka-server` (service discovery)
- `api-gateway` (routes requests to services via Eureka)

Port convention:
- Eureka: `8761`
- API Gateway: `8080`
- user-service: `8081`
- product-service: `8082`
- order-service: `8083`

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

## Services / Endpoints (through the API Gateway)

All the examples below target:
`http://localhost:8080`

### Users
Health:
```bash
curl http://localhost:8080/api/users/health
```

Register (returns JWT token):
```bash
curl -s -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Juan","email":"juan@test.com","password":"123456"}'
```

Login (returns JWT token):
```bash
curl -s -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"email":"juan@test.com","password":"123456"}'
```

Expected JSON shape:
```json
{
  "token": "...",
  "email": "juan@test.com",
  "name": "Juan",
  "role": "USER"
}
```

### Products (catalog) + Stock
List catalog:
```bash
curl http://localhost:8080/api/products
```

Get by id (numeric):
```bash
curl http://localhost:8080/api/products/1
```

Stock endpoint (used by order-service):
```bash
curl http://localhost:8080/api/products/stock/1
```

Expected shape:
```json
{ "productId": 1, "stock": 25 }
```

### Orders (reserve)
Reserve order (order-service checks stock in product-service):
```bash
curl -s -X POST http://localhost:8080/api/orders/reserve \
  -H "Content-Type: application/json" \
  -d '{"productId":1,"quantity":2}'
```

Expected response (example):
```json
{
  "productId": 1,
  "requestedQuantity": 2,
  "availableStock": 25,
  "remainingStock": 23
}
```

Insufficient stock should return `409`:
```bash
curl -i -X POST http://localhost:8080/api/orders/reserve \
  -H "Content-Type: application/json" \
  -d '{"productId":1,"quantity":999}'
```

## Startup (recommended order: 5 terminals)

### 0) Build (optional)
```bash
cd ecommerce-microservices
mvn -DskipTests package
```

### 1) Terminal A: eureka-server
```bash
cd eureka-server
mvn spring-boot:run -DskipTests
```

### 2) Terminal B: user-service
```bash
cd user-service
mvn spring-boot:run -DskipTests
```

### 3) Terminal C: product-service
```bash
cd product-service
mvn spring-boot:run -DskipTests
```

### 4) Terminal D: order-service
```bash
cd order-service
mvn spring-boot:run -DskipTests
```

### 5) Terminal E (last): api-gateway
```bash
cd api-gateway
mvn spring-boot:run -DskipTests
```

## PostgreSQL configuration (product-service)

`product-service` reads DB settings from environment variables (defaults shown):
- `DB_HOST=localhost`
- `DB_PORT=5432`
- `DB_NAME=ecommerce`
- `DB_USER=postgres`
- `DB_PASSWORD=postgres`

Quick checks:
- Ensure Postgres is running and the database exists.
- The schema is created by Hibernate on startup (`ddl-auto: create-drop`).
- On first run, `product-service` seeds 2 products (Keyboard, Mouse).

## Troubleshooting

### API Gateway returns 503 "No servers available"
Typical cause: the gateway started before services were registered in Eureka.

Fix:
- restart `api-gateway`
- or wait ~20-60 seconds and retry

### Product endpoints fail to start
Typical cause: Postgres is not reachable or user/db credentials mismatch.

Fix:
- confirm host/port/user/password
- confirm `DB_NAME` exists

## What to highlight in a portfolio
- Eureka-based service discovery (`lb://...` routes)
- API Gateway routing to multiple microservices
- JWT authentication in `user-service`
- `order-service` calling `product-service` to validate stock (sync call via load balancer)
- Persisted product/stock in PostgreSQL with Hibernate/JPA

## Curl demo (copy/paste)

### 0) Confirm services are up
User health:
```bash
curl -i http://localhost:8080/api/users/health
```

Products health (endpoint simple):
```bash
curl -i http://localhost:8080/api/products/health
```

### 1) Register + Login (goes through the API Gateway)
```bash
curl -s -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Juan","email":"juan@test.com","password":"123456"}'
echo

curl -s -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"email":"juan@test.com","password":"123456"}'
echo
```

Expected response keys:
- `token`
- `email`
- `name`
- `role`

### 2) Catalog + Stock
The DB seed creates 2 products on startup:
- `id=1` (Keyboard)
- `id=2` (Mouse)

List products:
```bash
curl -s http://localhost:8080/api/products
echo
```

Stock for product `1`:
```bash
curl -s http://localhost:8080/api/products/stock/1
echo
```

### 3) Reserve order (order-service -> product-service)
Enough stock (expect `200`):
```bash
curl -s -X POST http://localhost:8080/api/orders/reserve \
  -H "Content-Type: application/json" \
  -d '{"productId":1,"quantity":2}'
echo
```

Insufficient stock (expect `409`):
```bash
curl -s -i -X POST http://localhost:8080/api/orders/reserve \
  -H "Content-Type: application/json" \
  -d '{"productId":1,"quantity":999}'
```

## GitHub Codespaces (modo recruiter)
Si el recruiter abre tu repo como “Open in Codespaces”, puede levantar todo con 2 pasos:

1. En la terminal de Codespaces, ejecutar:
```bash
bash scripts/start-codespaces.sh
```

2. Esperar a que el script termine (verifica health checks vía el API Gateway) y abrir:
- `http://localhost:8080` (API Gateway)

Endpoints rápidos para probar:
```bash
curl http://localhost:8080/api/users/health
curl http://localhost:8080/api/products
curl -s -X POST http://localhost:8080/api/orders/reserve \
  -H "Content-Type: application/json" \
  -d '{"productId":1,"quantity":2}'
```

Nota: `product-service` usa PostgreSQL en Docker y la configuración (DB + JWT) está puesta por defecto en el `devcontainer.json`.

