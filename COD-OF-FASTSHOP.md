# FastShop — Documentação Técnica

Este documento resume a arquitetura, endpoints, modelos (DTOs e entidades), fluxo de dados, configuração de testes e pontos de melhoria do projeto FastShop.

## Sumário
- Visão Geral
- Arquitetura
- Como Executar
- Configuração de Testes (H2)
- Endpoints (por recurso)
- Modelos (DTOs)
- Entidades e Relacionamentos
- Converters/Mappers
- Repositórios
- Tratamento de Erros
- Observações e Melhorias

---

## Visão Geral
- Stack: Spring Boot, Spring Web, Spring Data JPA, H2 (testes).
- Camadas: Controllers → Services → Converters/Mappers → Repositories → Entities/DTOs.
- Objetivo: CRUD e operações básicas de e-commerce (produtos, categorias, clientes, endereços, pedidos e itens de carrinho).

## Arquitetura
- Controllers expõem REST endpoints.
- Services centralizam regras de negócio e validações.
- Converters/Mappers transformam entre DTOs e entidades.
- Repositories persistem com Spring Data JPA.
- Entities/DTOs modelam domínio e payloads de API.

## Como Executar
Pré‑requisitos:
- Java 17 (ou versão especificada no `pom.xml`).
- Maven (usa wrapper incluído).

Comandos (Windows):
- Executar aplicação: `mvnw.cmd spring-boot:run`
- Rodar testes: `mvnw.cmd test`
- Build: `mvnw.cmd clean package`

### Docker Compose
Para executar com Docker Compose (produção):

```bash
docker compose up -d
```

Serviços:
- `db`: Postgres 16 com volume persistente `postgres_data`.
- `app`: imagem `albertovilar/fastshop-backend:latest`, porta `8080`, perfil `prod`, variáveis JWT e credenciais do DB.

Healthcheck do `app`:
```yaml
healthcheck:
  test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
  interval: 30s
  timeout: 10s
  retries: 5
  start_period: 60s
```

Caso a imagem não tenha `curl`, usar:
```yaml
healthcheck:
  test: ["CMD-SHELL", "wget -qO- http://localhost:8080/actuator/health || exit 1"]
  interval: 30s
  timeout: 10s
  retries: 5
  start_period: 60s
```

Verificar estado:
- `docker ps` (STATUS deve ficar `healthy`).
- `docker compose logs app --tail=200`.
- `http://localhost:8080/actuator/health`.

## Configuração de Testes (H2)
Arquivo: `src/test/resources/application-test.properties`
- `spring.datasource.url=jdbc:h2:mem:fastshopdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`
- `spring.jpa.hibernate.ddl-auto=create-drop`
- `spring.jpa.show-sql=true`
- `spring.flyway.enabled=false`
- Console H2: `spring.h2.console.enabled=true`, path `spring.h2.console.path=/h2-console`
- Observação: `spring.h2.console.settings.web-allow-others=true` permite acesso remoto ao console — usar apenas em desenvolvimento.

## Endpoints (por recurso)

### Produtos (`/products`)
- `GET /products/{id}` — obter produto por id
- `GET /products` — listar todos
- `POST /products` — criar
- `PUT /products/{id}` — atualizar
- `DELETE /products/{id}` — deletar

### Categorias (`/categories`)
- `POST /categories` — criar
- `GET /categories` — listar todos
- `GET /categories/{id}` — obter por id
- `PUT /categories/{id}` — atualizar
- `DELETE /categories/{id}` — deletar

### Clientes (`/customers`)
- `POST /customers` — criar
- `PUT /customers/{id}` — atualizar
- `GET /customers/{id}` — obter por id
- `GET /customers` — listar todos
- `DELETE /customers/{id}` — deletar

### Endereços (`/addresses`)
- `POST /addresses` — criar
- `PUT /addresses/{id}` — atualizar
- `DELETE /addresses/{id}` — deletar
- `GET /addresses/{id}` — obter por id
- `GET /addresses` — listar todos

### Pedidos (`/orders`)
- `GET /orders/{id}` — obter por id
- `GET /orders` — listar todos
- `POST /orders` — criar
- `PUT /orders/{id}` — atualizar
- `DELETE /orders/{id}` — deletar

### Itens de Carrinho (`/cart-items`)
- `POST /cart-items` — criar item de carrinho

## Modelos (DTOs)

### Produto
- `ProductRequestDTO`: `name`, `description`, `price`, `stock`, `imageUrl`, `categoryId`
- `ProductResponseDTO`: `id`, `name`, `description`, `price`, `stock`, `imageUrl`, `categoryId`, `categoryName`

### Categoria
- `CategoryRequestDTO`: `name`, `description`, `products`
- `CategoryResponseDTO`: `id`, `name`, `description`

### Cliente
- `CustomerRequestDTO`: `name`, `email`, `addressIds`
- `CustomerResponseDTO`: `id`, `name`, `email`, `addresses` (lista de `AddressResponseDTO`)

### Endereço
- `AddressRequestDTO`: `street`, `neighborhood`, `city`, `state`, `zipCode`, `country`
- `AddressResponseDTO`: `id`, `street`, `neighborhood`, `city`, `state`, `zipCode`, `country`

### Pedido
- `OrderRequestDTO`: `customerId`, `items` (lista de `OrderItemRequestDTO`)
- `OrderResponseDTO`: `id`, `customerId`, `customerName`, `status`, `createdAt`, `total`, `items` (lista de `OrderItemResponseDTO`)

### Item de Pedido
- `OrderItemRequestDTO`: `productId`, `quantity`, `unitPrice`
- `OrderItemResponseDTO`: `id`, `productId`, `productName`, `quantity`, `unitPrice`

### Carrinho
- `CartRequestDTO`: `customerId`, `items` (lista de `CartItemRequestDTO`)
- `CartResponseDTO`: `id`, `customerId`, `total`, `items` (lista de `CartItemResponseDTO`)

### Item de Carrinho
- `CartItemRequestDTO`: `cartId`, `productId`, `quantity`, `unitPrice`
- `CartItemResponseDTO`: `id`, `productId`, `productName`, `quantity`, `unitPrice`

## Entidades e Relacionamentos
- `Product` — `@ManyToOne` com `Category`
- `Category` — `@OneToMany` `products` (cascade + orphanRemoval)
- `Customer` — `@OneToMany` `addresses`, `orders` (cascade + orphanRemoval)
- `Address` — `@ManyToOne` com `Customer`
- `Order` — `@ManyToOne` `customer`, `@OneToMany` `items`
- `OrderItem` — `@ManyToOne` `order` e `product`
- `Cart` — `@OneToOne` `customer`, `@OneToMany` `items`
- `CartItem` — `@ManyToOne` `cart` e `product`
- `OrderStatus` — enum (`PENDING`, `PAID`, `SHIPPED`, `CANCELED`)
- `UserRole.java` — arquivo presente porém vazio (possível enum/entidade futura)

## Converters/Mappers
- `ProductConverter`: `toResponseDTO`, `toEntity`, `updateEntityFromDTO`
- `ProductDTOConverter`: conversões entre request/response DTOs
- `CategoryConverter`: `fromDTO`, `toResponseDTO`, `updateEntityFromDTO`
- `CustomerConverter`: `fromDTO`, `updateEntityFromDTO` (reconcilia endereços), `customerResponseDTO`
- `OrderConverter`: `fromDTO` (status `PENDING`, `createdAt`, total), `toResponseDTO`, `updateEntityFromDTO` (recalcula total)
- `OrderItemConverter`: `fromDTO` (usa `unitPrice` do DTO), `toResponseDTO`
- `CartItemConverter`: `fromDTO` (usa `product.price` para `unitPrice`), `toResponseDTO`
- `AddressConverter`: `fronDto` (typo), `updateAddress`, `toResponseDTO`
- `CartConverter`: `toResponseDTO` (calcula total), `fromDTO` placeholder; contém campos supérfluos

## Repositórios
- `ProductRepository`, `CategoryRepository`, `CustomerRepository`, `AddressRepository`, `OrderRepository`, `CartRepository`, `CartItemRepository` — todos `JpaRepository<..., Long>`.

## Tratamento de Erros
- Exceções utilizadas: `ResourceNotFoundException`, `IllegalArgumentException`, `DatabaseException`.
- Sugestão: adicionar `@ControllerAdvice` global para padronizar respostas de erro (por exemplo, 404, 400, 409).

## Observações e Melhorias
- Typos a corrigir: `AddressConverter.fronDto`, `CustomerService.creatCustomer`, parâmetro `productcToResponsDTO` em `ProductService`.
- Uniformizar regra de `unitPrice` entre carrinho e pedido.
- Implementar/limpar `CartConverter.fromDTO` e campos extras.
- Definir/implementar `UserRole` se necessário ou remover.
- Adicionar validações nos DTOs com Bean Validation (`@NotNull`, `@Size`, etc.).
- Documentação automática: considerar `springdoc-openapi` para Swagger UI.

## Estrutura do Projeto
```
fastshop/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/
    │   └── resources/
    └── test/
        ├── java/
        └── resources/
```