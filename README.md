# Fastshop Backend

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
- Recomendações: adicionar smoke test do `/actuator/health`, badges de status, e dependência de `db` saudável com `condition: service_healthy` caso necessário.

## Solução de Problemas
- Healthcheck falhando em Alpine: já usamos `wget` no Compose para compatibilidade.
- Banco não pronto ao iniciar o app: ver `depends_on`; considere `condition: service_healthy`.
- Portas ocupadas: verifique `:8080` e `:5432` no host.

## Licença e Créditos
Projeto educacional/pessoal. Ajuste conforme sua política de licenciamento.

## Contato
- GitHub: `https://github.com/AlbertoVilar`
- LinkedIn: `https://www.linkedin.com/in/alberto-vilar-316725ab/`