# Secretary

Sistema de Controle de Ciclos Financeiros via WhatsApp.

## Visao Geral

O Secretary e uma API REST para gerenciamento de ciclos de credito financeiros. O sistema monitora automaticamente os ciclos, envia alertas de vencimento e fornece uma interface para criar e consultar ciclos via comandos.

## Funcionalidades

- **Gerenciamento de Ciclos**: Criacao e monitoramento de ciclos de credito
- **Alertas Automaticos**: Notificacoes por email e WhatsApp antes do vencimento
- **Status Automatico**: Atualizacao automatica do status baseada na data
- **Comandos Flexiveis**: Interface via API para criar e consultar ciclos
- **Tratamento de Erros**: Respostas padronizadas e tratamento centralizado

## Arquitetura

```
src/main/java/com/secretary/secretary/
├── interfaces/web/        # Controllers e handlers REST
├── application/           # Services e DTOs
│   ├── services/
│   └── dtos/
├── domain/               # Modelo de dominio e excecoes
│   ├── model/
│   └── exceptions/
└── infra/                # Infraestrutura
    ├── repositorys/
    ├── notification/
    ├── email/
    ├── whatsapp/
    ├── schedulers/
    └── config/
```

## Tecnologias

- **Java 22**
- **Spring Boot 3.3.1**
- **Spring Data JPA**
- **PostgreSQL**
- **Lombok**
- **Jakarta Bean Validation**

## Pre-requisitos

- Java 22 ou superior
- PostgreSQL 14 ou superior
- Maven 3.8+

## Instalacao

### 1. Clone o repositorio

```bash
git clone https://github.com/seu-usuario/secretary.git
cd secretary
```

### 2. Crie o banco de dados

```sql
CREATE DATABASE secretary;
```

### 3. Configure as variaveis de ambiente

Crie um arquivo `.env` na raiz do projeto:

```env
# Database
DATABASE_URL=jdbc:postgresql://localhost:5432/secretary
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=root

# Email (Gmail SMTP)
MAIL_USERNAME=seuemail@gmail.com
MAIL_PASSWORD=suasenha
MAIL_ADMIN_EMAIL=admin@secretary.com
```

### 4. Execute a aplicacao

```bash
mvn spring-boot:run
```

A aplicacao estara disponivel em `http://localhost:8080`

## Endpoints

### Configurar Banco

```
POST /v1/simulation/setup-bank?name={nome}
```

**Resposta (200):**
```json
{
  "id": 1,
  "name": "Nubank",
  "active": true
}
```

### Executar Comando

```
POST /v1/simulation/command
```

**Request Body:**
```json
{
  "intent": "CREATE_CYCLE",
  "bank": "Nubank",
  "amount": 5000.00
}
```

**Resposta (200):**
```json
{
  "intent": "CREATE_CYCLE",
  "message": "Ciclo de R$ 5000.00 criado com sucesso para o Nubank.",
  "cycle": {
    "id": 1,
    "bankName": "Nubank",
    "amount": 5000.00,
    "startDate": "2026-06-26",
    "endDate": "2026-07-06",
    "adjustedEndDate": "2026-07-03",
    "status": "ACTIVE"
  }
}
```

## Comandos Disponiveis

| Comando | Descricao |
|---------|-----------|
| `CREATE_CYCLE` | Cria um novo ciclo de credito |
| `CHECK_STATUS` | Consulta status (pendente de implementacao) |

## Ciclos

### Status

| Status | Descricao |
|--------|-----------|
| `ACTIVE` | Ciclo ativo, mais de 3 dias para vencer |
| `CLOSE_TO_EXPIRY` | Ciclo proximo ao vencimento (3 dias ou menos) |
| `OVERDUE` | Ciclo vencido |
| `CLOSED` | Ciclo encerrado |

### Regras de Negocio

- O vencimento e calculado automaticamente (10 dias uteis)
- Fins de semana sao ajustados para o dia util anterior
- Alertas sao enviados automaticamente 3, 1 e 0 dias antes do vencimento

## Tratamento de Erros

O sistema retorna respostas padronizadas em formato JSON:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Descricao do erro",
  "timestamp": "2026-06-26T10:30:00",
  "path": "/v1/simulation/command",
  "fieldErrors": [
    {
      "field": "bank",
      "message": "Nome do banco e obrigatorio",
      "rejectedValue": null
    }
  ]
}
```

### Tipos de Erro

| HTTP Status | Descricao |
|-------------|-----------|
| 400 | Erro de validacao ou regra de negocio |
| 404 | Recurso nao encontrado |
| 409 | Conflito (recurso ja existe) |
| 500 | Erro interno do servidor |

## Endpoints de Simulacao

A API de simulacao e util para testes e demonstracoes:

1. **Setup Bank**: Cadastre bancos antes de criar ciclos
2. **Command**: Teste comandos com dados simulados

## Desenvolvimento

### Estrutura de Pastas

- `interfaces/` - Camada de apresentacao (controllers)
- `application/` - Logica de negocio (services)
- `domain/` - Modelo de dominio e regras
- `infra/` - Infraestrutura (repositorios, integracoes)

### Comandos Uteis

```bash
# Compilar o projeto
mvn clean compile

# Executar testes
mvn test

# Empacotar a aplicacao
mvn clean package

# Executar a aplicacao
mvn spring-boot:run
```

## Licenca

Este projeto esta sob a licenca MIT.