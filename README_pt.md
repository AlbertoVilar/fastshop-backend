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

## Mudanças Recentes e Notas de Segurança
- Integridade de preços: o servidor calcula `unitPrice` a partir de `Product.price` para itens de carrinho e pedido. Valores de `unitPrice` enviados pelo cliente em DTOs de requisição são ignorados para evitar manipulação.
- Atualizações de DTO: `ProductRequestDTO` exige `categoryId` na criação e atualmente no update; pode ser introduzido um `ProductUpdateDTO` futuro onde `categoryId` seja opcional e aplicado apenas quando informado.
- Checagens de ownership: helpers de segurança em nível de método (`CustomerSecurity`, `OrderSecurity`) garantem que apenas o dono ou admins acessem/modifiquem recursos específicos de cliente e pedido.
- CORS mais restrito: limitar `allowedOrigins` a hosts conhecidos quando `allowCredentials=true`.
- Timestamps de erro: padronizados para ISO 8601 usando `OffsetDateTime` em respostas `StandardError`.
- Logs: remoção/ajuste de logs sensíveis na autenticação para não indicar se a senha confere.

## Pré-requisitos
- `Java 21` e `Maven` (opcional para rodar sem Docker).
- `Docker` e `Docker Compose`.

## Quickstart com Docker Compose
Dev (usa base + override automaticamente):
1. Crie `.env` a partir de `.env.example` (opcional para dev).
2. Suba os serviços: `docker compose up -d`
3. Health: `Invoke-WebRequest http://localhost:8080/actuator/health` → `{"status":"UP"}`
4. Logs: `docker compose logs -f app`
5. pgAdmin (somente dev): `http://localhost:5050` (login via variáveis `PGADMIN_*`)

Prod (apenas arquivo base):
1. Copie `.env.example` para `.env` e preencha segredos.
2. Suba serviços: `docker compose -f docker-compose.yml --env-file .env up -d`
3. Preferencialmente rode atrás de proxy reverso com TLS.

Destaques do Compose:
- Dev: `compose.yml` + `docker-compose.override.yml` com pgAdmin e Postgres exposto.
- Prod: somente `docker-compose.yml`; DB sem `ports` expostos; healthchecks habilitados; reset de admin desabilitado.

## Produção (docker-compose.yml)
- Utilize o `docker-compose.yml` com um arquivo `.env` para deploy em produção.
- Passos:
  - Copie `.env.example` para `.env` e preencha os valores.
  - Rode `docker compose --env-file .env up -d`.
- Diferenças em relação ao `compose.yml` (dev local):
  - O serviço de DB não expõe `ports`; acesso apenas pela rede interna.
  - A porta do app pode ser mapeada via `APP_HTTP_PORT` (padrão `8080`).
  - Flags de reset de admin (`RESET_ADMIN_*`) desligadas.
  - Healthchecks habilitados para DB e app; app espera DB saudável.
  - Utiliza `SPRING_PROFILES_ACTIVE=prod` e Postgres `16-alpine`.
- Notas de segurança:
  - Nunca versionar `.env` com segredos.
  - Use um `JWT_SECRET` forte e longo.
  - Mantenha reset de admin desligado em produção e rotacione credenciais.
  - Prefira rodar atrás de proxy reverso (Nginx/Traefik) com TLS.

## Endpoints Principais
- Autenticação
  - `POST /auth/login` — body exemplo:
    ```json
    {"username":"albertovilar1@gmail.com","password":"132747"}
    ```
    - Resposta: `200 OK` com `accessToken` (JWT). Use `Authorization: Bearer <token>` nas chamadas autenticadas.
- Usuários (`/users`)
  - `GET /users/me` — dados do usuário autenticado (requer token)
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
  - `PUT /customers/{id}` — atualizar (autenticado, ownership aplicado)
  - `DELETE /customers/{id}` — remover (autenticado)
- Carrinho (`/carts`)
  - `GET /carts` — listar carrinhos (autenticado; retorna 404 quando vazio; considerar ADMIN apenas para listagem geral)
  - `GET /carts/{id}` — carrinho por id (público)
  - `POST /carts` — criar carrinho (autenticado)
  - `PUT /carts/{id}` — atualizar carrinho (autenticado)
  - `DELETE /carts/{id}` — remover carrinho (autenticado)
  - `POST /carts/{cartId}/items` — adicionar item ao carrinho (autenticado)
  - `DELETE /carts/{cartId}/items/{productId}` — remover item do carrinho (autenticado)

### Comportamento de DELETE no Carrinho
- `DELETE /carts/{cartId}/items/{productId}`:
  - Retorna `204 No Content` quando a remoção é bem-sucedida.
  - Retorna `404 Not Found` com `StandardError` quando o item não existe no carrinho.
- `DELETE /carts/me/items/{productId}`:
  - Retorna `204 No Content` quando a remoção é bem-sucedida.
  - Retorna `404 Not Found` com `StandardError` quando o item não existe para o usuário autenticado.

Exemplo de resposta `404` (item inexistente):
```json
{
  "timestamp": "2025-01-01T12:34:56Z",
  "status": 404,
  "error": "Recurso não encontrado",
  "message": "Item do carrinho não encontrado para o produto: <productId>",
  "path": "/carts/<cartId>/items/<productId>"
}
```
- Pedidos (`/orders`)
  - `POST /orders` — criar pedido (autenticado)
  - `GET /orders` — listar pedidos (ROLE_ADMIN)
  - `GET /orders/{id}` — pedido por id (autenticado, ownership aplicado)
  - `PUT /orders/{id}` — atualizar pedido (ROLE_ADMIN)
  - `DELETE /orders/{id}` — remover pedido (ROLE_ADMIN)

### Exemplos práticos (curl)
- Obter token (admin):
  ```bash
  curl -sS -X POST "http://localhost:8080/auth/login" \
    -H "Content-Type: application/json" \
    -d '{"username":"albertovilar1@gmail.com","password":"132747"}'
  ```

- Consultar dados do usuário autenticado (`/users/me`):
  ```bash
  TOKEN="<cole_o_accessToken_da_resposta_de_login>"
  curl -sS -H "Authorization: Bearer $TOKEN" "http://localhost:8080/users/me"
  ```

- Criar cliente (público):
  ```bash
  curl -sS -X POST "http://localhost:8080/customers" \
    -H "Content-Type: application/json" \
    -d '{
      "name":"Maria da Silva",
      "email":"maria@example.com",
      "birthDate":"1990-05-20",
      "phone":"(11) 91234-5678",
      "cpfOrCnpj":"123.456.789-09"
    }'
  ```

- Ler cliente por ID (admin tem acesso; não-dono recebe 403):
  ```bash
  ADMIN_TOKEN="<cole_o_accessToken_da_resposta_de_login>"
  curl -i -H "Authorization: Bearer $ADMIN_TOKEN" "http://localhost:8080/customers/1"
  # Exemplo conceitual de não-dono (outro usuário):
  OTHER_TOKEN="<token_de_outro_usuario_nao_dono>"
  curl -i -H "Authorization: Bearer $OTHER_TOKEN" "http://localhost:8080/customers/1"
  ```

- Remover item inexistente do carrinho (retorna 404):
  ```bash
  TOKEN="<accessToken_do_login>"
  CART_ID=4
  PRODUCT_ID_INEXISTENTE=9999
  curl -i -X DELETE "http://localhost:8080/carts/$CART_ID/items/$PRODUCT_ID_INEXISTENTE" \
    -H "Authorization: Bearer $TOKEN"
  ```

- Remover item existente do carrinho (retorna 204):
  ```bash
  TOKEN="<accessToken_do_login>"
  CART_ID=4
  PRODUCT_ID_EXISTENTE=6
  curl -i -X DELETE "http://localhost:8080/carts/$CART_ID/items/$PRODUCT_ID_EXISTENTE" \
    -H "Authorization: Bearer $TOKEN"
  ```

- Criar pedido (autenticado; o servidor deriva o `unitPrice` do item a partir de `Product.price`, o request não envia preço):
  ```bash
  # Assumindo que já existe productId=1 e customerId=1
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

- Ler pedido por ID (dono ou admin recebe 200; não-dono recebe 403):
  ```bash
  OWNER_TOKEN="<token_do_dono_do_pedido>"
  curl -i -H "Authorization: Bearer $OWNER_TOKEN" "http://localhost:8080/orders/1"
  OTHER_TOKEN="<token_de_outro_usuario>"
  curl -i -H "Authorization: Bearer $OTHER_TOKEN" "http://localhost:8080/orders/1"
  ```

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