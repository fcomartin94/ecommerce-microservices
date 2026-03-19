# Como se hizo: Ecommerce Microservices (Spring Boot + Eureka + Gateway + JWT)

> Guía técnica paso a paso para reconstruir el backend desde cero. La idea es que
> puedas replicar la misma arquitectura y comportamiento funcional leyendo y siguiendo
> las decisiones del código: discovery con Eureka, routing con API Gateway, auth con JWT,
> stock con JPA/ PostgreSQL y “reserve” validando stock vía llamada entre servicios.

---

## Objetivo

Construir un backend con arquitectura de microservicios para un mini e-commerce:

- `eureka-server`: service discovery
- `api-gateway`: unifica entrada y rutea `/api/**`
- `user-service`: registro/login con JWT (H2 + JPA)
- `product-service`: catálogo + stock (PostgreSQL + JPA)
- `order-service`: reserva de pedido validando stock en `product-service`

---

## Requisitos

- JDK `17`
- Maven
- PostgreSQL para `product-service` (defaults):
  - `DB_HOST=localhost`
  - `DB_PORT=5432`
  - `DB_NAME=ecommerce`
  - `DB_USER=postgres`
  - `DB_PASSWORD=postgres`

---

## 1) Crear el proyecto multi-módulo (Maven)

Se usa un `pom.xml` raíz con `packaging: pom` y `modules`:

- `eureka-server`
- `api-gateway`
- `user-service`
- `product-service`
- `order-service`

En el pom raíz se fija:

- `java.version=17`
- `spring-cloud.version=2023.0.0`
- `spring-boot-starter-parent` en `3.2.0`

Checkpoint:

- `mvn -DskipTests package` desde la raíz (luego puedes levantar cada servicio por separado)

---

## 2) Servicio `eureka-server` (service discovery)

### Dependencias

- `spring-cloud-starter-netflix-eureka-server`
- `spring-boot-starter-web`

### Punto de entrada

En `EurekaServerApplication`:

- `@EnableEurekaServer`

### Configuración local

`application.yml` (resumen):

- `server.port=8761`
- `register-with-eureka=false`
- `fetch-registry=false`

Checkpoint:

- Visitar `http://localhost:8761` cuando esté arrancado

---

## 3) Servicio `api-gateway` (routing)

### Dependencias

- `spring-cloud-starter-gateway`
- `spring-cloud-starter-netflix-eureka-client`

### Configuración de rutas

En `application.yml` se declaran rutas por Path:

- Route `user-service`: `Path=/api/users/**` → `uri: lb://user-service`
- Route `product-service`: `Path=/api/products/**` → `uri: lb://product-service`
- Route `order-service`: `Path=/api/orders/**` → `uri: lb://order-service`

Además se activa:

- `spring.cloud.gateway.discovery.locator.enabled=true`
- `lower-case-service-id=true`

### Eureka config

El Gateway apunta a:

- `defaultZone: http://localhost:8761/eureka/`

Checkpoint:

- Si el Gateway arranca sin servicios registrados, suele aparecer `503`.

---

## 4) Servicio `user-service` (JWT + H2)

### Dependencias

- `spring-boot-starter-web`
- `spring-cloud-starter-netflix-eureka-client`
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-validation`
- `spring-boot-starter-security`
- `jjwt-api` + `jjwt-impl` + `jjwt-jackson`
- `h2` (runtime)
- `lombok` (provided, para builder/DTOs)

### Configuración

`application.yml`:

- `server.port=8081`
- H2 en memoria: `jdbc:h2:mem:usersdb`
- `ddl-auto: create-drop`
- `jwt.secret` (base64) y `jwt.expiration` (milisegundos)

### Modelo + persistencia

1) Entidad `User`:
   - `email` único (nullable=false)
   - `password` (nullable=false)
   - `name` (nullable=false)
   - `role` enum (persistido como string)

2) `UserRepository` extiende `JpaRepository<User, Long>` y expone:
   - `existsByEmail(email)`
   - `findByEmail(email)`

### DTOs con validación

- `RegisterRequest`:
  - `name` y `email` con `@NotBlank` + `@Email`
  - `password` con `@Size(min=6)`
- `LoginRequest`:
  - `email` con `@Email`
  - `password` con `@NotBlank`

### Security (stateless) + filtro JWT

- `SecurityConfig`:
  - CSRF disabled
  - `SessionCreationPolicy.STATELESS`
  - PermitAll a:
    - `/api/users/register`
    - `/api/users/login`
    - `/api/users/health`
    - `/h2-console/**`
  - `addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)`
  - encoder: `BCryptPasswordEncoder`

- `JwtService`:
  - firma con HS256 usando secret base64
  - `subject` = email del usuario

- `JwtAuthFilter`:
  - espera header `Authorization: Bearer <token>`
  - carga `UserDetails` y setea `SecurityContext` si el token es válido

### Lógica de negocio de auth

`AuthService`:

- `register`:
  - si ya existe email → error
  - guardar `passwordEncoder.encode(password)`
  - generar JWT y devolver `AuthResponse`
- `login`:
  - autenticar vía `AuthenticationManager`
  - generar JWT y devolver `AuthResponse`

### Controlador

`UserController`:

- base path: `/api/users`
- `POST /register` y `POST /login` (devuelven `AuthResponse {token,email,name,role}`)

Checkpoint:

- Probar:
  - `POST http://localhost:8080/api/users/register`
  - `POST http://localhost:8080/api/users/login`

---

## 5) Servicio `product-service` (catálogo + stock con PostgreSQL)

### Dependencias

- `spring-boot-starter-web`
- `spring-boot-starter-data-jpa`
- `spring-cloud-starter-netflix-eureka-client`
- `org.postgresql:postgresql` (runtime)

### Configuración

`application.yml`:

- `server.port=8082`
- datasource apuntando a `${DB_HOST}:${DB_PORT}/${DB_NAME}`
- `ddl-auto: create-drop`
- Hibernate dialect: PostgreSQL

### Modelo + persistencia

Entidad `Product`:

- `price: BigDecimal` (nullable=false)
- `stock: Integer` (nullable=false)

`ProductRepository` extiende `JpaRepository<Product, Long>`.

### Inicialización de datos

`ProductDataInitializer` (CommandLineRunner):

- si `count() > 0` → no hace nada
- si está vacío, guarda 2 productos:
  - Keyboard (79.99, stock 25)
  - Mouse (29.50, stock 40)

### Controlador / endpoints

`ProductController`:

- `GET /api/products` → lista `ProductDto {id,name,description,price,stock}`
- `GET /api/products/{id:[0-9]+}` → detalle por id
- `GET /api/products/stock/{productId}` → `StockResponse {productId, stock}`

Checkpoint:

- Consultar:
  - `GET http://localhost:8080/api/products`
  - `GET http://localhost:8080/api/products/stock/1`

---

## 6) Servicio `order-service` (reserve)

### Dependencias

- `spring-boot-starter-web`
- `spring-cloud-starter-netflix-eureka-client`
- `spring-cloud-starter-loadbalancer`

### Configuración

`application.yml`:

- `server.port=8083`
- eureka client `defaultZone` a `http://localhost:8761/eureka/`

### Cliente REST load-balanced

`RestClientConfig`:

- bean `RestTemplate` con `@LoadBalanced`

### Lógica de negocio

`OrderService.reserve()`:

1) Llama a `product-service` para obtener stock:
   - `http://product-service/api/products/stock/{productId}`
2) Si no hay producto/stock:
   - El código intenta lanzar `404` con `ResponseStatusException` si el response llega como `null`.
     Pero si el producto no existe, `product-service` lanza una `RuntimeException` (típicamente `500`),
     y `order-service` puede ver un error del `RestTemplate` en vez de pasar por el caso `null`.
3) Si `stock < quantity`:
   - `409`
4) Si hay stock:
   - calcula `remainingStock = stock - quantity`
   - devuelve `OrderReserveResponse`

Importante:

- En este estado no hay persistencia del “stock restante”.
  Se valida y se devuelve el cálculo, pero no actualiza la entidad en PostgreSQL.

### Controlador

`OrderController`:

- `POST /api/orders/reserve` body `OrderReserveRequest {productId, quantity}`
- devuelve `OrderReserveResponse`

Checkpoint:

- Probar:
  - `POST http://localhost:8080/api/orders/reserve` con `{ "productId": 1, "quantity": 2 }`
  - y con quantity grande para forzar `409`.

---

## 7) Startup local recomendado (5 terminals)

1) `eureka-server`
2) `user-service`
3) `product-service`
4) `order-service`
5) `api-gateway` (último)

Si arrancas el Gateway demasiado pronto, es común ver `503` hasta que Eureka registre instancias.

---

## 8) Qué destacar como “decisiones” (por qué está así)

- Eureka + `lb://...` para evitar IPs hardcodeadas.
- Gateway con predicates por Path para mantener contrato REST consistente (`/api/...`).
- JWT stateless en `user-service` con `JwtAuthFilter`, `JwtService` y `BCrypt` en credenciales.
- Persistencia real en `product-service` con JPA/Hibernate y seed de datos.
- Integración `order-service → product-service` usando `RestTemplate` `@LoadBalanced`.

