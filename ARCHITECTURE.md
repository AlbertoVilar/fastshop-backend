# FastShop — Arquitetura, Segurança e Operação

Este documento descreve a arquitetura do sistema FastShop, práticas de segurança, modelo de dados, fluxos de autenticação/autorização, tratamento de erros, configuração por perfis, execução local, contêiner/Docker, e CI. Foi elaborado seguindo boas práticas de engenharia de software e reflete o estado atual do repositório.

## Sumário
- Visão Geral
- Arquitetura
- Endpoints e Autorização
- Segurança (JWT)
- Modelo de Dados
- Persistência e Migrations (Flyway)
- Tratamento de Erros
- Configuração e Perfis
- Execução Local
- Docker e Compose
- CI (Integração Contínua)
- Observações e Melhorias

## Visão Geral
- Stack: `Spring Boot 3.5.x`, `Java 21`, Spring Web, Spring Data JPA, Spring Security, JJWT, Flyway, PostgreSQL (dev/prod), H2 (testes), Actuator.
- Camadas: Controllers → Services → Mappers/Converters → Repositories → Entities/DTOs.
- Objetivo: E-commerce simplificado com produtos, categorias, clientes, endereços, carrinhos e pedidos.

## Arquitetura
- Pacotes principais:
  - `controllers`: expõem endpoints REST por recurso (`ProductController`, `CategoryController`, `CustomerController`, `AddressController`, `CartController`, `OrderController`, `AuthController`, `UserController`).
  - `services`: centralizam regras de negócio e integrações (`ProductService`, `CategoryService`, `CustomerService`, `AddressService`, `CartService`, `OrderService`, `AuthService`, `TokenService`, `UserService`).
  - `mappers`: conversão entre entidades e DTOs (ex.: `ProductConverter`, `CustomerConverter`, `OrderConverter`, etc.).
  - `repositories`: interfaces Spring Data JPA por agregado (`ProductRepository`, `CategoryRepository`, `CustomerRepository`, `AddressRepository`, `CartRepository`, `OrderRepository`, `UserRepository`).
  - `entities`: domínio (`Product`, `Category`, `Customer`, `Address`, `Cart`, `CartItem`, `Order`, `OrderItem`, `User`, `Role`).
  - `config`: configuração de segurança (`SecurityConfig`).
  - `security`: handlers e filtro JWT (`CustomAuthenticationEntryPoint`, `CustomAccessDeniedHandler`, `jwt.JwtAuthenticationFilter`).
  - `handlers`: `GlobalExceptionHandler` para responses padronizados.
  - `bootstrap`: utilitários de inicialização (ex.: `PasswordResetRunner`).

- Fluxo de requisição típico:
  1. Controller recebe a requisição e valida DTOs.
  2. Service aplica regras, orquestra repositórios, mappers e validações.
  3. Repositories executam operações JPA.
  4. Responses retornam DTOs adequados.

## Endpoints e Autorização
- Público:
  - `POST /auth/login` (gera JWT).
  - `GET /products/**` (leitura de produtos).
  - `GET /categories/**` (leitura de categorias).
  - `POST /customers` (cadastro de cliente).
  - Recursos estáticos e `GET /actuator/**`.

- Requer autenticação (usuário logado):
  - `GET /orders/**` (visualização de pedidos do usuário) — regras adicionais por ID são aplicadas no service; `GET /orders` geral é restrito a `ADMIN`.
  - `PUT /customers/*` (atualização do próprio cliente).
  - Demais rotas não cobertas por permissões explícitas exigem autenticação.

- Restrito a `ADMIN`:
  - `POST/PUT/DELETE /products/**`.
  - `POST/PUT/DELETE /categories/**`.
  - `GET /orders` (lista administrativa), `PUT /orders/**` (administração de pedidos).
  - `POST/PUT /orders/*/payment`.
  - `GET /reports/**`.
  - `GET/POST/PUT/DELETE /users/**`.

As regras são configuradas em `SecurityConfig` usando `SecurityFilterChain` e `authorizeHttpRequests`.

## Segurança (JWT)
- Autenticação:
  - `AuthController` (`POST /auth/login`) chama `AuthService.authenticate`.
  - `AuthService` usa `AuthenticationManager` para validar credenciais e busca o `User` no `UserRepository`.
  - `TokenService.generateToken(User)` cria o JWT com issuer, subject (username), claim `roles`, `iat`, `exp`, e assina com chave HMAC (`jjwt`).
  - Configurações: `jwt.secret` e `jwt.expiration` (ms) nas `application-*.properties`.

- Autorização:
  - `JwtAuthenticationFilter` lê `Authorization: Bearer <token>`, valida via `TokenService.validateToken`, extrai `username`, carrega `UserDetails` e seta `SecurityContext`.
  - `SecurityConfig` define rotas públicas, autenticadas e restritas por role.
  - `UserService` implementa `UserDetailsService` para `loadUserByUsername`.

- Erros de segurança:
  - `CustomAuthenticationEntryPoint` retorna 401 com `StandardError` JSON para requisições sem credenciais válidas.
  - `CustomAccessDeniedHandler` retorna 403 com `StandardError` quando o usuário não tem permissão.
  - `GlobalExceptionHandler` cobre `BadCredentialsException` e outras `AuthenticationException` com 401.

## Modelo de Dados
- Principais entidades e relações:
  - `Product` — produto com `category` (`@ManyToOne`).
  - `Category` — categoria com lista de `products`.
  - `Customer` — dados pessoais e relação 1:N com `Address`.
  - `Address` — endereços ligados ao `Customer`.
  - `Cart` — relação com `Customer` e itens `CartItem` (unique `(cart_id, product_id)`).
  - `CartItem` — item com `product`, `quantity` e vínculo ao `Cart`.
  - `Order` — pedido relacionado a `Customer`, coleção de `OrderItem`.
  - `OrderItem` — item do pedido com produto, quantidade e preço.
  - `User` — credenciais e roles (`Role`), implementa `UserDetails` via `UserService`.
  - `Role` — autoridade/permissão (`ROLE_ADMIN`, etc.).

Nota: constraints e mapeamentos detalhados constam nos arquivos de entidade e nas migrations.

## Persistência e Migrations (Flyway)
- Migrations em `src/main/resources/db/migration`:
  - `V1__Create_initial_tables.sql` a `V8__seed_security_data.sql` (criação de tabelas, ajustes, seeds iniciais e de segurança).
- Perfis:
  - `dev`: PostgreSQL local, `ddl-auto=validate`, Flyway habilitado.
  - `prod`: PostgreSQL, `ddl-auto=none`, Flyway baseline e migração controlada.
  - `test`: H2 em memória, Flyway habilitado para garantir schema consistente nos testes.

## Tratamento de Erros
- `GlobalExceptionHandler` (`@RestControllerAdvice`) padroniza respostas:
  - 404 `ResourceNotFoundException`.
  - 400 `DataIntegrityViolationException`, `DatabaseException`, `IllegalArgumentException`, `HttpMessageNotReadableException`.
  - 401 `BadCredentialsException` e outras `AuthenticationException`.
  - 403 via `CustomAccessDeniedHandler` para `AccessDeniedException`.
- Modelo: `StandardError` com `timestamp`, `status`, `error`, `message`, `path`.

## Configuração e Perfis
- `application.properties`: define `spring.profiles.active` default.
- `application-dev.properties`: PostgreSQL local, `ddl-auto=validate`, `spring.flyway.enabled=true`.
- `application-prod.properties`: ajustes para produção, baseline Flyway, Actuator, logs e `jwt.secret`/`jwt.expiration` via env (`JWT_SECRET`, `JWT_EXPIRATION`).
- `application-test.properties`: H2, `ddl-auto=none`, Flyway habilitado, `jwt.secret` e `jwt.expiration` específicos de teste.

## Execução Local
- Pré‑requisitos: Java 21 e Maven.
- Comandos (Windows):
  - `mvnw.cmd spring-boot:run` — inicia a aplicação (perfil default definido em `application.properties`).
  - `mvnw.cmd test` — executa testes.
  - `mvnw.cmd clean package` — build e empacota.
- Endpoints úteis:
  - `POST /auth/login` com `{"username":"...","password":"..."}`.
  - `GET /products` e `GET /categories` públicos.
  - Acesso autenticado: enviar `Authorization: Bearer <token>`.

## Docker e Compose
- `Dockerfile` com multi‑stage: builder Maven (temurin 21) e runtime JRE (temurin 21). Gera `app.jar` e usa `ENTRYPOINT ["java","-jar","app.jar"]`.
- `compose.yml`:
  - Serviço `db` (Postgres 16) com volume persistente.
  - Serviço `app` usa a imagem publicada `albertovilar/fastshop-backend:latest`, expõe `8080`, configura env (`SPRING_PROFILES_ACTIVE=prod`, `JWT_SECRET`, `JWT_EXPIRATION`, credenciais do DB), depende de `db` e possui `healthcheck` via Actuator.

- Imagem publicada no Docker Hub: `albertovilar/fastshop-backend` com tags `latest` e `${{ github.sha }}`.
- Exemplos:
  - Pull: `docker pull albertovilar/fastshop-backend:latest`
  - Run: `docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=prod -e JWT_SECRET=changeme -e JWT_EXPIRATION=86400000 -e SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/fastshop -e SPRING_DATASOURCE_USERNAME=postgres -e SPRING_DATASOURCE_PASSWORD=postgres albertovilar/fastshop-backend:latest`

### Compose — Healthcheck
Exemplo de `healthcheck` no serviço `app` para monitoramento automático:

```yaml
services:
  app:
    image: albertovilar/fastshop-backend:latest
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 5
      start_period: 60s
```

Observação: se a imagem não possuir `curl`, usar `CMD-SHELL` com `wget`:

```yaml
healthcheck:
  test: ["CMD-SHELL", "wget -qO- http://localhost:8080/actuator/health || exit 1"]
  interval: 30s
  timeout: 10s
  retries: 5
  start_period: 60s
```

## CI (Integração Contínua)
- Workflow em `.github/workflows/ci.yml`:
  - Dispara em `push` para `main`, `develop` e `feature/**`, e em `pull_request` para `main` e `develop`.
  - Usa `ubuntu-latest`, faz checkout, configura Java 21 (Temurin) com cache Maven, executa `mvn -B package` (compila e testa) e exibe relatório do Surefire.
  - Configura Docker Buildx e constrói a imagem Docker usando `docker/build-push-action@v5`.
  - Faz login seguro no Docker Hub com `docker/login-action@v3` usando `secrets` (`DOCKERHUB_USERNAME`, `DOCKERHUB_TOKEN`).
  - Realiza push da imagem para `albertovilar/fastshop-backend` com tags `latest` e `${{ github.sha }}`.

  Exemplo de passos de Docker no workflow (build e push):

  ```yaml
  - name: Configurar Docker Buildx
    uses: docker/setup-buildx-action@v3

  - name: Login no Docker Hub
    uses: docker/login-action@v3
    with:
      username: ${{ secrets.DOCKERHUB_USERNAME }}
      password: ${{ secrets.DOCKERHUB_TOKEN }}

  - name: Construir e Fazer Push da Imagem Docker
    uses: docker/build-push-action@v5
    with:
      context: .
      file: ./Dockerfile
      tags: albertovilar/fastshop-backend:latest, albertovilar/fastshop-backend:${{ github.sha }}
      push: true
  ```

## CD (Entrega Contínua)
- O pipeline realiza push automático da imagem Docker a cada `push/PR` nas branches monitoradas, garantindo artefatos prontos para deploy.
- Boas práticas:
  - Gate para push somente em `main`/`develop` com `if:` no passo de push.
  - Usar ambientes e approvals para promoção (staging → prod).
  - Versionar imagens adicionais com tags semânticas além do SHA.

  Exemplo de passos de Docker no workflow:

  ```yaml
  - name: Configurar Docker Buildx
    uses: docker/setup-buildx-action@v3

  - name: Construir Imagem Docker
    uses: docker/build-push-action@v5
    with:
      context: .
      file: ./Dockerfile
      tags: fastshop:ci-${{ github.sha }}
      load: true
  ```
- Objetivo: garantir build e testes a cada push/PR, mantendo qualidade de integração.

## Observações e Melhorias
- Segurança:
  - Avaliar rotação de `jwt.secret` e externalização segura (Vault/Secrets Manager).
  - Considerar refresh tokens e logout (blacklist/short expiry + rotate).
  - Restringir CORS a origens conhecidas.
- Testes:
  - Ampliar cobertura para controllers/services com cenários de autorização.
  - Testes de integração com Flyway em H2 garantem schema consistente; validar compatibilidade com Postgres (dialeto/testcontainers).
- Observabilidade:
  - Ativar métricas/health via Actuator conforme necessidade.
- Dados:
  - Revisar seeds e constraints (ex.: unicidade em `CartItem`).
- CI/CD:
  - Push de imagem Docker configurado para Docker Hub (via secrets). Próximos passos: gates por branch, releases semânticas e pipelines de deploy.
  - Pipeline de deploy para ambientes (dev/staging/prod).