-- V9: Adiciona a coluna de controle de versão ao carrinho
-- Corrige erro: "column c1_0.version does not exist" ao consultar carts

ALTER TABLE carts
    ADD COLUMN IF NOT EXISTS version BIGINT;