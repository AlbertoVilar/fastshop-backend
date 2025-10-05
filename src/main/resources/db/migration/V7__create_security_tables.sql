-- V7__create_security_tables.sql

-- 1. Tabela de Permissões (Roles)
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    authority VARCHAR(255) UNIQUE NOT NULL -- Corrigido para AUTHORITY
);

-- 2. Tabela de Usuários (Users)
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW() NOT NULL
);

-- 3. Tabela de Junção (Many-to-Many)
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);