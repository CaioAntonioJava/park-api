📌 *Este projeto foi desenvolvido com foco em organização, segurança, escalabilidade e boas práticas de APIs REST.* CURSO APIREST e Spring Boot - Instrutor : Marcio Ballem

# 🚗 Sistema de Gerenciamento de Estacionamento (API REST)

## 📌 Visão Geral

Este projeto consiste no desenvolvimento de uma **API REST** para gerenciamento de estacionamentos, contemplando controle de usuários, clientes, vagas e fluxo de entrada e saída de veículos. A aplicação utiliza **autenticação via JSON Web Token (JWT)** e segue boas práticas de segurança, auditoria e documentação de APIs.

O sistema foi especificado a partir de um levantamento de requisitos junto ao cliente e tem como objetivo servir como **back-end** para consumo por aplicações front-end.

---

## 🛠️ Tecnologias e Padrões

* API REST
* Autenticação com **JWT (JSON Web Token)**
* Auditoria de dados (criação e atualização)
* Testes de integração **end-to-end (E2E)**
* Documentação dos endpoints (Swagger/OpenAPI ou equivalente)

---

## ⚙️ Configurações Gerais

* Timezone configurado conforme o país
* Locale configurado conforme o país
* Sistema de auditoria com:

  * Data de criação
  * Data da última modificação
  * Usuário responsável pela criação
  * Usuário responsável pela última alteração
* Banco de dados configurado para ambiente de desenvolvimento

---

## 👤 Módulo de Usuários

### Funcionalidades

* Cadastro de usuário sem autenticação prévia
* E-mail utilizado como **username** (único)
* Senha com no mínimo **6 caracteres**
* Perfis disponíveis:

  * **ADMIN**
  * **CLIENTE**

### Regras de Acesso

* Administrador pode:

  * Recuperar qualquer usuário por ID
  * Listar todos os usuários
* Cliente pode:

  * Recuperar apenas seus próprios dados
  * Alterar sua própria senha (quando autenticado)

### Qualidade

* Todos os recursos documentados
* Testes de integração end-to-end implementados

---

## 🔐 Autenticação

* Sistema de autenticação baseado em **JWT**
* Endpoint dedicado para login/autenticação
* Token necessário para acesso aos recursos protegidos
* Testes aplicados ao fluxo de autenticação

---

## 🧑‍💼 Módulo de Clientes

### Regras Gerais

* O cadastro de cliente só é permitido após o cadastro do usuário
* Um usuário pode possuir **apenas um cliente**
* Um cliente está vinculado a **um único usuário**

### Dados do Cliente

* Nome completo
* CPF (único)
* Vínculo com o usuário

### Permissões

* Cliente autenticado pode:

  * Consultar seus próprios dados
* Administrador autenticado pode:

  * Listar todos os clientes
  * Localizar cliente por ID

### Qualidade

* Recursos totalmente documentados
* Testes de integração end-to-end

---

## 🅿️ Módulo de Vagas

### Regras

* Todas as ações exigem autenticação
* Acesso restrito ao **administrador**

### Dados da Vaga

* Código único (diferente do ID)
* Status:

  * Livre
  * Ocupada

### Funcionalidades

* Localizar vaga pelo código

### Qualidade

* Endpoints documentados
* Testes de integração end-to-end

---

## 🚘 Módulo de Estacionamento

Responsável por controlar **entradas e saídas de veículos**.

### Regras de Negócio

* Apenas clientes cadastrados podem estacionar
* Cliente deve informar o **CPF** para estacionar
* Uma vaga pode receber vários veículos ao longo do tempo, mas **nunca simultaneamente**

### Check-in (Administrador)

Armazena:

* Placa do veículo
* Marca
* Modelo
* Cor
* Data/hora de entrada
* CPF do cliente
* Vaga vinculada

Gera automaticamente um **número de recibo único**.

### Check-out (Administrador)

Armazena:

* Data/hora de saída
* Valor total do período
* Valor do desconto (quando aplicável)

O cliente deve informar o **número do recibo** para retirada do veículo.

---

## 💰 Regras de Cálculo de Tarifas

| Período         | Valor                                      |
| --------------- | ------------------------------------------ |
| Até 15 minutos  | R$ 5,00                                    |
| Até 60 minutos  | R$ 9,25                                    |
| Após 60 minutos | R$ 9,25 + R$ 1,75 a cada 15 min adicionais |

### Exemplos

* 13:00 → 13:15 → **R$ 5,00**
* 13:00 → 14:00 → **R$ 9,25**
* 13:00 → 14:30 → **R$ 12,75**
* 13:00 → 15:00 → **R$ 16,25**
* 13:00 → 15:10 → **R$ 18,00**

---

## 🎁 Política de Desconto

* A cada **10 estacionamentos completos** (entrada + saída), o cliente recebe:

  * **30% de desconto** no próximo estacionamento concluído

---

## 📄 Relatórios

* Cliente pode gerar um **relatório em PDF** contendo o histórico de seus estacionamentos

---

## 🧪 Testes

* Todos os módulos possuem **testes de integração end-to-end**
* Cobertura dos fluxos principais e regras de negócio

---

## 📚 Documentação

* Todos os endpoints da API são documentados
* Documentação pensada para facilitar o consumo pelo front-end

---

## 📦 Versão

* **Demanda – Versão 1**

---

## 👥 Público-Alvo

* Desenvolvedores back-end
* Desenvolvedores front-end
* Avaliadores técnicos
* Equipes de QA

---
