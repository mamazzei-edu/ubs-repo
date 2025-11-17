-- Script para criar usuário Super Admin
USE ubs;

-- Inserir role SUPER_ADMIN se não existir
INSERT INTO roles (id, name, description, created_at, updated_at)
SELECT 1, 'SUPER_ADMIN', 'Super Administrador do Sistema', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'SUPER_ADMIN');

-- Inserir usuário super admin
-- Senha: 123456 (hash BCrypt)
-- Hash gerado: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
INSERT INTO users (id, full_name, email, password, matricula, username, crm, role_id, created_at, updated_at)
VALUES (
    1,
    'Super Admin',
    'super.admin@email.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'ADMIN001',
    'super.admin',
    NULL,
    1,
    NOW(),
    NOW()
) ON DUPLICATE KEY UPDATE
    email = 'super.admin@email.com',
    password = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy';

-- Verificar se foi criado
SELECT id, full_name, email, matricula, role_id FROM users WHERE email = 'super.admin@email.com';
