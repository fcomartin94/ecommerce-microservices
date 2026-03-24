# Eureka Server - Technical README (EN)

## Purpose

`eureka-server` provides service discovery for all modules in this monorepo.

## Runtime

- Service name: `eureka-server`
- Port: `8761`
- Registration/fetch disabled for itself

## Local run

```bash
cd eureka-server
mvn spring-boot:run -DskipTests
```

## Verification

- Open `http://localhost:8761`
- Confirm `api-gateway`, `user-service`, `product-service`, and `order-service` register correctly

## Related docs

- Spanish technical version: `README_TECH_ES.md`
- Recruiter overview (EN): `OVERVIEW_EN.md`
- Recruiter overview (ES): `OVERVIEW_ES.md`
