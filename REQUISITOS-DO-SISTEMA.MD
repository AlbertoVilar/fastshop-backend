# Requisitos do Sistema — FastShop

## Visão Geral
- API de e-commerce com catálogo de produtos e categorias, gestão de clientes, carrinhos e pedidos.
- Autenticação e autorização via JWT, sessões stateless.
- Observabilidade por `GET /actuator/health` e versionamento de banco com Flyway.
- Perfis: `ROLE_ADMIN` e `ROLE_CUSTOMER`.

## Objetivos
- Disponibilizar catálogo público de produtos/categorias.
- Permitir login e emissão de token JWT.
- Viabilizar operações de carrinho e criação de pedidos.
- Oferecer CRUD de clientes e endereços.
- Habilitar operações administrativas (produtos, categorias, usuários).

## Escopo
- Endpoints REST para autenticação, catálogo, carrinhos, pedidos, clientes e endereços.
- Regras de autorização centralizadas em `SecurityConfig`.
- DTOs e conversores para separar modelos internos de payloads externos.

## Perfis e Autorização
Conforme `src/main/java/com/fastshop/config/SecurityConfig.java`:
- Público:
  - `POST /auth/login`, `GET /products/**`, `GET /categories/**`, `GET /actuator/**`, `OPTIONS /**`.
  - `POST /customers` (cadastro/signup).
  - `GET/POST/PUT/DELETE /carts/**` (público na configuração atual; avaliar mudança para autenticado).
- Autenticado (cliente ou admin):
  - `POST /orders`, `GET /orders/{id}`.
  - `PUT /customers/*`.
- Administrador (`ROLE_ADMIN`):
  - `POST/PUT/DELETE /products/**`, `POST/PUT/DELETE /categories/**`, `GET /orders`, `PUT /orders/**`, `POST/PUT /orders/*/payment`, `GET /reports/**`, `GET/POST/PUT/DELETE /users/**`.

## Funcionalidades
- Autenticação
  - `POST /auth/login` autentica e retorna `JWT`.
  - Senhas com `BCrypt`; `CustomAuthenticationEntryPoint` para 401 padronizado.
- Produtos
  - CRUD com autorização (leitura pública, escrita restrita a admin).
  - Respostas em `ProductResponseDTO`.
- Categorias
  - CRUD; leitura pública, escrita restrita a admin.
- Carrinho
  - Criar, listar, atualizar e remover carrinhos.
  - Itens: `POST /carts/{cartId}/items`, `DELETE /carts/{cartId}/items/{productId}`.
- Pedidos
  - CRUD; criação por autenticado, administração por admin.
- Clientes
  - Cadastro público `POST /customers`; atualização com autenticação.
- Endereços
  - CRUD associado ao cliente.
- Usuários
  - Endpoints reservados para administração (CRUD futuro).

## Endpoints
- Autenticação: `POST /auth/login`
- Produtos: `GET /products`, `GET /products/{id}`, `POST /products`, `PUT /products/{id}`, `DELETE /products/{id}`
- Categorias: `GET /categories`, `GET /categories/{id}`, `POST /categories`, `PUT /categories/{id}`, `DELETE /categories/{id}`
- Carrinhos: `POST /carts`, `GET /carts`, `GET /carts/{id}`, `PUT /carts/{id}`, `DELETE /carts/{id}`, `POST /carts/{cartId}/items`, `DELETE /carts/{cartId}/items/{productId}`
- Pedidos: `GET /orders`, `GET /orders/{id}`, `POST /orders`, `PUT /orders/{id}`, `DELETE /orders/{id}`
- Clientes: `GET /customers`, `GET /customers/{id}`, `POST /customers`, `PUT /customers/{id}`, `DELETE /customers/{id}`
- Endereços: `GET /addresses`, `GET /addresses/{id}`, `POST /addresses`, `PUT /addresses/{id}`, `DELETE /addresses/{id}`
- Usuários: `GET/POST/PUT/DELETE /users/**` (administrativo)
- Saúde: `GET /actuator/health`

## Modelos de Dados (resumo)
- `Product`: id, nome, descrição, preço, categoria.
- `Category`: id, nome.
- `Cart`: id, cliente (1:1), itens (1:N).
- `CartItem`: `cart_id`, `product_id` com `UNIQUE (cart_id, product_id)`.
- `Order`, `OrderItem`: associações a `Customer` e `Product`.
- `Customer`: dados pessoais, listas de `Address` e `Order`.
- `Address`: logradouro, número, cidade, estado, CEP, etc.
- `User`: credenciais, lista de `Role`.
- `Role`: autoridade (`ROLE_ADMIN`, `ROLE_CUSTOMER`).

## Regras de Negócio
- Carrinho
  - Um produto aparece no carrinho no máximo uma vez (`UNIQUE (cart_id, product_id)`).
  - Remoção de item por `DELETE /carts/{cartId}/items/{productId}`.
- Deleção de Produto
  - Bloqueada se referenciado em `cart_items.product_id` (sem `ON DELETE CASCADE`).
  - Limpar itens dos carrinhos antes do `DELETE /products/{id}`.
- Pedidos
  - Criação apenas por usuário autenticado; listagem/atualização por admin.
- Clientes
  - Cadastro público; alterações exigem autenticação.

## Erros e Respostas
- Padrão de erro: `StandardError` via `GlobalExceptionHandler`.
- Mapeamentos principais:
  - `ResourceNotFoundException` → `404 Not Found`.
  - `IllegalArgumentException`/`HttpMessageNotReadableException` → `400 Bad Request`.
  - `BadCredentialsException`/`AuthenticationException` → `401 Unauthorized`.
  - `DataIntegrityViolationException`/`DatabaseException` → `400/409` conforme caso.

## Requisitos Não Funcionais
- Segurança: JWT, `BCrypt`, entrada validada, sessões stateless.
- Performance: filtros leves e sem estado.
- Confiabilidade: `Flyway` e `HikariCP`.
- Compatibilidade: `CORS` liberado (`*`), métodos `GET/POST/PUT/DELETE/OPTIONS`.
- Manutenibilidade: DTOs e conversores; serviços desacoplados.

## Observabilidade e Operações
- Saúde: `GET /actuator/health` com `status=UP`.
- Logs: inicialização de `Tomcat`, `HikariPool-1`, `Flyway`; logs de autenticação/JWT.
- Deploy local: `compose.yml` e `Dockerfile` atualizados.

## Dados Iniciais (Seed)
- `V5__seed_initial_data.sql`: categorias (1–5), clientes, endereços, produtos (1–3), pedidos e itens.
- `V8__seed_security_data.sql`: roles, usuários admin/cliente com senhas `BCrypt` e vínculos a `Customer`.

## Critérios de Aceite
- Autenticação: `POST /auth/login` retorna token; endpoints protegidos aceitam `Authorization: Bearer <token>`.
- Produtos: admin realiza `POST/PUT/DELETE`; leitura pública funciona.
- Categorias: leitura pública; CRUD por admin.
- Carrinho: adicionar/remover itens; deleção de produto bloqueia se referido.
- Pedidos: cliente autenticado cria; admin lista/atualiza.
- Clientes/Endereços: cadastro público e updates autenticados.

## Próximos Passos
- Detalhar payloads de DTOs de request/response por endpoint.
- Testes automatizados de autorização (admin vs cliente vs anônimo).
- Avaliar exigir autenticação para `carts` e soft delete para `Product`.
- Criar endpoint para remover produto de todos os carrinhos (opcional).
- Adicionar exemplos `curl` e README de integração.