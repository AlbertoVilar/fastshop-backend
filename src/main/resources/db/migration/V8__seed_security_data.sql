-- V8__seed_security_data.sql
-- The seeded accounts intentionally have an unusable random password hash.
-- For local development, set a password only through local environment variables.

INSERT INTO roles (id, authority) VALUES (1, 'ROLE_ADMIN');
INSERT INTO roles (id, authority) VALUES (2, 'ROLE_CUSTOMER');

INSERT INTO users (id, username, password, created_at) VALUES
  (1, 'admin@fastshop.local', '$2a$10$m/NGLzcdXr0ONpgqfG9SoeJDug8YSRMm7bO2rbsT7JGsuxTlfsRWC', NOW()),
  (2, 'customer@fastshop.local', '$2a$10$m/NGLzcdXr0ONpgqfG9SoeJDug8YSRMm7bO2rbsT7JGsuxTlfsRWC', NOW());

INSERT INTO customers (id, name, email, phone, cpf_or_cnpj, birth_date) VALUES
  (1, 'FastShop Admin', 'admin@fastshop.local', '00000000000', '00000000000', '1990-01-01'),
  (2, 'FastShop Customer', 'customer@fastshop.local', '00000000001', '00000000001', '1990-01-02')
ON CONFLICT (id) DO NOTHING;

INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);
INSERT INTO user_roles (user_id, role_id) VALUES (2, 2);

SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('roles_id_seq', (SELECT MAX(id) FROM roles));
SELECT setval('customers_id_seq', (SELECT MAX(id) FROM customers));
