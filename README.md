# Insurance Platform - Sistema de Cadastro de Clientes e Seguros

## 📋 Visão Geral

Sistema completo de cadastro de clientes e contratação de seguros desenvolvido com Spring Boot, seguindo princípios de **Arquitetura Hexagonal/Clean Architecture**, **SOLID**, e padrões de resiliência para sistemas financeiros de alta criticidade.

## 🏗️ Arquitetura

### Arquitetura Hexagonal (Ports & Adapters)

```
customer-service/
├── domain/                    # Core do negócio (independente de frameworks)
│   ├── model/                # Entidades de domínio
│   └── port/
│       ├── in/              # Portas de entrada (Use Cases)
│       └── out/             # Portas de saída (Repository, Cache, Events)
├── application/              # Lógica de aplicação (orquestração)
│   └── service/             # Implementação dos Use Cases
└── adapter/                  # Adaptadores externos
    ├── in/rest/            # Controladores REST
    ├── out/persistence/    # Implementação JPA
    ├── out/cache/          # Implementação Redis
    └── out/event/          # Implementação Outbox Pattern
```

## 🚀 Funcionalidades Implementadas

### Customer Service (API de Cadastro)
- ✅ CRUD completo de clientes
- ✅ Validação avançada de CPF (com dígitos verificadores)
- ✅ Validação de idade legal (18+ anos)
- ✅ Gestão completa de endereços
- ✅ Cache inteligente com Redis (Cache-Aside + Versioning)
- ✅ Circuit Breaker, Rate Limiter e Bulkhead
- ✅ Transactional Outbox Pattern para eventos
- ✅ Documentação Swagger/OpenAPI

### Insurance Service (API de Seguros)
- ✅ Simulação de seguros (Bronze, Prata, Ouro)
- ✅ Contratação de seguros com idempotência
- ✅ Validação de cliente via chamada back-to-back
- ✅ Circuit Breaker para resiliência
- ✅ Deduplicação de requisições
- ✅ Cache warming e Thundering Herd prevention

## 🛡️ Padrões de Resiliência Implementados

### 1. Circuit Breaker (Resilience4j)
- Protege contra falhas em cascata
- Fail-fast quando serviço está degradado
- Transição automática de estados (Closed → Open → Half-Open)

### 2. Cache Strategy (Redis)
- **Cache-Aside Pattern**: Leitura otimizada
- **Versioning (Fence Tokens)**: Previne race conditions
- **Transactional Outbox**: Invalidação atômica do cache
- **Probabilistic Early Recomputation**: Previne Thundering Herd

### 3. Graceful Degradation
- Fallback para cache quando DB está lento
- Respostas parciais em caso de falha
- Priorização de operações críticas

### 4. Idempotência e Deduplicação
- Chaves de idempotência para prevenir duplicações
- Deduplicação baseada em identificadores únicos
- Gestão de estados transacionais

## 🐳 Comandos Docker & Makefile

```bash
# Iniciar infraestrutura (PostgreSQL + Redis)
make start

# Criar schema e dados de teste
make ddl

# Criar dados de teste
make dml

# Construir aplicações
make build

# Iniciar APIs
make api

# Deploy completo (limpa, inicia tudo)
make full-deploy

# Ver logs
make logs
make logs-customer
make logs-insurance

# Parar tudo
make stop

# Limpar completamente
make clean
```

## 📊 Endpoints API

### Customer Service (Port 8080)

```
POST   /api/v1/customers          # Criar cliente
GET    /api/v1/customers/{id}     # Buscar por ID
GET    /api/v1/customers/cpf/{cpf} # Buscar por CPF
GET    /api/v1/customers          # Listar todos
PUT    /api/v1/customers/{id}     # Atualizar
DELETE /api/v1/customers/{id}     # Deletar
```

### Insurance Service (Port 8081)

```
POST   /api/v1/insurance/simulate  # Simular seguro
POST   /api/v1/insurance/contract  # Contratar seguro
GET    /api/v1/insurance/{id}      # Buscar apólice
GET    /api/v1/insurance/customer/{customerId} # Apólices do cliente
```

## 📚 Swagger UI

- Customer Service: http://localhost:8080/swagger-ui.html
- Insurance Service: http://localhost:8081/swagger-ui.html

## 🧪 Testes

```bash
# Executar testes unitários
cd customer-service && ./mvnw test
cd insurance-service && ./mvnw test

# Executar com coverage
./mvnw test jacoco:report
```

## 💡 Boas Práticas Aplicadas

1. **Código Limpo**
   - Nomenclatura clara e intencional
   - Funções pequenas com responsabilidade única
   - Sem lógica aninhada complexa
   - Comentários apenas quando necessário

2. **SOLID**
   - **S**ingle Responsibility: Cada classe tem um propósito
   - **O**pen/Closed: Extensível via interfaces
   - **L**iskov Substitution: Contratos bem definidos
   - **I**nterface Segregation: Portas específicas
   - **D**ependency Inversion: Depende de abstrações

3. **DDD (Domain-Driven Design)**
   - Linguagem ubíqua
   - Entidades ricas com regras de negócio
   - Agregados e Value Objects
   - Bounded Contexts claros

4. **CAP Theorem**
   - Consistência Eventual aceitável para reads
   - Consistência forte para writes (ACID)
   - Partition Tolerance via Circuit Breaker

## 🔧 Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.2.1**
- **PostgreSQL 15**
- **Redis 7**
- **Resilience4j** (Circuit Breaker, Rate Limiter, Bulkhead)
- **SpringDoc OpenAPI** (Swagger)
- **Docker & Docker Compose**
- **JUnit 5 & Mockito**
- **Lombok**

## 📝 Dados Fictícios (Seed Data)

Após executar `make dml`, os seguintes clientes estarão disponíveis:

```
CPF: 12345678901 - João Silva Santos
CPF: 23456789012 - Maria Oliveira Costa
CPF: 34567890123 - Pedro Henrique Souza
CPF: 45678901234 - Ana Paula Ferreira
CPF: 56789012345 - Carlos Eduardo Lima
```

## 🎯 Diferenciais Técnicos

1. **Staff Engineer Level**
   - Transactional Outbox para atomicidade
   - Cache versioning para prevenir race conditions
   - Bulkhead pattern para isolamento de recursos
   - Observabilidade com Prometheus metrics

2. **Fintech-Ready**
   - Auditoria completa (created_at, updated_at, version)
   - LGPD compliance (máscara de CPF em logs)
   - Idempotência em operações críticas
   - Rollback automático em falhas

3. **Production-Ready**
   - Health checks configurados
   - Graceful shutdown
   - Connection pooling otimizado
   - Retry automático com backoff

## 📖 Próximos Passos (Evolução)

- [ ] Implementar Kafka para eventos assíncronos
- [ ] Adicionar Debezium para CDC (Change Data Capture)
- [ ] Implementar CQRS para queries complexas
- [ ] Event Sourcing para audit trail completo
- [ ] API Gateway (Spring Cloud Gateway)
- [ ] Service Mesh (Istio) para observabilidade
- [ ] Kubernetes deployment manifests

---

**Desenvolvido seguindo as melhores práticas de engenharia de software para sistemas financeiros de alta criticidade.**
