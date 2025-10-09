# Fastshop Backend [![CI](https://github.com/AlbertoVilar/fastshop-backend/actions/workflows/ci.yml/badge.svg)](https://github.com/AlbertoVilar/fastshop-backend/actions/workflows/ci.yml)

Fastshop is a backend API for an e-commerce-style system. It supports product and category management, customer registration and updates, carts and orders flows, secured authentication using JWT, persistence with PostgreSQL, migrations via Flyway, and visibility through Spring Boot Actuator. Run locally with Docker Compose in minutes.

## Overview
- Framework: Spring Boot `3.5.x` (Java `21`).
- Persistence: JPA/Hibernate with PostgreSQL (prod) and H2 (dev/test runtime option).
- Migrations: Flyway.
- Security: Spring Security and JWT (JJWT).
- Observability: Spring Boot Actuator (`/actuator/health`).
- Containerization: Docker (`eclipse-temurin:21-jre-alpine`).
- Orchestration: Docker Compose with `db` (Postgres) and `app`.

## Architecture (high level)
- Typical layers: controllers → services → repositories (DTOs, Bean Validation).
- Error handling: global handler returns standardized validation error payloads.
- Profiles: `SPRING_PROFILES_ACTIVE=prod` (Compose) for Postgres.
- Healthcheck: `/actuator/health` validated in container using `wget` (Alpine-friendly).

## Prerequisites
- `Java 21` and `Maven` (optional for non-Docker runs).
- `Docker` and `Docker Compose`.

## Quickstart with Docker Compose
1. Start services:
   - Windows (PowerShell): `docker compose up -d`
2. Check app health:
   - `Invoke-WebRequest http://localhost:8080/actuator/health` → should return `{"status":"UP"}`
3. Check application logs:
   - `docker compose logs app --tail 120`

Compose highlights (`compose.yml`):
- App port: `8080` on host.
- DB port: `5432` on host.
- App healthcheck: `wget -qO- http://localhost:8080/actuator/health`.
- Restart policy: `on-failure` (app) and `always` (db).

## Key Endpoints
- Authentication
  - `POST /auth/login` — example body:
    ```json
    {"username":"albertovilar1@gmail.com","password":"132747"}
    ```
    - Response: `200 OK` with `accessToken` (JWT). Use `Authorization: Bearer <token>` for protected requests.
- Products (`/products`)
  - `GET /products` — list products (public)
  - `GET /products/{id}` — get by id (public)
  - `POST /products` — create (ROLE_ADMIN)
  - `PUT /products/{id}` — update (ROLE_ADMIN)
  - `DELETE /products/{id}` — delete (ROLE_ADMIN)
- Categories (`/categories`)
  - `GET /categories` — list categories (public)
  - `GET /categories/{id}` — get by id (public)
  - `POST /categories` — create (ROLE_ADMIN)
  - `PUT /categories/{id}` — update (ROLE_ADMIN)
  - `DELETE /categories/{id}` — delete (ROLE_ADMIN)
- Customers (`/customers`)
  - `POST /customers` — register (public)
  - `GET /customers` — list customers (authenticated)
  - `GET /customers/{id}` — get by id (authenticated)
  - `PUT /customers/{id}` — update (authenticated)
  - `DELETE /customers/{id}` — delete (authenticated)
- Carts (`/carts`)
  - `POST /carts` — create cart (public)
  - `GET /carts` — list carts (public)
  - `GET /carts/{id}` — get cart by id (public)
  - `PUT /carts/{id}` — update cart (public)
  - `DELETE /carts/{id}` — remove cart (public)
  - `POST /carts/{cartId}/items` — add item (public)
  - `DELETE /carts/{cartId}/items/{productId}` — remove item (public)
- Orders (`/orders`)
  - `POST /orders` — create order (authenticated)
  - `GET /orders` — list orders (ROLE_ADMIN)
  - `GET /orders/{id}` — get order by id (authenticated)
  - `PUT /orders/{id}` — update order (ROLE_ADMIN)
  - `DELETE /orders/{id}` — delete order (ROLE_ADMIN)

## Configuration (env vars)
- `SPRING_DATASOURCE_URL`: e.g. `jdbc:postgresql://db:5432/fastshop_db`
- `SPRING_DATASOURCE_USERNAME`: e.g. `fastuser`
- `SPRING_DATASOURCE_PASSWORD`: e.g. `fastpassword`
- `SPRING_PROFILES_ACTIVE`: e.g. `prod`
- `JWT_SECRET`: secret used to sign JWT tokens
- `JWT_EXPIRATION`: expiration in ms (e.g. `3600000`)
- `RESET_ADMIN_PASSWORD`: `true|false` (optional)
- `RESET_ADMIN_USERNAME`: admin email/username (optional)
- `RESET_ADMIN_PLAIN_PASSWORD`: new plain password (optional)

Defaults suitable for local dev are provided in `compose.yml`.

## Admin Credentials (local)
- User: `albertovilar1@gmail.com`
- Password: `132747`
- Control envs: `RESET_ADMIN_PASSWORD=true`, plus `RESET_ADMIN_USERNAME` and `RESET_ADMIN_PLAIN_PASSWORD`.
Use only locally. Disable reset and rotate credentials in production.

## Local Development (without Docker)
- Run with Maven (Windows): `mvnw.cmd spring-boot:run`
- Build the JAR: `mvnw.cmd package -DskipTests`
- Optionally configure datasource env vars or use H2 for quick tests.

## Docker Build & Run (without Compose)
- Build local image: `docker build -t albertovilar/fastshop-backend:local .`
- Run container: `docker run -p 8080:8080 --env SPRING_PROFILES_ACTIVE=prod --env SPRING_DATASOURCE_URL=jdbc:postgresql://<host>:5432/fastshop_db --env SPRING_DATASOURCE_USERNAME=fastuser --env SPRING_DATASOURCE_PASSWORD=fastpassword --env JWT_SECRET=<your_secret> albertovilar/fastshop-backend:local`

## Tests
- Run tests: `mvnw.cmd test`
- Surefire plugin configured for `*Test.java`, `*Tests.java`, `*TestCase.java`, and `*IT.java`.

## Observability
- Health: `GET http://localhost:8080/actuator/health` → `{"status":"UP"}`
- For containers, Compose healthcheck waits for app readiness after initial startup.

## Validation Error Payload
Example for HTTP 422 Unprocessable Entity:
```json
{
  "timestamp": "2025-01-01T12:34:56",
  "status": 422,
  "error": "Invalid resources",
  "message": "Field validation errors",
  "path": "/api/resource",
  "errors": [
    { "fieldName": "name", "message": "must not be empty" },
    { "fieldName": "email", "message": "invalid format" }
  ]
}
```

## CI/CD
- CI workflow: `.github/workflows/ci.yml` for automated build and tests.
- Badge: `https://github.com/AlbertoVilar/fastshop-backend/actions/workflows/ci.yml/badge.svg`.
- Workflow page: `https://github.com/AlbertoVilar/fastshop-backend/actions/workflows/ci.yml`.
- Docker Hub: `https://hub.docker.com/r/albertovilar/fastshop-backend`.
- Recommendations: add smoke test for `/actuator/health` and `depends_on: condition: service_healthy` for `db`.

## Troubleshooting
- Alpine healthcheck compatibility ensured via `wget`.
- DB not ready: check `depends_on`; consider `condition: service_healthy`.
- Ports busy: verify `:8080` and `:5432` on host.

## License & Credits
Educational/personal project. Adapt per your licensing policy.

## Contact
- GitHub: `https://github.com/AlbertoVilar`
- LinkedIn: `https://www.linkedin.com/in/alberto-vilar-316725ab/`

## Screenshots & API Collection
- Place images (Postman/Insomnia) under `docs/`:
  - Login (JWT) showing `accessToken`.
  - `Authorization: Bearer <token>` on an authenticated `GET /orders/{id}`.
  - Validation example (HTTP 422) with `errors` payload.
- Suggested names: `docs/login-jwt.png`, `docs/orders-auth.png`, `docs/validation-422.png`.
- Postman Collection: `docs/Fastshop.postman_collection.json`
- Postman Environment: `docs/Fastshop.postman_environment.json` (vars: `baseUrl`, `jwt`).
- How to use:
  - Import both into Postman/Insomnia.
  - Run `Auth - Login (Admin)` to get `accessToken` and auto-populate `jwt`.
  - Call protected endpoints with the environment active (header `Authorization` parameterized).

---

Para leitura em Português, acesse o `README_pt.md`.