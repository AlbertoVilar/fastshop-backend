# Fastshop Backend [![CI](https://github.com/AlbertoVilar/fastshop-backend/actions/workflows/ci.yml/badge.svg)](https://github.com/AlbertoVilar/fastshop-backend/actions/workflows/ci.yml)

Este projeto backend simula um sistema de e-commerce, permitindo o gerenciamento de produtos, categorias, clientes, carrinhos de compra e pedidos. Conta com autenticação segura via JWT, persistência de dados em PostgreSQL com migrações Flyway e observabilidade por meio do Actuator. A execução é simplificada com Docker e Docker Compose para levantar o ambiente rapidamente.

Sistema backend em Java/Spring Boot para o projeto Fastshop. Foca em APIs REST com segurança, persistência no PostgreSQL, migrações com Flyway, observabilidade via Actuator e execução containerizada com Docker e Docker Compose.

## Visão Geral
- Framework: Spring Boot `3.5.x` (Java `21`).
- Persistência: JPA/Hibernate com PostgreSQL (prod) e H2 (runtime disponível para dev/testes).
- Migrações: Flyway.
- Segurança: Spring Security e JWT (JJWT).
- Observabilidade: Spring Boot Actuator (`/actuator/health`).
- Containerização: Docker (imagem `eclipse-temurin:21-jre-alpine`).
- Orquestração: Docker Compose com serviço `db` (Postgres) e `app`.

## Arquitetura (alto nível)
- Camadas típicas: controllers → services → repositories (DTOs e validações Bean Validation).
- Tratamento de erros: handler global retorna objeto padronizado com mensagens de validação de campos.
- Profiles: `SPRING_PROFILES_ACTIVE=prod` no Compose para uso de Postgres.
- Healthcheck: `/actuator/health` validado no container com `wget` (compatível com Alpine).

## Pré-requisitos
- `Java 21` e `Maven` (opcional para rodar sem Docker).
- `Docker` e `Docker Compose`.

## Quickstart com Docker Compose
1. Subir os serviços:
   - Windows (PowerShell): `docker compose up -d`
2. Verificar saúde da aplicação:
   - `Invoke-WebRequest http://localhost:8080/actuator/health` → deve retornar `{"status":"UP"}`
3. Checar logs da aplicação:
   - `docker compose logs app --tail 120`

Compose principal (`compose.yml`):
- Porta do app: `8080` mapeada para o host.
- Porta do db: `5432` mapeada para o host.
- Healthcheck do app: `wget -qO- http://localhost:8080/actuator/health`.
- Reinício: `restart: on-failure` para o app e `restart: always` para o db.

## Endpoints Principais
- Autenticação
  - `POST /auth/login` — body exemplo:
    ```json
    {"username":"albertovilar1@gmail.com","password":"132747"}
    ```
    - Resposta: `200 OK` com `accessToken` (JWT). Use `Authorization: Bearer <token>` nas chamadas autenticadas.
- Produtos (`/products`)
  - `GET /products` — lista produtos (público)
  - `GET /products/{id}` — produto por id (público)
  - `POST /products` — criar produto (ROLE_ADMIN)
  - `PUT /products/{id}` — atualizar (ROLE_ADMIN)
  - `DELETE /products/{id}` — remover (ROLE_ADMIN)
- Categorias (`/categories`)
  - `GET /categories` — lista categorias (público)
  - `GET /categories/{id}` — categoria por id (público)
  - `POST /categories` — criar categoria (ROLE_ADMIN)
  - `PUT /categories/{id}` — atualizar (ROLE_ADMIN)
  - `DELETE /categories/{id}` — remover (ROLE_ADMIN)
- Clientes (`/customers`)
  - `POST /customers` — cadastro (público)
  - `GET /customers` — listar clientes (autenticado)
  - `GET /customers/{id}` — cliente por id (autenticado)
  - `PUT /customers/{id}` — atualizar (autenticado)
  - `DELETE /customers/{id}` — remover (autenticado)
- Carrinho (`/carts`)
  - `POST /carts` — criar carrinho (público)
  - `GET /carts` — listar carrinhos (público)
  - `GET /carts/{id}` — carrinho por id (público)
  - `PUT /carts/{id}` — atualizar carrinho (público)
  - `DELETE /carts/{id}` — remover carrinho (público)
  - `POST /carts/{cartId}/items` — adicionar item ao carrinho (público)
  - `DELETE /carts/{cartId}/items/{productId}` — remover item do carrinho (público)
- Pedidos (`/orders`)
  - `POST /orders` — criar pedido (autenticado)
  - `GET /orders` — listar pedidos (ROLE_ADMIN)
  - `GET /orders/{id}` — pedido por id (autenticado)
  - `PUT /orders/{id}` — atualizar pedido (ROLE_ADMIN)
  - `DELETE /orders/{id}` — remover pedido (ROLE_ADMIN)

## Configuração (variáveis de ambiente)
Os principais parâmetros são configuráveis via variáveis de ambiente:
- `SPRING_DATASOURCE_URL`: ex. `jdbc:postgresql://db:5432/fastshop_db`
- `SPRING_DATASOURCE_USERNAME`: ex. `fastuser`
- `SPRING_DATASOURCE_PASSWORD`: ex. `fastpassword`
- `SPRING_PROFILES_ACTIVE`: ex. `prod`
- `JWT_SECRET`: chave secreta para assinar tokens JWT
- `JWT_EXPIRATION`: tempo de expiração em milissegundos (ex.: `3600000`)
- `RESET_ADMIN_PASSWORD`: `true|false` para reset de admin (opcional)
- `RESET_ADMIN_USERNAME`: e-mail/usuário do admin (opcional)
- `RESET_ADMIN_PLAIN_PASSWORD`: nova senha em texto plano (opcional)

No `compose.yml` já existem valores padrão adequados para um ambiente local.

## Credenciais de Admin (ambiente local)
Para facilitar testes, o Compose pode resetar um admin:
- Usuário: `albertovilar1@gmail.com`
- Senha: `132747`
- Variáveis de controle: `RESET_ADMIN_PASSWORD=true`, `RESET_ADMIN_USERNAME`, `RESET_ADMIN_PLAIN_PASSWORD`.
Use apenas em ambiente local. Em produção, desabilite o reset e troque as credenciais.

## Desenvolvimento local (sem Docker)
- Executar com Maven (Windows): `mvnw.cmd spring-boot:run`
- Build do JAR: `mvnw.cmd package -DskipTests`
- Se necessário, defina as variáveis de datasource ou utilize H2 para testes rápidos.

## Build e execução com Docker (sem Compose)
- Build da imagem local: `docker build -t albertovilar/fastshop-backend:local .`
- Run do container: `docker run -p 8080:8080 --env SPRING_PROFILES_ACTIVE=prod --env SPRING_DATASOURCE_URL=jdbc:postgresql://<host>:5432/fastshop_db --env SPRING_DATASOURCE_USERNAME=fastuser --env SPRING_DATASOURCE_PASSWORD=fastpassword --env JWT_SECRET=<sua_chave> albertovilar/fastshop-backend:local`

## Testes
- Executar testes: `mvnw.cmd test`
- Plugin Surefire configurado para rodar `*Test.java`, `*Tests.java`, `*TestCase.java` e `*IT.java`.

## Observabilidade
- Health: `GET http://localhost:8080/actuator/health` → `{"status":"UP"}`
- Para ambientes conteinerizados, o healthcheck do Compose aguarda o app ficar saudável após o período inicial.

## Padrão de Erros de Validação
Quando ocorrem erros de validação (HTTP 422 Unprocessable Entity), o backend retorna um objeto com os campos e mensagens de erro. Exemplo:
```json
{
  "timestamp": "2025-01-01T12:34:56",
  "status": 422,
  "error": "Recursos inválidos",
  "message": "Erro de validação nos campos",
  "path": "/api/resource",
  "errors": [
    { "fieldName": "nome", "message": "não pode ser vazio" },
    { "fieldName": "email", "message": "formato inválido" }
  ]
}
```

## CI/CD
- Workflow de CI disponível em `.github/workflows/ci.yml` para build e testes automáticos.
- Badge de status: `https://github.com/AlbertoVilar/fastshop-backend/actions/workflows/ci.yml/badge.svg` (adicione no topo do README).
- Página do workflow: `https://github.com/AlbertoVilar/fastshop-backend/actions/workflows/ci.yml`.
- Docker Hub (imagem usada no Compose): `https://hub.docker.com/r/albertovilar/fastshop-backend`.
- Recomendações: adicionar smoke test do `/actuator/health` e `depends_on: condition: service_healthy` para `db`.

## Solução de Problemas
- Healthcheck falhando em Alpine: já usamos `wget` no Compose para compatibilidade.
- Banco não pronto ao iniciar o app: ver `depends_on`; considere `condition: service_healthy`.
- Portas ocupadas: verifique `:8080` e `:5432` no host.

## Licença e Créditos
Projeto educacional/pessoal. Ajuste conforme sua política de licenciamento.

## Contato
- GitHub: `https://github.com/AlbertoVilar`
- LinkedIn: `https://www.linkedin.com/in/alberto-vilar-316725ab/`

## Screenshots e Coleção de API
- Adicione uma pasta `docs/` com imagens de chamadas (Postman/Insomnia):
  - Login (JWT) mostrando `accessToken`.
  - `Authorization: Bearer <token>` em um `GET /orders/{id}` autenticado.
  - Exemplo de validação (HTTP 422) com payload de erro contendo `errors`.
- Sugestão de nomes: `docs/login-jwt.png`, `docs/orders-auth.png`, `docs/validation-422.png`.
- Coleção Postman: `docs/Fastshop.postman_collection.json`
- Ambiente Postman: `docs/Fastshop.postman_environment.json` (variáveis: `baseUrl`, `jwt`).
- Como usar:
  - Importe a coleção e o ambiente no Postman/Insomnia.
  - Execute `Auth - Login (Admin)` para obter o `accessToken` e popular `jwt` automaticamente.
  - Chame endpoints protegidos com o ambiente ativo (header `Authorization` já parametrizado).