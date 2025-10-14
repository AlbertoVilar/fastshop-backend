-- V10: Alinhar tabela users com customers por email
-- Objetivo: garantir que todo usuário possua um customer correspondente com o mesmo email
-- Estratégia: inserir customers ausentes com base em users.username (email)

-- 1) Criar customers para quaisquer users que não possuam customer com o mesmo email
INSERT INTO customers (name, email)
SELECT 
    INITCAP(SPLIT_PART(u.username, '@', 1)) AS name,
    u.username AS email
FROM users u
LEFT JOIN customers c ON c.email = u.username
WHERE c.id IS NULL;

-- 2) Ajustar sequência da tabela customers após possíveis inserts
SELECT setval('customers_id_seq', COALESCE((SELECT MAX(id) FROM customers), 1), true);

-- Observações:
-- - Este script cobre apenas a criação inicial de correspondências.
-- - Inserções futuras na tabela users devem ser acompanhadas por lógica de aplicação
--   (ex.: CommandLineRunner) se desejar manter sincronização contínua.