# Eureka Server - README tecnico (ES)

## Proposito

`eureka-server` provee descubrimiento de servicios para todos los modulos del monorepo.

## Runtime

- Nombre de servicio: `eureka-server`
- Puerto: `8761`
- Registro/fetch deshabilitado para si mismo

## Ejecucion local

```bash
cd eureka-server
mvn spring-boot:run -DskipTests
```

## Verificacion

- Abrir `http://localhost:8761`
- Confirmar registro de `api-gateway`, `user-service`, `product-service` y `order-service`

## Documentacion relacionada

- Version tecnica EN: `README_TECH_EN.md`
- Overview recruiter EN: `OVERVIEW_EN.md`
- Overview recruiter ES: `OVERVIEW_ES.md`
