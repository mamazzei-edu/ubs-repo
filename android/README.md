# UBS Android - Sistema de Gestão de Pacientes

Aplicativo Android nativo para o Sistema de Gestão de Pacientes da UBS (Unidade Básica de Saúde).

## 📱 Funcionalidades

- ✅ **Login de Usuários** - Autenticação com email e senha
- ✅ **Lista de Pacientes** - Visualização paginada de todos os pacientes
- ✅ **Detalhes do Paciente** - Informações completas de cada paciente
- ✅ **Cadastro de Pacientes** - Adicionar novos pacientes ao sistema
- ✅ **Edição de Pacientes** - Atualizar informações existentes
- ✅ **Busca de Pacientes** - Por nome ou prontuário
- ✅ **Logout** - Encerrar sessão com segurança

## 🛠️ Tecnologias Utilizadas

### Linguagem
- **Kotlin** - Linguagem moderna e oficial do Android

### Arquitetura
- **MVVM** (Model-View-ViewModel)
- **Repository Pattern**
- **Clean Architecture**

### Bibliotecas Principais
- **Retrofit** - Comunicação HTTP com API REST
- **Coroutines** - Programação assíncrona
- **Flow** - Gerenciamento de estado reativo
- **DataStore** - Armazenamento de preferências
- **Material Design Components** - UI moderna e consistente
- **ViewBinding** - Acesso seguro às views
- **RecyclerView** - Listas eficientes
- **Navigation Component** - Navegação entre telas

## 📋 Pré-requisitos

- **Android Studio** Hedgehog (2023.1.1) ou superior
- **JDK 17** ou superior
- **Android SDK** API 24+ (Android 7.0 Nougat)
- **Backend UBS** rodando em `http://localhost:8080`

## 🚀 Como Executar

### 1. Abrir o Projeto
```bash
# Abra o Android Studio
# File > Open > Selecione a pasta 'android'
```

### 2. Configurar o Backend
O app está configurado para conectar ao backend em:
- **Emulador**: `http://10.0.2.2:8080` (localhost do PC)
- **Dispositivo Físico**: Altere em `app/build.gradle` para o IP da sua máquina

```gradle
buildConfigField "String", "BASE_URL", "\"http://SEU_IP:8080/\""
```

### 3. Sincronizar Dependências
```bash
# No Android Studio:
# File > Sync Project with Gradle Files
```

### 4. Executar o App
- Conecte um dispositivo Android ou inicie um emulador
- Clique em **Run** (▶️) ou pressione `Shift + F10`

## 🔐 Credenciais de Teste

Use as mesmas credenciais do sistema web:

**Super Admin:**
- Email: `super.admin@email.com`
- Senha: `123456`

**Admin:**
- Email: `admin@email.com`
- Senha: `123456`

## 📂 Estrutura do Projeto

```
android/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/br/sp/gov/fatec/ubs/android/
│   │   │   │   ├── data/
│   │   │   │   │   ├── api/          # Serviços de API
│   │   │   │   │   ├── model/        # Modelos de dados
│   │   │   │   │   └── repository/   # Repositórios
│   │   │   │   ├── ui/
│   │   │   │   │   ├── login/        # Tela de login
│   │   │   │   │   ├── paciente/     # Telas de pacientes
│   │   │   │   │   └── MainActivity.kt
│   │   │   │   └── UBSApplication.kt
│   │   │   ├── res/
│   │   │   │   ├── layout/           # Layouts XML
│   │   │   │   ├── values/           # Strings, cores, temas
│   │   │   │   └── menu/             # Menus
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle
│   └── build.gradle
├── gradle/
├── settings.gradle
└── README.md
```

## 🎨 Design

O app segue as diretrizes do **Material Design 3** com:
- Cores do tema UBS (azul médico)
- Componentes modernos e acessíveis
- Animações suaves
- Feedback visual claro

## 🔄 Fluxo de Navegação

```
SplashScreen (verificar login)
    ↓
LoginActivity → MainActivity (lista de pacientes)
                    ↓
                PacienteDetailActivity
                    ↓
                PacienteFormActivity (cadastro/edição)
```

## 📡 Endpoints Utilizados

- `POST /api/login` - Autenticação
- `POST /api/logout` - Logout
- `GET /api/pacientes` - Listar pacientes (paginado)
- `GET /api/pacientes/{id}` - Buscar por ID
- `POST /api/pacientes` - Criar paciente
- `PUT /api/pacientes/{id}` - Atualizar paciente
- `DELETE /api/pacientes/{id}` - Deletar paciente

## 🐛 Troubleshooting

### Erro de Conexão
- Verifique se o backend está rodando
- No emulador, use `10.0.2.2` ao invés de `localhost`
- Em dispositivo físico, use o IP da máquina na mesma rede

### Erro de Build
```bash
# Limpar e rebuildar
./gradlew clean
./gradlew build
```

### Problemas com Dependências
```bash
# Invalidar cache do Android Studio
File > Invalidate Caches / Restart
```

## 📝 Próximas Funcionalidades

- [ ] Upload de fichas PDF
- [ ] Busca avançada com filtros
- [ ] Modo offline com sincronização
- [ ] Notificações push
- [ ] Agendamentos
- [ ] Relatórios

## 👨‍💻 Desenvolvimento

Para contribuir com o projeto:

1. Siga o padrão MVVM
2. Use Kotlin Coroutines para operações assíncronas
3. Implemente testes unitários
4. Siga as convenções de código Kotlin
5. Documente mudanças significativas

## 📄 Licença

Este projeto faz parte do Sistema UBS e segue as mesmas diretrizes de licenciamento.

---

**Desenvolvido com ❤️ para a Saúde Pública**
