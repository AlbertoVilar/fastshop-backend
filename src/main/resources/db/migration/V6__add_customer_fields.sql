-- V6: Adicionar colunas de dados pessoais e auditoria em customers
-- Campos: birth_date (DATE), phone (VARCHAR), cpf_or_cnpj (VARCHAR), created_at/updated_at (TIMESTAMP)

ALTER TABLE customers
    ADD COLUMN IF NOT EXISTS birth_date DATE,
    ADD COLUMN IF NOT EXISTS phone VARCHAR(30),
    ADD COLUMN IF NOT EXISTS cpf_or_cnpj VARCHAR(30),
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

-- Índice único opcional para cpf_or_cnpj se desejado
-- CREATE UNIQUE INDEX IF NOT EXISTS customers_cpf_or_cnpj_key ON customers(cpf_or_cnpj);