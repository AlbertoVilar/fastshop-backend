# Relatório de Auditoria — FastShop (Local)

**Resumo**
- Validado o comportamento dos endpoints de deleção de itens do carrinho.
- Quando o item não existe no carrinho, o serviço lança `ResourceNotFoundException` e a API retorna `404 Not Found` com `StandardError`.
- Quando o item existe e é removido, a API retorna `204 No Content` (deleção bem-sucedida).

**Diagnóstico Técnico**
- `CartService.removeItemFromCart` e `removeItemFromAuthenticatedCart` verificam `cart.removeItem(productId)` e lançam `ResourceNotFoundException` quando `false`.
- `CartController` responde `204 No Content` após a execução bem-sucedida do serviço.
- `GlobalExceptionHandler` mapeia `ResourceNotFoundException` para `404 Not Found` com o `StandardError` definido em `REQUISITOS-DO-SISTEMA.MD`.

**Ambiente e Build**
- Ambiente local usando `compose.yml` com imagem `fastshop-backend:local` e perfil ativo `prod`.
- Imagem reconstruída com `--no-cache` para garantir inclusão de alterações.
- Saúde da aplicação confirmada via `GET /actuator/health` retornando `UP`.

**Validações Executadas (PowerShell)**
- Autenticação como admin e teste de deleção com item inexistente:
  - `Invoke-RestMethod -Method Post -Uri http://localhost:8080/auth/login -ContentType 'application/json' -Body (@{ username = "albertovilar1@gmail.com"; password = "132747" } | ConvertTo-Json)`
  - `curl.exe -H "Authorization: Bearer <TOKEN>" -X DELETE "http://localhost:8080/carts/4/items/6" -i`
  - Resposta: `404 Not Found` com `message: Item do carrinho não encontrado para o produto: 6`.
- Criação de carrinho e deleção de item inexistente:
  - `Invoke-RestMethod -Method Post -Uri http://localhost:8080/carts -ContentType 'application/json' -Headers @{ Authorization = "Bearer <TOKEN>" } -Body (@{ customerId = 1 } | ConvertTo-Json)`
  - `curl.exe -H "Authorization: Bearer <TOKEN>" -X DELETE "http://localhost:8080/carts/<CART_ID>/items/9999" -i`
  - Resposta: `404 Not Found` com `StandardError`.
- Deleção de item existente (quando presente no carrinho):
  - `curl.exe -H "Authorization: Bearer <TOKEN>" -X DELETE "http://localhost:8080/carts/<CART_ID>/items/<PRODUCT_ID_EXISTENTE>" -i`
  - Resposta: `204 No Content`.

**Recomendações de Versionamento (Git)**
- Mensagem de commit (Conventional Commits):
  - `docs: registrar auditoria da lógica de deleção de itens do carrinho`
- Branch de trabalho recomendado:
  - Criar a partir da `main`: `docs/auditoria-cart-delete-404` (ou usar a última branch ativa, se alinhada ao fluxo).
- Fluxo sugerido:
  - Validar testes locais (`mvn -q -DskipTests=false test`) e build (`mvn -q -DskipTests package`).
  - `git add AUDIT-RELATORIO.md`
  - `git commit -m "docs: registrar auditoria da lógica de deleção de itens do carrinho"`
  - `git push -u origin docs/auditoria-cart-delete-404`
  - Abrir PR com descrição e evidências (logs/saídas acima) e fazer merge (preferencialmente squash) para `main` após CI verde.

**Observações**
- Não há mudanças de código necessárias neste tópico; a documentação foi atualizada para refletir o diagnóstico e validações.
- Caso deseje, podemos adicionar uma nota breve no `README_pt.md` em "Rotas do Carrinho" indicando o comportamento de `DELETE` (404 quando item inexistente; 204 quando sucesso).

Este relatório consolida as validações realizadas sobre autenticação, autorização, exposição de endpoints de observabilidade (Actuator) e configurações operacionais em produção, além de documentar alterações de documentação aplicadas no repositório.

## Contexto
- Objetivo: validar e documentar o endpoint `GET /users/me` e o estado dos mapeamentos do Actuator em produção, garantindo regras de segurança adequadas.
- Ambiente: containers via Compose com `SPRING_PROFILES_ACTIVE=prod`.
- Observação: este arquivo é local-only e está ignorado pelo `.gitignore`.

## Validações Executadas
- Autenticação:
  - Login bem-sucedido com `alex@gmail.com`/`132747`.
  - Token JWT obtido com `ROLE_CUSTOMER`.
- Endpoint `GET /users/me`:
  - Retorno dos dados do usuário autenticado (Alex) com header `Authorization: Bearer <token>`.
  - Confirmação de regra de segurança aplicada em `SecurityConfig` (`authenticated()`).
- Actuator:
  - `GET /actuator/health` acessível: `{"status":"UP"}`.
  - `GET /actuator/mappings` acessível; estrutura `contexts.fastshop.mappings` presente, porém sem listagem textual direta de strings dos endpoints (comportamento do Spring Boot 3). A chave `dispatcherServlet` existe, mas seu conteúdo não inclui nomes de endpoints em formato simplificado.

## Segurança e Configuração
- `SecurityConfig` (trecho relevante):
  - `requestMatchers(HttpMethod.GET, "/users/me").authenticated()`.
  - `requestMatchers("/users/**").hasRole("ADMIN")` (CRUD de usuários segue restrito a admin).
- Variáveis de ambiente (prod):
  - `RESET_ADMIN_PASSWORD=true`, `RESET_ADMIN_USERNAME=albertovilar1@gmail.com`, `RESET_ADMIN_PLAIN_PASSWORD=132747` — habilitadas no Compose atual, adequadas SOMENTE para dev; recomendação: desativar em produção.
- Flyway:
  - Desabilitado em `prod`; recomendação: reativar após revisar/ajustar migrations.

## Actuator e Mapeamentos
- Endpoints expostos: `health`, `info`, `mappings`, `env` (conforme `application-prod.properties`).
- O payload de `/actuator/mappings` não apresenta uma listagem simples contendo `"/users/me"`, o que é consistente com serialização mais estruturada do Spring Boot 3; o endpoint está funcional e registrado.

## Evidências
- Login (cliente Alex) → token com `ROLE_CUSTOMER`.
- `GET /users/me` → resposta `200 OK` com DTO de usuário.
- `GET /actuator/health` → `UP`.
- `GET /actuator/mappings` → resposta presente, com chaves de contexto e `dispatcherServlet`.

## Recomendações de Produção
- Desativar variáveis `RESET_ADMIN_*` em produção; rotacionar credenciais.
- Reativar `Flyway` no perfil `prod` após correção das migrations.
- Manter `JWT_SECRET` forte e segredos fora do versionamento (`.env`).
- Preferir deploy atrás de proxy com TLS.

## Alterações de Documentação (aplicadas)
- `README_pt.md`: adicionada seção de Usuários e exemplo `GET /users/me`.
- `ARCHITECTURE.md`: adicionada menção do `GET /users/me` na seção de autorização.
- `README.md`: adicionada documentação de `GET /users/me`.
- `REQUISITOS-DO-SISTEMA.MD`: incluído `GET /users/me` em Perfis/Autorização e Endpoints.

## Próximos Passos
- Adicionar testes automatizados de autorização para `GET /users/me` no perfil de testes (H2).
- Opcional: expor documentação automatizada (OpenAPI/Swagger).
- Monitorar `mappings` em ambientes com observabilidade adicional (logs de mapeamento em startup).