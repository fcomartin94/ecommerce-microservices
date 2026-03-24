# API Gateway - README tecnico (ES)

## Proposito

`api-gateway` es el punto de entrada HTTP unico para clientes. Enruta trafico a servicios internos usando descubrimiento por Eureka.

## Runtime

- Nombre de servicio: `api-gateway`
- Puerto: `8080`
- Discovery: Eureka (`http://localhost:8761/eureka/`)

## Rutas

- `/api/users/**` -> `lb://user-service`
- `/api/products/**` -> `lb://product-service`
- `/api/orders/**` -> `lb://order-service`

## Ejecucion local

```bash
cd api-gateway
mvn spring-boot:run -DskipTests
```

## Documentacion relacionada

- Version tecnica EN: `README_TECH_EN.md`
- Overview recruiter EN: `OVERVIEW_EN.md`
- Overview recruiter ES: `OVERVIEW_ES.md`
