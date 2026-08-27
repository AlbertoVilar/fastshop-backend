# Relatório de auditoria — FastShop

## Escopo

Validação local da remoção de itens do carrinho e dos endpoints autenticados.

## Resultados

- A remoção de item inexistente retorna `404 Not Found`.
- A remoção bem-sucedida retorna `204 No Content`.
- `GET /users/me` exige autenticação.
- `GET /actuator/health` retornou `UP` no ambiente local.

## Segurança

- Credenciais, tokens e arquivos temporários de login não fazem parte do repositório.
- O Compose recebe variáveis sensíveis exclusivamente de `.env`, que deve permanecer fora do versionamento.
- O reset de senha administrativa fica desativado por padrão e só deve ser habilitado temporariamente em desenvolvimento local.
- Em produção, mantenha os segredos em um gerenciador apropriado e use TLS.

## Próximos passos

- Adicionar testes automatizados de autorização para `GET /users/me`.
- Revisar as migrations antes de habilitar Flyway no perfil de produção.
- Adicionar documentação OpenAPI/Swagger.
