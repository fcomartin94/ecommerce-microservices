# User Service - Technical README (EN)

## Purpose

`user-service` handles authentication and user account access.

## Runtime

- Service name: `user-service`
- Port: `8081`
- Database: H2 in-memory (`jdbc:h2:mem:usersdb`)
- Security: JWT-based stateless auth

## Main endpoints

- `POST /api/users/register`
- `POST /api/users/login`
- `GET /api/users/health`

## Local run

```bash
cd user-service
mvn spring-boot:run -DskipTests
```

## Related docs

- Spanish technical version: `README_TECH_ES.md`
- Recruiter overview (EN): `OVERVIEW_EN.md`
- Recruiter overview (ES): `OVERVIEW_ES.md`
