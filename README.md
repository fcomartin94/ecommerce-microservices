# Mini E-Commerce — Spring Boot Microservices

> Full microservices backend with **Spring Boot + Spring Cloud Gateway + Eureka**, real **PostgreSQL** persistence, **JWT authentication**, and inter-service communication. Five independent services working end-to-end through a single API Gateway entry point.

---

## Architecture

```text
Client (curl / Postman / browser)
        |
        v
API Gateway :8080  (Spring Cloud Gateway + Eureka routing)
    |           |           |
    v           v           v
user-service  product-service  order-service
  :8081          :8082           :8083
                                   |
                                   v (lb://product-service)
                             product-service (stock check)

eureka-server :8761  ←  all services register here
```

## Services

| Service | Port | Purpose |
|---------|------|---------|
| `eureka-server` | 8761 | Service discovery — all `lb://` routes resolve here |
| `api-gateway` | 8080 | Single public entry point; routes `/api/**` to services |
| `user-service` | 8081 | Register / login, issues **JWT** tokens (H2) |
| `product-service` | 8082 | Product catalog + stock in **PostgreSQL** |
| `order-service` | 8083 | Reserve order after validating stock against `product-service` |

## Key technical highlights

- **Eureka service discovery** — services communicate by name (`lb://product-service`), no hardcoded hosts
- **Spring Cloud Gateway** routing from a single public port
- **JWT auth** in `user-service` with `BCryptPasswordEncoder`, `JwtService` (HS256), and `JwtAuthFilter`
- **PostgreSQL + Hibernate/JPA** for persistent catalog and stock in `product-service`
- **Sync inter-service call** — `order-service` calls `product-service` via `@LoadBalanced RestTemplate`
- **Conflict response** — `409` when stock is insufficient, `200` with `remainingStock` on success

## Quick start (5 terminals)

```bash
# 1. Eureka first
cd eureka-server && mvn spring-boot:run -DskipTests

# 2. user-service
cd user-service && mvn spring-boot:run -DskipTests

# 3. product-service  (needs PostgreSQL — see below)
cd product-service && mvn spring-boot:run -DskipTests

# 4. order-service
cd order-service && mvn spring-boot:run -DskipTests

# 5. api-gateway last
cd api-gateway && mvn spring-boot:run -DskipTests
```

PostgreSQL defaults for `product-service`: `DB_HOST=localhost`, `DB_PORT=5432`, `DB_NAME=ecommerce`, `DB_USER=postgres`, `DB_PASSWORD=postgres`.

## Try it

```bash
# Register and get a JWT
curl -s -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Alice","email":"alice@test.com","password":"secret"}'

# List products (seeded on startup: Keyboard, Mouse)
curl http://localhost:8080/api/products

# Reserve an order (validates stock in product-service)
curl -s -X POST http://localhost:8080/api/orders/reserve \
  -H "Content-Type: application/json" \
  -d '{"productId":1,"quantity":2}'
```

## License

MIT
