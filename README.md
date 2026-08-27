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

## Recent Changes & Security Notes
- Pricing integrity: server computes `unitPrice` from `Product.price` for cart and order items. Client-provided `unitPrice` in request DTOs is ignored to prevent tampering.
- DTO updates: `ProductRequestDTO` requires `categoryId` on create and currently on update; future change may introduce a dedicated `ProductUpdateDTO` where `categoryId` is optional and applied only when provided.
- Ownership checks: method-level security helpers (`CustomerSecurity`, `OrderSecurity`) ensure only the owner or admins can access/modify specific customer and order resources.
- CORS tightening: restrict `allowedOrigins` to known hosts when `allowCredentials=true`.
- Error timestamps: standardized to ISO 8601 using `OffsetDateTime` in `StandardError` responses.
- Logging hygiene: removed/toned down sensitive authentication logs to avoid leaking password-match signals.

## Prerequisites
- `Java 21` and `Maven` (optional for non-Docker runs).
- `Docker` and `Docker Compose`.

## Quickstart with Docker Compose
Dev (uses base + override automatically):
1. Create `.env` from `.env.example` (optional for dev).
2. Start services: `docker compose up -d`
3. Check health: `Invoke-WebRequest http://localhost:8080/actuator/health` → `{"status":"UP"}`
4. Logs: `docker compose logs -f app`
5. pgAdmin (dev only): `http://localhost:5050` (login via `PGADMIN_*` vars)

Prod (base file only):
1. Copy `.env.example` to `.env` and fill secrets.
2. Start services: `docker compose -f docker-compose.yml --env-file .env up -d`
3. Prefer running behind a reverse proxy with TLS.

Compose highlights:
- Dev: `compose.yml` + `docker-compose.override.yml` with pgAdmin and Postgres port exposed.
- Prod: `docker-compose.yml` only; DB ports not exposed; healthchecks enabled; admin reset disabled.

## Production (docker-compose.yml)
- Use `docker-compose.yml` with an `.env` file for production deployments.
- Steps:
  - Copy `.env.example` to `.env` and fill values.
  - Run `docker compose --env-file .env up -d`.
- Differences vs `compose.yml` (local dev):
  - DB service does not expose `ports`; internal network-only.
  - App port can be mapped via `APP_HTTP_PORT` (defaults to `8080`).
  - Admin reset flags (`RESET_ADMIN_*`) are disabled.
  - Healthchecks enabled for both DB and app; app waits for DB healthy.
  - Uses `SPRING_PROFILES_ACTIVE=prod` and Postgres `16-alpine`.
- Security notes:
  - Never commit `.env` secrets.
  - Use a strong, long `JWT_SECRET`.
  - Keep admin reset off in production and rotate credentials.
  - Prefer running behind a reverse proxy (Nginx/Traefik) with TLS.

## Key Endpoints
- Authentication
  - `POST /auth/login` — example body:
    ```json
    {"username":"<admin_username>","password":"<admin_password>"}
    ```
    - Response: `200 OK` with `accessToken` (JWT). Use `Authorization: Bearer <token>` for protected requests.
- Users (`/users`)
  - `GET /users/me` — returns the authenticated user (requires token)
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
  - `PUT /customers/{id}` — update (authenticated, ownership enforced)
  - `DELETE /customers/{id}` — delete (authenticated)
- Carts (`/carts`)
  - `GET /carts` — list carts (authenticated; returns 404 when empty; consider ADMIN-only for global listing)
  - `GET /carts/{id}` — get cart by id (public)
  - `POST /carts` — create cart (authenticated)
  - `PUT /carts/{id}` — update cart (authenticated)
  - `DELETE /carts/{id}` — remove cart (authenticated)
  - `POST /carts/{cartId}/items` — add item (authenticated)
  - `DELETE /carts/{cartId}/items/{productId}` — remove item (authenticated)
 
### Cart DELETE Behavior
- `DELETE /carts/{cartId}/items/{productId}`:
  - Returns `204 No Content` when the removal succeeds.
  - Returns `404 Not Found` with `StandardError` when the item does not exist in the cart.
- `DELETE /carts/me/items/{productId}`:
  - Returns `204 No Content` when the removal succeeds.
  - Returns `404 Not Found` with `StandardError` when the item does not exist for the authenticated user.

Example `404` response (nonexistent item):
```json
{
  "timestamp": "2025-01-01T12:34:56Z",
  "status": 404,
  "error": "Resource not found",
  "message": "Cart item not found for product: <productId>",
  "path": "/carts/<cartId>/items/<productId>"
}
```
- Orders (`/orders`)
  - `POST /orders` — create order (authenticated)
  - `GET /orders` — list orders (ROLE_ADMIN)
  - `GET /orders/{id}` — get order by id (authenticated, ownership enforced)
  - `PUT /orders/{id}` — update order (ROLE_ADMIN)
  - `DELETE /orders/{id}` — delete order (ROLE_ADMIN)

### Practical curl examples
- Authenticate and get token (admin):
  ```bash
  curl -sS -X POST "http://localhost:8080/auth/login" \
    -H "Content-Type: application/json" \
    -d '{"username":"<admin_username>","password":"<admin_password>"}'
  ```

- Get authenticated user data (`/users/me`):
  ```bash
  TOKEN="<paste_accessToken_from_login_response>"
  curl -sS -H "Authorization: Bearer $TOKEN" "http://localhost:8080/users/me"
  ```

- Create a customer (public):
  ```bash
  curl -sS -X POST "http://localhost:8080/customers" \
    -H "Content-Type: application/json" \
    -d '{
      "name":"Mary Smith",
      "email":"mary@example.com",
      "birthDate":"1990-05-20",
      "phone":"(11) 91234-5678",
      "cpfOrCnpj":"123.456.789-09"
    }'
  ```

- Read customer by ID (owner or admin allowed; non-owner gets 403):
  ```bash
  ADMIN_TOKEN="<paste_accessToken_from_login_response>"
  curl -i -H "Authorization: Bearer $ADMIN_TOKEN" "http://localhost:8080/customers/1"
  OTHER_TOKEN="<token_of_another_non_owner_user>"
  curl -i -H "Authorization: Bearer $OTHER_TOKEN" "http://localhost:8080/customers/1"
  ```

- Remove nonexistent cart item (returns 404):
  ```bash
  TOKEN="<accessToken_from_login>"
  CART_ID=4
  NONEXISTENT_PRODUCT_ID=9999
  curl -i -X DELETE "http://localhost:8080/carts/$CART_ID/items/$NONEXISTENT_PRODUCT_ID" \
    -H "Authorization: Bearer $TOKEN"
  ```

- Remove existing cart item (returns 204):
  ```bash
  TOKEN="<accessToken_from_login>"
  CART_ID=4
  EXISTING_PRODUCT_ID=6
  curl -i -X DELETE "http://localhost:8080/carts/$CART_ID/items/$EXISTING_PRODUCT_ID" \
    -H "Authorization: Bearer $TOKEN"
  ```

- Create an order (authenticated; server derives item `unitPrice` from Product.price, request omits price):
  ```bash
  # Assuming productId=1 and customerId=1 already exist
  curl -sS -X POST "http://localhost:8080/orders" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
      "customerId": 1,
      "items": [
        {"productId":1, "quantity":2}
      ]
    }'
  ```

- Read order by ID (owner or admin gets 200; non-owner gets 403):
  ```bash
  OWNER_TOKEN="<token_of_order_owner>"
  curl -i -H "Authorization: Bearer $OWNER_TOKEN" "http://localhost:8080/orders/1"
  OTHER_TOKEN="<token_of_another_user>"
  curl -i -H "Authorization: Bearer $OTHER_TOKEN" "http://localhost:8080/orders/1"
  ```

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

## Local access

No account credentials are committed. To test authenticated endpoints, create a local user and keep its credentials only in your local environment (for example, in Postman environment variables). Do not use real or reused credentials in documentation, collections, or Compose files.

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