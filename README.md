# UBS — Sistema de Gestão de Unidade Básica de Saúde

Sistema web para gestão de uma **Unidade Básica de Saúde (UBS)**, contemplando o
cadastro de pacientes e médicos, agendamento de consultas, controle de status dos
atendimentos (confirmado, cancelado, realizado, falta) e autenticação de usuários
via **JWT**. O projeto também conta com um aplicativo Android (`UBSAndroidAPP`) e um
serviço auxiliar de descoberta de rede (`nsd_server`).

> Projeto acadêmico desenvolvido pela turma de 2026 (FATEC).

---

## 📦 Estrutura do repositório

| Pasta             | Descrição                                                             |
| ----------------- | --------------------------------------------------------------------- |
| `backend/`        | API REST em **Spring Boot** (Java 21, Maven, MySQL, JWT)              |
| `frontend/`       | Aplicação web em **Angular 20**, servida em produção via **Nginx**     |
| `nsd_server/`     | Serviço **Node.js** de descoberta de rede (Bonjour/mDNS) — opcional   |
| `UBSAndroidAPP/`  | Aplicativo Android cliente                                            |
| `compose.yaml`    | Orquestração de todos os serviços com Docker Compose                  |

---

## 🛠️ Tecnologias utilizadas

### Backend
- **Java 21**
- **Spring Boot 3.5.5** (Web, Data JPA, Security, Thymeleaf)
- **Maven** (com wrapper `mvnw`)
- **MySQL** (driver `mysql-connector-j`)
- **JWT** (biblioteca `jjwt` 0.13.0) para autenticação
- **PDFBox / iText** para geração de PDFs
- **spring-dotenv** para leitura de variáveis de ambiente

### Frontend
- **Angular 20** (standalone components)
- **Angular Material** + **CDK**
- **RxJS**, **Moment.js**, **ngx-cookie-service**
- **TypeScript 5.9**
- **Nginx** (servidor estático no container de produção)

### Serviço de rede (opcional)
- **Node.js** + **Express** + **bonjour-service** (mDNS/NSD)

### Infraestrutura
- **Docker** & **Docker Compose**
- **MySQL** (container)

---

## ✅ Pré-requisitos

Para rodar tudo via Docker (forma recomendada), você só precisa de:

- [Docker](https://docs.docker.com/get-docker/) **20+**
- [Docker Compose](https://docs.docker.com/compose/) (já incluso no Docker Desktop)

Não é necessário ter Java, Node ou MySQL instalados na máquina — tudo roda dentro
dos containers.

---

## 🚀 Como rodar localmente (frontend + backend + banco)

Esta é a forma mais simples: um único comando sobe o **banco de dados**, o
**backend** e o **frontend** de uma vez.

### 1. Clone o repositório

```bash
git clone https://github.com/mamazzei-edu/ubs-repo.git
cd ubs-repo
```

### 2. Crie o arquivo de variáveis de ambiente

O Compose lê as configurações de um arquivo `.env` na raiz do projeto. Use o
modelo fornecido:

```bash
# Linux / macOS
cp .env.example .env

# Windows (PowerShell)
Copy-Item .env.example .env
```

> Os valores padrão do `.env.example` já funcionam para ambiente local. Ajuste
> senhas e a chave JWT se desejar. **Nunca commite o arquivo `.env`** — ele já
> está no `.gitignore`.

### 3. Suba os serviços

```bash
docker compose up --build
```

A primeira execução é mais demorada, pois faz o build do backend (Maven) e do
frontend (Angular). Para rodar em segundo plano, adicione `-d`:

```bash
docker compose up --build -d
```

### 4. Acesse a aplicação

| Serviço            | URL / Endereço                  |
| ------------------ | ------------------------------- |
| **Frontend (web)** | http://localhost:4200           |
| **Backend (API)**  | http://localhost:8080           |
| **MySQL**          | `localhost:3307` (porta externa)|

> ℹ️ O frontend faz chamadas diretas do navegador para `http://localhost:8080`,
> por isso o backend é exposto nessa porta no host.

### 5. Parar os serviços

```bash
# Para os containers (mantém os dados do banco)
docker compose down

# Para os containers e APAGA o volume do banco de dados
docker compose down -v
```

---

## 🔌 Serviços e portas

| Serviço        | Porta no container | Porta no host | Observação                          |
| -------------- | ------------------ | ------------- | ----------------------------------- |
| `frontend`     | 8080 (Nginx)       | **4200**      | Interface web                       |
| `backend`      | 8080               | **8080**      | API REST Spring Boot                |
| `mysql_server` | 3306               | **3307**      | Banco de dados (dados em volume)    |

O backend só inicia **após** o MySQL estar saudável (healthcheck configurado no
`compose.yaml`), evitando erros de conexão na primeira subida.

> O serviço `nsd_server` está comentado no `compose.yaml` por depender de
> `network_mode: host`, que não funciona corretamente no Docker Desktop para
> Windows/macOS. Em Linux ele pode ser habilitado removendo os comentários.

---

## 🧩 Variáveis de ambiente

Todas as variáveis consumidas pelo Compose estão documentadas no arquivo
[`.env.example`](.env.example). As principais:

| Variável                        | Descrição                                       |
| ------------------------------- | ----------------------------------------------- |
| `DB_NAME` / `DB_USER` / `DB_PASSWORD` | Credenciais do banco MySQL                 |
| `DB_ROOT_PASSWORD`              | Senha do usuário `root` do MySQL                |
| `SPRING_DATASOURCE_URL`         | URL JDBC de conexão do backend com o MySQL      |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Estratégia de DDL do Hibernate (`update`)       |
| `SECURITY_JWT_SECRET_KEY`       | Chave secreta (Base64) para assinatura do JWT   |
| `SECURITY_JWT_EXPIRATION_TIME`  | Expiração do token JWT em milissegundos         |
| `SERVER_PORT`                   | Porta interna do backend (8080)                 |

---

## 💻 Rodando sem Docker (desenvolvimento)

Caso prefira rodar cada parte separadamente durante o desenvolvimento:

### Backend (Spring Boot)

Requer **Java 21** e um MySQL acessível. Defina as variáveis de ambiente
(ou use o `.env`) e execute:

```bash
cd backend

# Linux / macOS
./mvnw spring-boot:run

# Windows
.\mvnw.cmd spring-boot:run
```

A API ficará disponível em http://localhost:8080.

### Frontend (Angular)

Requer **Node.js** e o **Angular CLI**.

```bash
cd frontend
npm install
npm start
```

A aplicação ficará disponível em http://localhost:4200 (servidor de
desenvolvimento do Angular) e consumirá a API em `http://localhost:8080`.

---

## 🩺 Solução de problemas

- **`docker compose` não encontra variáveis**: confirme que o arquivo `.env`
  existe na raiz do projeto (passo 2).
- **Backend não conecta no banco**: aguarde alguns segundos na primeira
  execução; o healthcheck do MySQL pode levar até ~30s. O backend tem
  `restart: always` e tentará reconectar automaticamente.
- **Porta em uso**: ajuste o mapeamento de portas no `compose.yaml` caso
  4200, 8080 ou 3307 já estejam ocupadas na sua máquina.
- **Rebuild forçado**: `docker compose build --no-cache` seguido de
  `docker compose up`.
