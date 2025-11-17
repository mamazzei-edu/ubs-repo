-- Criar usuário e banco de dados para o projeto UBS
CREATE DATABASE IF NOT EXISTS ubs;
CREATE USER IF NOT EXISTS 'ubs'@'localhost' IDENTIFIED BY 'ubs123';
GRANT ALL PRIVILEGES ON ubs.* TO 'ubs'@'localhost';
FLUSH PRIVILEGES;

-- Verificar
SELECT User, Host FROM mysql.user WHERE User = 'ubs';
