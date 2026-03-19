# Ecommerce — Microservices con Spring Boot

> Implementación de un mini e-commerce con **microservicios** (Spring Boot + Spring Cloud Gateway + Eureka) y persistencia real en **PostgreSQL** (catálogo/stock). Incluye **JWT auth** en `user-service` y flujo de negocio de “reservar pedido” validando stock contra `product-service`.

---

## Status

Funciona end-to-end (API Gateway → servicios) con:

- `eureka-server` (service discovery)
- `api-gateway` (enrutado por rutas `/api/**`)
- `user-service` (registro/login con JWT, H2)
- `product-service` (catálogo + stock en PostgreSQL)
- `order-service` (reserva pedido validando stock via call a `product-service`)

Puertos (convención):

- Eureka: `8761`
- API Gateway: `8080`
- user-service: `8081`
- product-service: `8082`
- order-service: `8083`

---

## Arquitectura (alto nivel)

```text
curl/Thunder Client
        |
        v
API Gateway (8080)
        |
        v (lb://order-service)
order-service (8083)
        |
        v (HTTP vía lb://product-service)
product-service (8082)
```

---

## Qué hace cada microservicio

- `api-gateway`
  - Expone un único punto de entrada en `http://localhost:8080`
  - Rutea:
    - `/api/users/**` → `user-service`
    - `/api/products/**` → `product-service`
    - `/api/orders/**` → `order-service`

- `eureka-server`
  - Eureka en `8761` para que Gateway y servicios resuelvan URIs con `lb://...`

- `user-service`
  - `POST /api/users/register`: crea usuario en H2 y devuelve `token` (JWT)
  - `POST /api/users/login`: valida credenciales y devuelve `token` (JWT)
  - Seguridad stateless con filtro `JwtAuthFilter` (Authorization: `Bearer <token>`)

- `product-service`
  - Catálogo:
    - `GET /api/products`
    - `GET /api/products/{id}`
  - Stock (para `order-service`):
    - `GET /api/products/stock/{productId}`
  - Persistencia con JPA/ Hibernate en PostgreSQL
  - Inicializa datos de ejemplo (2 productos) si la tabla está vacía

- `order-service`
  - `POST /api/orders/reserve`
  - Llama a `product-service` para consultar stock
  - Responde:
    - `200` con `remainingStock` si hay stock suficiente
    - `409` si el stock es insuficiente
    - Si el `productId` no existe: `product-service` lanza `RuntimeException` (típicamente `500`).
      `order-service` solo llega a lanzar `404` cuando el `RestTemplate` devuelve `null` (caso particular).

---

## Endpoints principales (vía API Gateway)

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

## Decisiones técnicas para destacar en portfolio

- Service discovery con Eureka (`lb://...`) y routing con Spring Cloud Gateway
- JWT auth en `user-service` con:
  - `BCryptPasswordEncoder`
  - `JwtAuthFilter` + `JwtService` (HS256, expiración configurable)
  - DTOs con validación `jakarta.validation` para registro/login
- Stock y catálogo persistidos con JPA/Hibernate en PostgreSQL
- Integración entre microservicios con `RestTemplate` `@LoadBalanced` para llamar por nombre de servicio

---

## Cómo levantarlo rápido (local)

Requisito:

- Tener PostgreSQL activo para `product-service` (defaults: `DB_HOST=localhost`, `DB_PORT=5432`, `DB_NAME=ecommerce`, `DB_USER=postgres`, `DB_PASSWORD=postgres`)

Startup recomendado (5 terminals):

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

# 5) api-gateway (último)
cd ../api-gateway
mvn spring-boot:run -DskipTests
```

---

## Nota

El repo tiene un `README.md` con instrucciones más “operativas” (curl y troubleshooting). Este `README_PUBLIC.md` está pensado para recruiter/portfolio.

---

## GitHub Codespaces (recruiter)
Si el recruiter usa “Open in Codespaces”, basta con:

1. Abrir la terminal en Codespaces y ejecutar:
```bash
bash scripts/start-codespaces.sh
```

2. Probar en el navegador:
- `http://localhost:8080`

El script levanta PostgreSQL en Docker y arranca todos los microservicios. Los puertos relevantes (incl. `8080`) quedan forwarded automáticamente por el `devcontainer.json`.

