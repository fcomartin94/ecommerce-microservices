## Navegacion del repositorio (Monorepo)

Navegacion por idioma:

- Tecnico ES (este archivo, guia completa): `README_ES.md`
- Tecnico EN (traduccion equivalente): `README_EN.md`
- Recruiter ES (equivalente al EN): `OVERVIEW_ES.md`
- Recruiter EN (equivalente al ES): `OVERVIEW_EN.md`

Este repositorio es un **monorepo multi-modulo Maven**. Contiene cinco aplicaciones Spring Boot:

- `eureka-server`
- `api-gateway`
- `user-service`
- `product-service`
- `order-service`

Convencion documental:

- Guia tecnica raiz (EN): `README_EN.md`
- Guia tecnica raiz (ES): `README_ES.md` (par bilingue equivalente con la guia EN)
- Guia recruiter (ES): `OVERVIEW_ES.md`
- Guia recruiter (EN): `OVERVIEW_EN.md`
- Docs tecnicas por modulo: `README_TECH_EN.md` / `README_TECH_ES.md`
- Docs recruiter por modulo: `OVERVIEW_EN.md` / `OVERVIEW_ES.md`

Acceso rapido por modulo:

- `eureka-server`: `eureka-server/README_TECH_EN.md`, `eureka-server/README_TECH_ES.md`, `eureka-server/OVERVIEW_EN.md`, `eureka-server/OVERVIEW_ES.md`
- `api-gateway`: `api-gateway/README_TECH_EN.md`, `api-gateway/README_TECH_ES.md`, `api-gateway/OVERVIEW_EN.md`, `api-gateway/OVERVIEW_ES.md`
- `user-service`: `user-service/README_TECH_EN.md`, `user-service/README_TECH_ES.md`, `user-service/OVERVIEW_EN.md`, `user-service/OVERVIEW_ES.md`
- `product-service`: `product-service/README_TECH_EN.md`, `product-service/README_TECH_ES.md`, `product-service/OVERVIEW_EN.md`, `product-service/OVERVIEW_ES.md`
- `order-service`: `order-service/README_TECH_EN.md`, `order-service/README_TECH_ES.md`, `order-service/OVERVIEW_EN.md`, `order-service/OVERVIEW_ES.md`

GitHub Codespaces:

- Boton en raiz (fuente unica): `OVERVIEW_ES.md` y `OVERVIEW_EN.md`
- URL verificada con el remoto actual: `https://codespaces.new/fcomartin94/ecommerce-microservices`

---

# Mini E-Commerce (Microservicios con Spring Boot) (ES)

## Estado
Funciona de extremo a extremo:
- `user-service` (auth JWT, H2)
- `product-service` (catalogo + stock, PostgreSQL)
- `order-service` (reserva de pedido tras comprobar stock en `product-service`)
- `eureka-server` (descubrimiento de servicios)
- `api-gateway` (enruta peticiones a los servicios via Eureka)

Convencion de puertos:
- Eureka: `8761`
- API Gateway: `8080`
- user-service: `8081`
- product-service: `8082`
- order-service: `8083`

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
        v (HTTP via lb://product-service)
product-service (8082)
```

## Servicios / Endpoints (a traves del API Gateway)

Todos los ejemplos apuntan a:
`http://localhost:8080`

### Usuarios
Health:
```bash
curl http://localhost:8080/api/users/health
```

Registro (devuelve token JWT):
```bash
curl -s -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Juan","email":"juan@test.com","password":"123456"}'
```

Login (devuelve token JWT):
```bash
curl -s -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"email":"juan@test.com","password":"123456"}'
```

Forma JSON esperada:
```json
{
  "token": "...",
  "email": "juan@test.com",
  "name": "Juan",
  "role": "USER"
}
```

### Productos (catalogo) + Stock
Listar catalogo:
```bash
curl http://localhost:8080/api/products
```

Obtener por id (numerico):
```bash
curl http://localhost:8080/api/products/1
```

Endpoint de stock (usado por order-service):
```bash
curl http://localhost:8080/api/products/stock/1
```

Forma esperada:
```json
{ "productId": 1, "stock": 25 }
```

### Pedidos (reserva)
Reservar pedido (order-service comprueba stock en product-service):
```bash
curl -s -X POST http://localhost:8080/api/orders/reserve \
  -H "Content-Type: application/json" \
  -d '{"productId":1,"quantity":2}'
```

Respuesta esperada (ejemplo):
```json
{
  "productId": 1,
  "requestedQuantity": 2,
  "availableStock": 25,
  "remainingStock": 23
}
```

Stock insuficiente deberia devolver `409`:
```bash
curl -i -X POST http://localhost:8080/api/orders/reserve \
  -H "Content-Type: application/json" \
  -d '{"productId":1,"quantity":999}'
```

## Arranque (orden recomendado: 5 terminales)

### 0) Compilar (opcional)
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

### 5) Terminal E (ultimo): api-gateway
```bash
cd api-gateway
mvn spring-boot:run -DskipTests
```

## Configuracion PostgreSQL (product-service)

`product-service` lee la configuracion de BD desde variables de entorno (valores por defecto):
- `DB_HOST=localhost`
- `DB_PORT=5432`
- `DB_NAME=ecommerce`
- `DB_USER=postgres`
- `DB_PASSWORD=postgres`

Comprobaciones rapidas:
- Asegurate de que Postgres esta en marcha y la base de datos existe.
- El esquema lo crea Hibernate al arrancar (`ddl-auto: create-drop`).
- En el primer arranque, `product-service` inserta 2 productos de ejemplo (Teclado, Raton).

## Resolucion de problemas

### El API Gateway devuelve 503 "No servers available"
Causa tipica: el gateway arranco antes de que los servicios se registraran en Eureka.

Solucion:
- reinicia `api-gateway`
- o espera ~20-60 segundos y reintenta

### Los endpoints de producto no arrancan
Causa tipica: Postgres no es alcanzable o credenciales incorrectas.

Solucion:
- confirma host/puerto/usuario/password
- confirma que existe `DB_NAME`

## Que destacar en un portfolio
- Descubrimiento de servicios con Eureka (rutas `lb://...`)
- Enrutado con API Gateway a varios microservicios
- Autenticacion JWT en `user-service`
- `order-service` llamando a `product-service` para validar stock (llamada sincrona via balanceador)
- Producto/stock persistido en PostgreSQL con Hibernate/JPA

## Demo curl (copiar y pegar)

### 0) Confirmar que los servicios estan arriba
Health usuarios:
```bash
curl -i http://localhost:8080/api/users/health
```

Health productos (endpoint simple):
```bash
curl -i http://localhost:8080/api/products/health
```

### 1) Registro + Login (pasa por el API Gateway)
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

Claves esperadas en la respuesta:
- `token`
- `email`
- `name`
- `role`

### 2) Catalogo + Stock
El seed de BD crea 2 productos al arrancar:
- `id=1` (Teclado)
- `id=2` (Raton)

Listar productos:
```bash
curl -s http://localhost:8080/api/products
echo
```

Stock del producto `1`:
```bash
curl -s http://localhost:8080/api/products/stock/1
echo
```

### 3) Reservar pedido (order-service -> product-service)
Stock suficiente (esperar `200`):
```bash
curl -s -X POST http://localhost:8080/api/orders/reserve \
  -H "Content-Type: application/json" \
  -d '{"productId":1,"quantity":2}'
echo
```

Stock insuficiente (esperar `409`):
```bash
curl -s -i -X POST http://localhost:8080/api/orders/reserve \
  -H "Content-Type: application/json" \
  -d '{"productId":1,"quantity":999}'
```

## GitHub Codespaces (modo recruiter)
Si alguien abre el repo con "Open in Codespaces", puede levantar todo en 2 pasos:

1. En la terminal de Codespaces, ejecutar:
```bash
bash scripts/start-codespaces.sh
```

2. Esperar a que el script termine (comprueba health checks via API Gateway) y abrir:
- `http://localhost:8080` (API Gateway)

Endpoints rapidos para probar:
```bash
curl http://localhost:8080/api/users/health
curl http://localhost:8080/api/products
curl -s -X POST http://localhost:8080/api/orders/reserve \
  -H "Content-Type: application/json" \
  -d '{"productId":1,"quantity":2}'
```

Nota: `product-service` usa PostgreSQL en Docker; la configuracion (DB + JWT) viene por defecto en `devcontainer.json`.
