## Grupo turma 2026


- Caio
- Caua
- Diogo
- Gabriel Braga
- Gabriel Paulino
- Raul
- Samuel
- Vinycius
- Vitor

## Como rodar o programa

Pré-requisitos de execução (Precisam estar instalados na máquina)

 - Docker Desktop (24.x+)

 - Docker Compose (2.x+)
 
 - Java JDK (21+)
 
 - Apache Maven (3.9+)
 
 - Node.js (18+)
 
 - MySQL (8.x)
 
 - Git

## Execução em Docker (Recomendado)

① Clone o repositório e acesse a branch principal com o seguinte código:

	git clone https://github.com/mamazzei-edu/ubs-repo.git
	cd ubs-repo
	git checkout devel_producao // ou alguma outra branch dependendo do que você deseja.

② Crie o arquivo de variáveis de ambiente:
	Na raiz do projeto, crie um arquivo chamado .env com as seguintes variáveis:
	
	DB_ROOT_PASSWORD=12345678
	DB_NAME=ubs
	DB_USER=root
	DB_PASSWORD=12345678
	DB_HOST=mysql_server
	DB_PORT=3306
	DB_SCHEMA=ubs
	SERVER_PORT=8080
	SECURITY_JWT_SECRET_KEY=7a8b3c9d1e2f4a5b6c7d8e9f0a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2e3f4
	SECURITY_JWT_EXPIRATION_TIME=3600000
	SPRING_JPA_HIBERNATE_DDL_AUTO=update

③ Suba todos os serviços:

	docker compose up --build

④ Aguarde a inicialização completa:
	- O Spring Boot iniciará e criará automaticamente as tabelas no banco de dados
	- Os seeders (RoleSeeder e AdminSeeder) popularão os dados iniciais
	- Aguarde a mensagem de startup do Spring Boot no terminal

⑤ Acesse a aplicação:
- Frontend (Angular): http://localhost:4200
- Backend (API REST): http://localhost:8080
- MySQL (conexão externa): localhost:3307

Para encerrar os containers:

	docker compose down

Para encerrar e remover também o volume do banco de dados:

	docker compose down -v

## Execução Manual (Desenvolvimento)

① Backend:

	cd backend
	# Configure o arquivo src/main/resources/application.properties com as credenciais do MySQL
	./mvnw spring-boot:run

② Frontend:

	cd frontend
	npm install
	npm start    # ou: ng serve

③ Acesse:
- Angular: http://localhost:4200 (com proxy configurado para o backend)
- Certifique-se de que o schema "ubs" foi criado previamente no MySQL
