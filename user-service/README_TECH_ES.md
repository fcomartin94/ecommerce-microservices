# User Service - README tecnico (ES)

## Proposito

`user-service` gestiona autenticacion y acceso de cuentas de usuario.

## Runtime

- Nombre de servicio: `user-service`
- Puerto: `8081`
- Base de datos: H2 en memoria (`jdbc:h2:mem:usersdb`)
- Seguridad: autenticacion stateless con JWT

## Endpoints principales

- `POST /api/users/register`
- `POST /api/users/login`
- `GET /api/users/health`

## Ejecucion local

```bash
cd user-service
mvn spring-boot:run -DskipTests
```

## Documentacion relacionada

- Version tecnica EN: `README_TECH_EN.md`
- Overview recruiter EN: `OVERVIEW_EN.md`
- Overview recruiter ES: `OVERVIEW_ES.md`
