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

-- Usuario: maria@email.com | Senha: 132747 (BCrypt)
INSERT INTO users (id, username, password) VALUES (
  3,
  'maria@email.com',
  '$2a$10$QwS6UVcNoS5wSpURmkJbS.Wd4fEclOaClMQKaQ9JjOkCI8ZtaTGVm'
);

INSERT INTO user_roles (user_id, role_id) VALUES (3, 2);


-- Removido: inserts de categorias/produtos para evitar conflito
-- com import.sql (executado pelo Hibernate em create-drop).

-- -----------------------------------------------
-- Seeds adicionais para testes de integração
-- Necessários para CartControllerIT (customers, categories, products)
-- -----------------------------------------------

-- Clientes (devem existir para criar carrinho e validar propriedade)
INSERT INTO customers (id, name, email) VALUES (1, 'Alex Green', 'alex@gmail.com');
INSERT INTO customers (id, name, email) VALUES (2, 'Maria Oliveira', 'maria@email.com');

-- Categorias mínimas para relacionamento de produtos
INSERT INTO categories (id, name, description) VALUES (1, 'Eletrônicos', 'Categoria base para testes');

-- Produto esperado pelos testes (id=1, price=4500.00)
INSERT INTO products (id, name, description, price, stock, image_url, category_id)
VALUES (1, 'Notebook Dell', 'Notebook i7 16GB RAM SSD 512GB', 4500.00, 10, 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8', 1);

-- Ajuste das sequências de IDs para evitar conflito com inserts explícitos
ALTER TABLE categories ALTER COLUMN id RESTART WITH 2;
ALTER TABLE products ALTER COLUMN id RESTART WITH 2;