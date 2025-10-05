-- V8__seed_security_data.sql

-- 1. INSERÇÃO DAS ROLES (Permissões)
INSERT INTO roles (id, authority) VALUES (1, 'ROLE_ADMIN');
INSERT INTO roles (id, authority) VALUES (2, 'ROLE_CUSTOMER');

-- 2. INSERÇÃO DO USUÁRIO ADMINISTRADOR/OPERADOR (ID 1)
-- User: albertovilar1@gmail.com | Senha: 132747
INSERT INTO users (id, username, password, created_at) VALUES (
    1,
    'albertovilar1@gmail.com',
    '$2a$10$QwS6UVcNoS5wSpURmkJbS.Wd4fEclOaClMQKaQ9JjOkCI8ZtaTGVm', -- Senha criptografada
    NOW()
);

-- 3. INSERÇÃO DO CLIENTE CORRESPONDENTE (ID 1)
INSERT INTO customers (id, name, email, phone, cpf_or_cnpj, birth_date)
VALUES (
    1,
    'Alberto Vilar',
    'albertovilar1@gmail.com',
    '11987654321',
    '11122233344',
    '1990-01-01'
)
ON CONFLICT (id) DO NOTHING;

-- 4. INSERÇÃO DO USUÁRIO NORMAL / CLIENTE (ID 2)
-- User: alex@gmail.com | Senha: 132747
INSERT INTO users (id, username, password, created_at) VALUES (
    2,
    'alex@gmail.com',
    '$2a$10$QwS6UVcNoS5wSpURmkJbS.Wd4fEclOaClMQKaQ9JjOkCI8ZtaTGVm', -- Senha criptografada
    NOW()
);

-- 5. INSERÇÃO DO CLIENTE CORRESPONDENTE (ID 2)
INSERT INTO customers (id, name, email, phone, cpf_or_cnpj, birth_date)
VALUES (
    2,
    'Alex Green',
    'alex@gmail.com',
    '977777777',
    '99988877766',
    '1987-12-13'
)
ON CONFLICT (id) DO NOTHING;

-- 6. INSERÇÃO DOS RELACIONAMENTOS (user_roles)
-- Associa Usuário 1 (Admin) à Role 1 (ROLE_ADMIN)
INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);
-- Associa Usuário 2 (Alex) à Role 2 (ROLE_CUSTOMER)
INSERT INTO user_roles (user_id, role_id) VALUES (2, 2);

-- 7. RESETAR SEQUÊNCIAS APÓS SEEDING
-- Garante que novos registros iniciem a contagem após o último ID usado (2)
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('roles_id_seq', (SELECT MAX(id) FROM roles));
SELECT setval('customers_id_seq', (SELECT MAX(id) FROM customers));