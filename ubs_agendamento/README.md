# UBS Agendamento — app mobile (Flutter)

App Android/iOS para a recepção da UBS **marcar, consultar, confirmar e cancelar
consultas**. Ele faz o mesmo que a tela `/agendamento` do sistema web e usa o
mesmo backend, o mesmo banco e o mesmo login.

```
┌──────────────┐  HTTPS + JWT (Bearer)  ┌───────────┐       ┌───────────────┐      ┌───────┐
│  App Flutter │ ─────────────────────▶ │ nginx-ubs │ ────▶ │ backend Spring│ ───▶ │ MySQL │
│ (celular)    │   /diretorio-api/...   │  :8081    │       │  :8080        │      │ :3306 │
└──────────────┘                        └───────────┘       └───────────────┘      └───────┘
```

> **O app nunca conecta direto no MySQL.** Toda leitura e gravação passa pela
> API, que confere o login e aplica as regras de negócio (horário livre,
> médico ativo, data futura). Os motivos estão em [Decisões](#decisões).

---

## Sumário

- [Funcionalidades](#funcionalidades)
- [Decisões](#decisões) (por que não WebView, por que não acesso direto ao banco)
- [Como rodar](#como-rodar)
- [Configuração do servidor](#configuração-do-servidor)
- [Rotas da API usadas](#rotas-da-api-usadas)
- [Estrutura do código](#estrutura-do-código)
- [Testes](#testes)
- [Gerar o APK de produção](#gerar-o-apk-de-produção)
- [Segurança](#segurança)
- [Problemas comuns](#problemas-comuns)

---

## Funcionalidades

| Tela | O que faz |
|---|---|
| **Abertura** | Ao iniciar, chama `GET /status` e confirma que a **API responde e alcança o banco de dados**. Se algo falhar, diz qual parte (rede, API ou banco) e oferece tentar de novo ou trocar o servidor. |
| **Servidor** | Endereço da API. Aparece sozinha na primeira abertura e testa a conexão antes de salvar. Mostra um alerta se o endereço for `http://`. |
| **Login** | Mesmo e-mail e senha do sistema web. O token é guardado no armazenamento seguro do aparelho e vale até expirar (padrão: 1 h). |
| **Agenda** | Consultas de um dia, em ordem de horário. Navegação por dia ou calendário, filtro por status com contagem e "puxar para atualizar". Ao tocar numa consulta, mostra os detalhes e as ações **Confirmar** e **Cancelar**, com as mesmas regras da tela web. |
| **Novo agendamento** | Busca do paciente por nome ou CPF (direto na API, sem baixar a base). Tipo de consulta, **médico filtrado pela especialidade do tipo**, data, hora e observações. Antes de gravar, confere se o médico está livre. Qualquer recusa do backend aparece com o motivo. |

Cadastrar ou editar paciente continua no sistema web. O app só agenda.

---

## Decisões

### Por que não uma WebView do sistema web

A WebView seria o caminho mais curto, mas aqui não é o mais prático nem o mais seguro:

1. **O login do site não funciona dentro dela sem HTTPS.** O backend grava o JWT
   num cookie `Secure` (`AuthenticationController`). Numa rede interna em
   `http://`, o WebView descarta esse cookie e o usuário não passa do login.
2. **Mostraria o sistema inteiro, não só o agendamento.** Cadastro de usuários,
   upload de fichas e tabelas largas foram feitos para desktop e ficam ruins no
   celular. Restringir isso exigiria mexer no Angular.
3. **A experiência seria de site.** Não haveria seletores nativos de data e hora,
   mensagens de rede claras, "puxar para atualizar" nem o token no Keystore. As
   lojas também costumam recusar apps que só embrulham um site (App Store,
   diretriz 4.2).
4. **O custo de fazer nativo é baixo.** A API REST já existia. No backend
   bastaram uma rota de login por token (`/auth/token`), uma de status
   (`/status`), a busca de pacientes (`/api/pacientes/busca`) e a agenda por
   período (`/api/agendamentos/periodo`).

### Por que o app não fala direto com o banco

"Comunicar com o banco ao iniciar" foi implementado como **API → banco**: o
`GET /status` testa a conexão com o MySQL e responde ao app. Conectar o
celular direto no MySQL exigiria:

- **Usuário e senha do banco dentro do APK.** Qualquer APK pode ser
  descompilado, e com essa senha alguém leria ou apagaria todos os dados de
  pacientes.
- **A porta 3306 aberta na rede**, um alvo conhecido.
- **Repetir no app todas as regras do backend**: conflito de horário, médico
  ativo, CPF único, permissões por papel. Isso ficaria fora de sincronia com o
  sistema web.

---

## Como rodar

### Pré-requisitos

- [Flutter](https://docs.flutter.dev/get-started/install) 3.24 ou mais recente (`flutter doctor` sem erros)
- Android Studio com um emulador, ou um celular com depuração USB
- O sistema da UBS rodando (`docker compose up -d` na raiz do repositório)

### Baixar as dependências e rodar

Com o celular conectado no USB (ou um emulador aberto):

```bash
cd ubs_agendamento
flutter pub get
flutter run
```

As pastas `android/` e `ios/` já estão no repositório: não é preciso rodar
`flutter create`. Se um dia elas forem apagadas, recrie com
`flutter create --org br.gov.sp.fatec.ubs --project-name ubs_agendamento --platforms=android,ios .`.
Esse comando não sobrescreve arquivos existentes, então o `AndroidManifest.xml`
e as regras de rede deste repositório são mantidos.

**Sem cabo USB:** `flutter build apk --debug` gera
`build\app\outputs\flutter-apk\app-debug.apk`. Envie o arquivo para o celular
e instale.

Para não digitar o endereço na primeira abertura, ele pode ir no build:

```bash
flutter run --dart-define=API_BASE_URL=http://10.0.2.2:8081/diretorio-api
```

> `10.0.2.2` é o "localhost do computador" visto de dentro do **emulador**
> Android. Num celular físico, use o IP do computador na rede
> (ex.: `http://192.168.0.10:8081/diretorio-api`).

---

## Configuração do servidor

O endereço é **o mesmo que o navegador usa para a API**:

```
<protocolo>://<servidor>:<porta do nginx-ubs>/<API_BASE_PATH do .env>
```

Com o `.env.exemplo` da raiz: `http://<servidor>:8081/diretorio-api`.

O endereço fica salvo no aparelho e pode ser trocado pelo link no rodapé do
login ou pela tela de erro de conexão.

O app não usa CORS (CORS só existe no navegador). Não é preciso incluir nada em
`LISTA_HOSTS`.

---

## Rotas da API usadas

Todas são relativas ao endereço configurado. As marcadas com 🔓 são públicas;
as demais exigem `Authorization: Bearer <token>`.

| Método | Rota | Uso no app |
|---|---|---|
| GET 🔓 | `/status` | Abertura: API e banco no ar? `200 {"api":"ok","bancoDeDados":"ok"}` ou `503` com o banco fora. |
| POST 🔓 | `/auth/token` | Login. Corpo `{"email","password"}`. Devolve `token`, `expiresIn` (instante de expiração, em ms) e `roles`. |
| GET | `/api/agendamentos/periodo?inicio=&fim=` | Agenda do dia (`yyyy-MM-ddTHH:mm:ss`). |
| GET | `/api/agendamentos/medico/{id}/disponibilidade?dataHora=` | Confere o horário antes de gravar. |
| POST | `/api/agendamentos` | Cria o agendamento. |
| PUT | `/api/agendamentos/{id}/confirmar` | Confirma. |
| PUT | `/api/agendamentos/{id}/cancelar` | Cancela. |
| GET | `/api/pacientes/busca?termo=` | Busca de paciente por nome ou CPF (até 20 resultados). |
| GET | `/api/medicos/ativos` | Médicos que podem receber agendamento. |

Os erros chegam em *ProblemDetail* (`{"detail": "motivo"}`), e o app mostra o `detail`.

---

## Estrutura do código

```
lib/
├── main.dart                     # inicializa datas pt_BR, carrega config e sobe o app
├── app.dart                      # MaterialApp, tema, idioma, volta ao login em 401
├── tema.dart                     # cores (verde-água do sistema web), campos, status
├── core/
│   ├── api_client.dart           # HTTP: URL base, Bearer, JSON, timeout, erros
│   ├── api_exception.dart        # erro com mensagem pronta (lê o ProblemDetail)
│   ├── configuracao_servidor.dart# endereço da API (SharedPreferences)
│   └── sessao.dart               # JWT no armazenamento seguro (Keystore/Keychain)
├── models/                       # Agendamento, Paciente, Medico, Status, tipos de consulta
├── services/                     # uma classe por grupo de rotas da API
│   └── servicos.dart             # monta tudo; repassado às telas pelo construtor
├── screens/                      # abertura, servidor, login, agenda, novo agendamento
├── util/formatos.dart            # CPF, datas no formato do backend e em pt_BR
└── widgets/                      # card de agendamento, chip de status, campos de seleção
```

Não há pacote de gerência de estado: as telas são `StatefulWidget` e recebem
os serviços pelo construtor. Para um app deste tamanho fica mais simples de ler
e de testar.

**Espelhado do sistema web.** A lista `tiposConsulta` e a regra
`medicosParaTipo` repetem `TIPOS_CONSULTA` e `_filtrarMedicosPorTipo` do
Angular. Mudou lá, mude aqui.

---

## Testes

```bash
flutter test
flutter analyze
```

- `test/regras_test.dart`: máscara de CPF, datas no formato do backend, filtro
  de médicos, leitura do JSON da API, mensagens de erro e endereço do servidor.
- `test/widget_test.dart`: abertura e login contra uma **API simulada**
  (`MockClient`), sem servidor real. Verifica a chamada ao `/status`, o aviso de
  banco fora do ar, o corpo enviado ao `/auth/token`, o `Bearer` na agenda e a
  mensagem de login recusado.

---

## Gerar o APK de produção

```bash
flutter build apk --release --dart-define=API_BASE_URL=https://servidor-da-ubs/diretorio-api
```

O APK fica em `build/app/outputs/flutter-apk/app-release.apk`. Para publicar,
configure a assinatura conforme a
[documentação do Flutter](https://docs.flutter.dev/deployment/android#signing-the-app).
O `.gitignore` já impede que `key.properties` e `*.jks` sejam commitados.

---

## Segurança

| Ponto | Como foi tratado |
|---|---|
| Senha do banco | Não existe no app. Só o backend conhece. |
| Token de sessão | Guardado com `flutter_secure_storage` (Keystore/Keychain). `android:allowBackup="false"` impede que ele vá para o backup do Google. |
| Expiração | O app confere a validade antes de cada chamada. Um `401` do servidor apaga a sessão e volta ao login. |
| Rede | **O release bloqueia `http://`** (`network_security_config.xml`). O debug libera, para testes locais. No iOS o ATS já bloqueia por padrão. |
| Login do site | Inalterado: `/auth/login` continua com cookie `httpOnly`. O token só vai no corpo da resposta em `/auth/token`, que é a rota do app. |
| `/status` | Responde só `ok` ou `indisponivel`, sem versão, host ou mensagem de erro do banco. |

**Rede interna sem HTTPS:** o recomendado é pôr TLS no nginx. Se não houver
como, libere **só o IP do servidor** no `network_security_config.xml` de
`src/main` (o arquivo traz o exemplo comentado). No iOS, use uma exceção de
domínio em `NSAppTransportSecurity` no `Info.plist`.

---

## Problemas comuns

| Sintoma | Causa provável |
|---|---|
| "Sem conexão com o servidor" | Endereço errado, celular em outra rede ou firewall. Teste o mesmo endereço + `/status` no navegador do celular. |
| "O servidor respondeu, mas não reconhece o app" | O endereço não inclui o `API_BASE_PATH` (ex.: falta `/diretorio-api`) ou o backend é anterior às rotas do app. |
| "Sem acesso ao banco de dados" | O backend está no ar e o MySQL não. Veja `docker compose ps` e os logs do `mysql_server`. |
| Build falha com "sdkmanager did not install NDK ..." | No Windows, a instalação automática do NDK pelo Flutter quebra no `;` do nome do pacote. Instale manualmente com a versão citada no erro: `& "$env:ANDROID_HOME\cmdline-tools\latest\bin\android.exe" sdk install ndk/28.2.13676358` |
| Emulador trava ao iniciar ("hanging thread QEMU") | Pouca CPU livre (Docker + Gradle). Abra com menos núcleos e GPU por software: `emulator -avd <nome> -cores 4 -gpu swiftshader_indirect` |
| Funciona no debug e não no release | O endereço é `http://`, que o release bloqueia. Veja [Segurança](#segurança). |
| "Nenhum médico ativo atende ..." | Não há médico ativo com essa especialidade. Médicos são cadastrados no sistema web (usuário com função MEDICO). |
