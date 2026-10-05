# Projeto UBS — roteiro de instalação

Aplicação com backend Spring Boot, frontend Angular, banco MySQL e um servidor
Nginx, todos em contêineres Docker. O roteiro abaixo leva da clonagem do
repositório até a aplicação no ar.

Tempo estimado: 15 a 25 minutos, a maior parte esperando downloads.

---

## 1. Pré-requisitos

Instale antes de começar:

| Programa | Versão | Para quê |
|---|---|---|
| **Git** | qualquer recente | clonar o repositório |
| **Docker Desktop** | com Docker Compose v2 | executar os contêineres |
| **Node.js** | 20.19+ ou 22.12+ | compilar o frontend Angular |

Não é necessário instalar Java nem Maven: o backend é compilado dentro da
própria imagem Docker.

Confira se está tudo certo:

```bash
git --version
docker --version
docker compose version
node --version
npm --version
```

O Docker Desktop precisa estar **em execução** (ícone da baleia ativo) antes
dos passos 5 e 6.

---

## 2. Clonar o repositório

```bash
git clone https://github.com/mamazzei-edu/ubs-repo.git
cd ubs-repo
```

Todos os comandos seguintes são executados a partir desta pasta, salvo quando
indicado o contrário.

---

## 3. Criar o arquivo `.env` (obrigatório)

Este é o passo mais importante. O arquivo `.env` **não vem no repositório** (ele
contém senhas e está no `.gitignore`), e **sem ele nada funciona**.

O repositório traz um modelo pronto, `.env.example`. Copie-o:

```bash
# Linux/macOS
cp .env.example .env

# Windows
copy .env.example .env
```

Abra o `.env` recém-criado num editor de texto e substitua os quatro valores
marcados com `ALTERAR`:

| Variável | O que colocar |
|---|---|
| `DB_PASSWORD` | uma senha de sua escolha para o usuário da aplicação |
| `DB_ROOT_PASSWORD` | uma senha de sua escolha para o `root` do MySQL |
| `SPRING_DATASOURCE_PASSWORD` | **a mesma** de `DB_ROOT_PASSWORD` |
| `SECURITY_JWT_SECRET_KEY` | no mínimo 64 caracteres aleatórios |

O próprio `.env.example` traz, em comentários, a explicação de cada variável.

### Cuidados com os valores

- **`SECURITY_JWT_SECRET_KEY`** — use pelo menos **64 caracteres** aleatórios.
  Chave curta derruba o backend na partida. Para gerar uma:
  ```bash
  # Linux/macOS
  openssl rand -base64 64 | tr -d '\n='
  ```
  ```powershell
  # Windows PowerShell
  -join ((48..57) + (65..90) + (97..122) | Get-Random -Count 72 | ForEach-Object {[char]$_})
  ```
- **`DB_USER` não pode ser `root`** — a imagem oficial do MySQL recusa subir
  nesse caso. O modelo já vem com `ubs_app`.
- **`SPRING_DATASOURCE_PASSWORD`** precisa ser igual a `DB_ROOT_PASSWORD`
  (o backend conecta como `root`).
- **Sem espaços em volta do `=`** e sem aspas nos valores.
- **`FRONT_BASE_PATH` e `API_BASE_PATH`** devem começar com `/` e **não** devem
  terminar com `/`. São os caminhos pelos quais a aplicação responde:
  `http://localhost:8081/ubs/` e `http://localhost:8081/ubs-api/`.

---

## 4. Ajustar os caminhos no frontend

Se você **alterou** `FRONT_BASE_PATH` ou `API_BASE_PATH` no passo anterior, dois
arquivos precisam acompanhar. Eles valem para o modo de desenvolvimento
(`ng serve`); o contêiner lê os caminhos direto do `.env`.

Com os valores padrão do modelo (`/ubs` e `/ubs-api`), este passo pode ser
dispensado — os dois arquivos já vêm ajustados.

**`frontend/angular.json`** — procure `baseHref` e `servePath` e deixe os dois
iguais ao `FRONT_BASE_PATH`, **com barra no final**:

```json
"baseHref": "/ubs/",
...
"servePath": "/ubs/",
```

**`frontend/proxy.conf.json`** — a chave deve ser igual ao `API_BASE_PATH`,
sem barra no final:

```json
{
  "/ubs-api": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true
  }
}
```

> Os três valores (`.env`, `angular.json` e `proxy.conf.json`) precisam
> combinar. Divergência entre eles é a causa mais comum de "a tela abre em
> branco" ou "o login não responde".

---

## 5. Compilar o frontend

O contêiner do Nginx **copia o resultado da compilação**; ele não compila o
Angular. Portanto este passo vem antes de subir os contêineres.

```bash
cd frontend
npm ci
npm run build
cd ..
```

A compilação cria a pasta `frontend/dist/frontend/browser`. Se ela não existir,
o passo 6 falha com erro de `COPY`.

Repita este passo sempre que alterar o código do frontend.

---

## 6. Subir a aplicação

```bash
docker compose up -d --build
```

A primeira execução baixa as imagens e compila o backend — pode levar vários
minutos. Acompanhe:

```bash
docker compose ps
docker compose logs -f backend
```

Espere a linha `Started BackendApplication in ... seconds` nos logs do backend.
Pressione `Ctrl+C` para sair do acompanhamento (isso não derruba os
contêineres).

---

## 7. Acessar

Abra o navegador em:

```
http://localhost:8081/ubs/
```

(trocando `/ubs` pelo valor que você usou em `FRONT_BASE_PATH`)

A raiz `http://localhost:8081/` redireciona automaticamente para esse endereço.

### Primeiro acesso

| Campo | Valor |
|---|---|
| E-mail | `super.admin@email.com` |
| Senha | `123456` |

Esse usuário é criado automaticamente na primeira partida do backend.

> **Troque esta senha antes de usar o sistema com dados reais.** Ela está
> escrita no código-fonte e é pública.

---

## 8. Comandos do dia a dia

```bash
# Parar tudo (os dados do banco são preservados)
docker compose stop

# Religar
docker compose start

# Derrubar e remover os contêineres (dados preservados)
docker compose down

# Ver o que está rodando
docker compose ps

# Logs de um serviço
docker compose logs -f backend
docker compose logs -f nginx-ubs

# Após alterar o código do FRONTEND
cd frontend && npm run build && cd ..
docker compose build nginx-ubs && docker compose up -d nginx-ubs

# Após alterar o código do BACKEND
docker compose build backend && docker compose up -d backend

# Após alterar APENAS os caminhos no .env (sem recompilar nada)
docker compose up -d

# APAGAR o banco e recomeçar do zero — perde todos os dados
docker compose down -v
```

---

## 9. Portas utilizadas

| Porta | Serviço |
|---|---|
| 8081 | Nginx (a aplicação) |
| 8080 | Backend Spring Boot |
| 3306 | MySQL |

Se alguma delas já estiver ocupada na sua máquina — é comum ter um MySQL local
na 3306 —, altere o lado esquerdo do mapeamento no `compose.yaml`. Exemplo,
para publicar o MySQL na 3307:

```yaml
    ports:
      - "3307:3306"
```

Só o lado de fora muda; a configuração do backend continua apontando para
`mysql_server:3306`, que é o endereço interno da rede do Docker.

---

## 10. Se algo der errado

**O backend reinicia sem parar.**
`docker compose logs backend`. As causas mais comuns:
- `Communications link failure` → o `SPRING_DATASOURCE_URL` não aponta para
  `mysql_server:3306`, ou a senha não confere com `DB_ROOT_PASSWORD`.
- `WeakKeyException` ou erro de chave → `SECURITY_JWT_SECRET_KEY` curta demais.
- Erro ao criar índice único em `cpf` → só ocorre em banco já populado, quando
  há CPFs repetidos ou em branco; em instalação nova não acontece.

**A página abre em branco, ou os arquivos `.js` dão 404.**
Os caminhos não estão combinando. Confira `FRONT_BASE_PATH` no `.env` e o
`baseHref` no `angular.json`, e lembre que a URL precisa terminar com barra:
`/ubs/`, não `/ubs`.

**A tela abre mas o login não responde.**
Abra o console do navegador (F12). Erro de CORS significa que o endereço usado
não está em `LISTA_HOSTS`. Erro 404 nas chamadas significa que `API_BASE_PATH`
não está combinando entre o `.env` e o `proxy.conf.json`.

**`docker compose up` falha no `COPY frontend/dist`.**
O passo 5 não foi executado, ou falhou. Rode `npm run build` dentro de
`frontend` e confira se a pasta `frontend/dist/frontend/browser` existe.

**Porta já em uso.**
Mensagem `bind: address already in use`. Veja o passo 9.

---

## 11. Desenvolvimento (opcional)

Para trabalhar no frontend com recarga automática, sem reconstruir a imagem a
cada alteração:

```bash
# Em um terminal: sobe só o backend e o banco
docker compose up -d backend mysql_server

# Em outro: o servidor de desenvolvimento do Angular
cd frontend
npm start
```

A aplicação fica em `http://localhost:4200/ubs/`, e o `proxy.conf.json`
encaminha as chamadas da API para o backend na 8080.
