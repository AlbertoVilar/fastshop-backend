-- data.sql para perfil de teste (H2 em memória)
-- Inclui roles, usuários e relacionamento user_roles

-- ROLES
INSERT INTO roles (id, authority) VALUES (1, 'ROLE_ADMIN');
INSERT INTO roles (id, authority) VALUES (2, 'ROLE_CUSTOMER');

-- Usuario: albertovilar1@gmail.com | Senha: 132747 (BCrypt)
INSERT INTO users (id, username, password) VALUES (
  1,
  'albertovilar1@gmail.com',
  '$2a$10$QwS6UVcNoS5wSpURmkJbS.Wd4fEclOaClMQKaQ9JjOkCI8ZtaTGVm'
);

-- Usuario: alex@gmail.com | Senha: 132747 (BCrypt)
INSERT INTO users (id, username, password) VALUES (
  2,
  'alex@gmail.com',
  '$2a$10$QwS6UVcNoS5wSpURmkJbS.Wd4fEclOaClMQKaQ9JjOkCI8ZtaTGVm'
);

-- USER_ROLES
INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);
INSERT INTO user_roles (user_id, role_id) VALUES (2, 2);

-- Removido: inserts de categorias/produtos para evitar conflito
-- com import.sql (executado pelo Hibernate em create-drop).