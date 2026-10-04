# Sistema UBS

Sistema de gestão de uma Unidade Básica de Saúde: cadastro de pacientes,
médicos e usuários, e agendamento de consultas. Tem uma versão **web** e um
**app de celular** para os agendamentos.

## Grupos turma 2026

- Caio
- Caua
- Diogo
- Gabriel Braga
- Gabriel Paulino
- Raul
- Samuel
- Vinycius
- Vitor

---

## O que tem aqui

| Pasta | O que é |
|---|---|
| `backend/` | API em Java (Spring Boot), ligada ao banco MySQL |
| `frontend/` | Site em Angular |
| `nginx-ubs/` | Servidor que publica o site e a API na porta **8081** |
| `ubs_agendamento/` | App de celular (Flutter) para agendar consultas |

---

## Como rodar o sistema (site + API + banco)

**Você precisa instalar:** [Docker Desktop](https://www.docker.com/products/docker-desktop/) e [Node.js](https://nodejs.org/).

Abra o PowerShell na pasta do projeto e rode, em ordem:

**1. Criar o arquivo de configuração**
```powershell
copy .env.exemplo .env
```

**2. Compilar o site**
```powershell
cd frontend; npm ci; npm run build; cd ..
```

**3. Subir tudo**
```powershell
docker compose up -d --build
```

**4. Abrir no navegador:** http://localhost:8081

- Usuário inicial: `super.admin@email.com`
- Senha: `123456`

Para conferir se a API e o banco estão no ar, abra
http://localhost:8081/diretorio-api/status. A resposta deve ser
`{"api":"ok","bancoDeDados":"ok"}`.

---

## Como rodar o app no celular

**Você precisa instalar:** [Flutter](https://docs.flutter.dev/get-started/install/windows/mobile) e [Android Studio](https://developer.android.com/studio). Abra o Android Studio uma vez e deixe-o baixar o Android SDK.

O sistema precisa estar rodando (seção anterior).

**1. Descubra o IP do seu computador**

Rode `ipconfig` e anote o "Endereço IPv4", por exemplo `192.168.0.10`.

**2. Libere a porta 8081 no firewall**

Faça isso uma vez só, num PowerShell **como administrador**:
```powershell
New-NetFirewallRule -DisplayName "UBS 8081" -Direction Inbound -LocalPort 8081 -Protocol TCP -Action Allow
```

**3. Ligue a "Depuração USB" no celular Android e conecte o cabo**

Para liberar a opção: Configurações → Sobre o telefone → toque 7 vezes em "Número da versão".

**4. Rode o app**
```powershell
cd ubs_agendamento
flutter pub get
flutter run
```

**5. Na primeira abertura, informe o servidor** `http://SEU-IP:8081/diretorio-api` e entre com o mesmo usuário do site.

**Sem cabo:** rode `flutter build apk --debug`, envie para o celular o
arquivo `ubs_agendamento\build\app\outputs\flutter-apk\app-debug.apk` e
instale.

Se der algum problema, consulte a seção de problemas comuns em
[ubs_agendamento/README.md](ubs_agendamento/README.md#problemas-comuns).

---

## Mudanças desta versão (branch `Devel_App`)

**App de celular (novo)**
- Agenda do dia, novo agendamento, confirmar e cancelar consultas.
- Ao abrir, verifica se a API e o banco de dados estão no ar.
- O app conversa com o banco **através da API**, nunca direto. Os motivos estão no [README do app](ubs_agendamento/README.md#decisões).

**CPF sem duplicatas**
- O CPF passou a ser obrigatório, conferido pelos dígitos verificadores e único.
- Antes, cadastrar um CPF repetido **apagava os dados do paciente que já existia**. Agora o cadastro é recusado e a tela oferece abrir o paciente existente.
- CPFs antigos gravados com pontos e traço são corrigidos automaticamente ao iniciar o backend. CPFs repetidos aparecem no log ("CPF duplicado") para revisão manual; nada é apagado.

**Nomes padronizados**
- No cadastro de usuários, `fullName` virou `nomeCompleto` e `username` virou `nomeUsuario`, os mesmos nomes de Paciente e Médico. O banco não mudou.
- O CRM é gravado sempre no mesmo formato (`123456SP`).
- Na tela "Editar Paciente", campos com nome errado faziam a edição ser perdida. Foram corrigidos, e o campo CPF foi adicionado.
- Variáveis do `.env` com um nome só em todo o projeto:
  - `SECURITY_JWT_SECRET_KEY` no lugar de `SPRING_JWT_SECRET_KEY`.
  - `MYSQL_ROOT_PASSWORD` e `MYSQL_DATABASE` no lugar de `DB_*`.
  - `LISTA_HOSTS` continua igual.

**Site e API pela porta 8081** (trazido da branch `feat/nginx-usb-8081`)
- O container `nginx-ubs` publica o site e a API juntos na porta 8081.
- Os caminhos do site e da API ficam no `.env` (`FRONT_BASE_PATH` e `API_BASE_PATH`) e podem ser trocados sem recompilar.
- Correções de autenticação e nos cadastros de paciente, médico e usuário (máscaras, menu duplicado).

**Correções**
- O container `nginx-ubs` caía ao iniciar no Windows por causa do fim de linha do script de partida.
- As telas de agendamento e cadastro agora mostram o motivo quando o servidor recusa algo, em vez de falhar em silêncio.
