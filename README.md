<p align="center">
  <img src="https://img.shields.io/badge/Java-17-%23ED8B00?style=flat&logo=java&logoColor=white" alt="Java 17"/>
  <img src="https://img.shields.io/badge/Spring_Boot-3.1.0-%236DB33F?style=flat&logo=springboot&logoColor=white" alt="Spring Boot 3.1"/>
  <img src="https://img.shields.io/badge/build-passing-brightgreen" alt="Build Passing"/>
  <img src="https://img.shields.io/badge/tests-228-blue" alt="228 Tests"/>
  <img src="https://img.shields.io/badge/license-MIT-green" alt="License MIT"/>
</p>

# 🅿️ Park API — Sistema de Gerenciamento de Estacionamento

API REST para gerenciamento de estacionamentos com controle de **usuários**, **clientes**, **vagas** e fluxo completo de **check-in/check-out** de veículos, incluindo cálculo de tarifas e descontos.

> Projeto de estudo desenvolvido com foco em organização, segurança, escalabilidade e boas práticas de APIs REST.

---

## ✨ Funcionalidades

- ✅ Cadastro e autenticação de usuários com **JWT**
- ✅ Perfis de acesso: **ADMIN** e **CLIENT**
- ✅ Cadastro de clientes vinculados a usuários
- ✅ Gerenciamento de vagas (LIVRE / OCUPADA)
- ✅ Check-in com geração automática de recibo
- ✅ Check-out com cálculo de tarifa por tempo
- ✅ Política de desconto progressivo (30% a cada 10 sessões)
- ✅ Auditoria completa (criação/atualização por usuário)
- ✅ Documentação interativa via **Swagger/OpenAPI**
- ✅ **228 testes** automatizados (unitários + integração)

---

## 🛠️ Stack Tecnológica

| Tecnologia | Versão | Propósito |
|---|---|---|
| Java | 17 | Linguagem principal |
| Spring Boot | 3.1.0 | Framework web |
| Spring Security | 6.x | Autenticação e autorização |
| Spring Data JPA | 3.1.0 | Persistência e repositories |
| Hibernate | 6.x | ORM |
| JWT (jjwt) | 0.13.0 | Tokens de autenticação |
| MySQL | 8.0+ | Banco de dados (produção) |
| H2 | — | Banco de dados (testes) |
| ModelMapper | 3.2.4 | Mapeamento DTO ↔ Entity |
| Lombok | 1.18.46 | Redução de boilerplate |
| Swagger (springdoc) | 2.2.0 | Documentação OpenAPI |
| JUnit 5 + Mockito | — | Testes unitários e integração |
| Maven | 3.8+ | Gerenciamento de dependências |

---

## 📋 Pré-requisitos

- **Java 17** (ou superior)
- **Maven 3.8+**
- **MySQL 8.0+** (para execução com perfil `dev`)
- Git

---

## 🚀 Como Executar

### 1. Clone o repositório

```bash
git clone https://github.com/CaioAntonioJava/park-api.git
cd park-api
```

### 2. Configure o ambiente

Copie o arquivo de exemplo e preencha suas credenciais:

```bash
cp .env.example .env
```

Edite o arquivo `.env` com suas configurações:

```env
# MySQL Database
DB_URL=jdbc:mysql://localhost:3306/park_api?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo
DB_USERNAME=root
DB_PASSWORD=sua_senha_aqui

# JWT Secret Key (mínimo 32 caracteres para HS256)
JWT_SECRET_KEY=sua-chave-secreta-jwt-com-pelo-menos-32-caracteres
```

> ⚠️ **Importante:** O arquivo `.env` contém credenciais sensíveis e está no `.gitignore`. **Nunca** commite este arquivo.

### 3. Execute a aplicação

```bash
./mvnw spring-boot:run
```

A aplicação iniciará em `http://localhost:8080`.

### 4. Executar com perfil de teste (H2)

```bash
./mvnw test
```

Os testes utilizam o perfil `test` com banco H2 em memória e uma chave JWT fixa definida em `application-test.properties`.

---

## 📁 Estrutura do Projeto

```
📦 demo-park-api
├── .env.example                  # Template de variáveis de ambiente
├── .gitignore
├── pom.xml
├── mvnw
├── mvnw.cmd
└── src/
    ├── main/
    │   ├── java/com/caiohenrique/demo_park_api/
    │   │   ├── config/              # Configurações (Security, JPA, Swagger, Timezone)
    │   │   ├── entity/              # Entidades JPA (User, Client, ParkingSpot, ParkingSession)
    │   │   ├── enums/               # Enums (SpotStatus)
    │   │   ├── exception/           # Exceções customizadas
    │   │   ├── jwt/                 # Utilitários e filtros JWT
    │   │   ├── parking/             # Lógica de negócio (tarifas, descontos, recibos)
    │   │   ├── repository/          # Repositórios JPA
    │   │   │   └── projection/      # Projeções para consultas
    │   │   ├── service/             # Camada de serviços
    │   │   └── web/
    │   │       ├── controller/      # Controllers REST
    │   │       ├── dto/             # DTOs de requisição/resposta
    │   │       │   └── mapper/      # Mappers DTO ↔ Entity
    │   │       └── exception/       # Handler global de exceções
    │   └── resources/
    │       ├── application.properties       # Configuração (MySQL)
    │       └── application-test.properties  # Configuração de teste (H2)
    └── test/
        └── java/com/caiohenrique/demo_park_api/
            ├── jwt/                 # Testes JWT
            ├── parking/             # Testes de lógica de domínio
            ├── repository/          # Testes de integração com BD
            ├── service/             # Testes de serviços
            └── web/
                ├── controller/      # Testes de controllers REST
                ├── dto/mapper/      # Testes de mappers
                └── exception/       # Testes do handler de exceções
```

---

## 🔐 Autenticação

A API utiliza **JWT (JSON Web Token)** para autenticação. O token tem validade de **30 minutos** e deve ser enviado no header `Authorization` no formato `Bearer <token>`.

### Obter token

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin@email.com.br",
    "password": "123456"
  }'
```

**Resposta:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### Usar token nas requisições

```bash
curl -X GET http://localhost:8080/api/v1/users \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

---

## 📡 API Endpoints

### 👤 Usuários (`/api/v1/users`)

| Método | Path | Auth | Role | Descrição |
|---|---|---|---|---|
| `POST` | `/api/v1/users` | ❌ | Público | Criar novo usuário |
| `GET` | `/api/v1/users/{id}` | ✅ | ADMIN ou CLIENT (próprio) | Buscar usuário por ID |
| `GET` | `/api/v1/users` | ✅ | ADMIN | Listar todos os usuários |
| `PATCH` | `/api/v1/users/{id}/password` | ✅ | CLIENT (próprio) | Alterar própria senha |
| `PATCH` | `/api/v1/users/{id}/reset-password` | ✅ | ADMIN | Redefinir senha de um usuário |

### 🔑 Autenticação (`/api/v1/auth`)

| Método | Path | Auth | Role | Descrição |
|---|---|---|---|---|
| `POST` | `/api/v1/auth/login` | ❌ | Público | Autenticar e obter token JWT |

### 🧑‍💼 Clientes (`/api/v1/clients`)

| Método | Path | Auth | Role | Descrição |
|---|---|---|---|---|
| `POST` | `/api/v1/clients` | ✅ | CLIENT | Criar novo cliente |
| `GET` | `/api/v1/clients/{id}` | ✅ | ADMIN | Buscar cliente por ID |
| `GET` | `/api/v1/clients` | ✅ | ADMIN | Listar todos os clientes |
| `GET` | `/api/v1/clients/details` | ✅ | CLIENT | Buscar dados do próprio cliente |

### 🅿️ Vagas (`/api/v1/parking-spots`)

| Método | Path | Auth | Role | Descrição |
|---|---|---|---|---|
| `POST` | `/api/v1/parking-spots` | ✅ | ADMIN | Criar nova vaga |
| `GET` | `/api/v1/parking-spots/{spotCode}` | ✅ | ADMIN | Buscar vaga por código |
| `GET` | `/api/v1/parking-spots` | ✅ | ADMIN | Listar todas as vagas |

### 🚘 Sessões de Estacionamento (`/api/v1/parking-sessions`)

| Método | Path | Auth | Role | Descrição |
|---|---|---|---|---|
| `POST` | `/api/v1/parking-sessions/check-in` | ✅ | ADMIN | Registrar entrada de veículo |
| `GET` | `/api/v1/parking-sessions/{receipt}` | ✅ | ADMIN | Buscar sessão por recibo |
| `PUT` | `/api/v1/parking-sessions/check-out/{receipt}` | ✅ | ADMIN | Registrar saída de veículo |
| `GET` | `/api/v1/parking-sessions/cpf/{cpf}` | ✅ | ADMIN | Histórico por CPF |
| `GET` | `/api/v1/parking-sessions` | ✅ | CLIENT | Histórico do próprio cliente |

---

## 💰 Regras de Negócio

### Cálculo de Tarifas

| Período | Valor |
|---|---|
| Até 15 minutos | R$ 5,00 |
| Até 60 minutos | R$ 9,25 |
| Após 60 minutos | R$ 9,25 + R$ 1,75 a cada 15 min adicionais |

**Exemplos:**

- 13:00 → 13:15 → **R$ 5,00**
- 13:00 → 14:00 → **R$ 9,25**
- 13:00 → 14:30 → **R$ 12,75**
- 13:00 → 15:00 → **R$ 16,25**
- 13:00 → 15:10 → **R$ 18,00**

### Política de Desconto

A cada **10 estacionamentos completos** (entrada + saída), o cliente recebe **30% de desconto** no próximo estacionamento concluído.

---

## 🧪 Testes

O projeto conta com **228 testes automatizados** entre unitários e de integração.

### Executar todos os testes

```bash
./mvnw test
```

### Cobertura por camada

| Camada | Quantidade |
|---|---|
| Lógica de domínio (tarifas, descontos, recibos) | 48 testes |
| Serviços | 48 testes |
| JWT (utils, filtros, user details) | 27 testes |
| Mappers DTO ↔ Entity | 11 testes |
| Repositórios (integração com H2) | 28 testes |
| Controllers REST (integração) | 55 testes |
| Exception handler | 11 testes |
| **Total** | **228 testes** |

---

## 📖 Documentação

A documentação interativa da API (Swagger/OpenAPI) está disponível em:

```
http://localhost:8080/docs-park.html
```

---

## 🤝 Contribuição

Contribuições são bem-vindas! Siga os passos:

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
3. Commit suas alterações (`git commit -m 'feat: adiciona nova feature'`)
4. Faça push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request

---

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

<p align="center">
  Desenvolvido por <a href="https://github.com/CaioAntonioJava">CaioAntonioJava</a>
</p>
