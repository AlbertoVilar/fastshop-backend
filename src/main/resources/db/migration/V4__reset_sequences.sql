-- V4: Reset sequences after initial data inserts
-- Ajusta as sequências BIGSERIAL para refletirem o último ID inserido
-- Útil quando há inserts iniciais (via migrations ou import.sql)

-- Customers
SELECT setval('customers_id_seq', COALESCE((SELECT MAX(id) FROM customers), 1), true);

-- Products
SELECT setval('products_id_seq', COALESCE((SELECT MAX(id) FROM products), 1), true);

-- Se houver outras tabelas com BIGSERIAL e dados iniciais, repetir aqui