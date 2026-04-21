# Definições Arquiteturais do Projeto

## 1. Objetivo do sistema

Vamos desenvolver uma **plataforma de feedback para aulas e cursos online**, em que:

- estudantes enviam avaliações
- o sistema identifica feedbacks críticos
- administradores recebem alertas automáticos
- um relatório semanal consolidado é enviado com métricas das avaliações

Essa solução atende ao enunciado, que exige:

- execução em ambiente cloud
- uso obrigatório de serverless
- no mínimo duas funções serverless
- separação adequada de responsabilidades
- monitoramento
- deploy automatizado
- notificações automáticas para problemas críticos
- relatório semanal com média de avaliações
- documentação da arquitetura, deploy, monitoramento e funções criadas

---

## 2. Direção arquitetural

### Cloud escolhida
- **Microsoft Azure**

### Backend principal
- **Java + Quarkus**

### Estilo arquitetural
- **Clean Architecture**

### Princípios que guiarão a implementação
- Clean Code
- SOLID
- SRP
- separação entre domínio, aplicação e infraestrutura
- baixo acoplamento
- alta coesão
- testabilidade
- observabilidade
- facilidade de evolução

---

## 3. Arquitetura do sistema

### Componentes principais

#### API principal
- **Quarkus**
- hospedada em **Azure Container Apps**

#### Banco de dados
- **Azure Database for PostgreSQL**

#### Mensageria
- **Azure Service Bus Topic + Subscription**

#### Camada serverless
- **Azure Functions**

#### Observabilidade
- **Azure Monitor + Application Insights** como base principal
- **Prometheus + Grafana** como camada complementar de métricas e dashboards

#### CI/CD
- **GitHub Actions**

#### Segurança
- **Managed Identity**
- **Azure Key Vault**
- secrets fora do código
- princípio do menor privilégio

---

## 4. Definições arquiteturais

### 4.1 Hospedagem da API
A API principal será executada em **Azure Container Apps**.

Essa escolha preserva a aplicação Quarkus como container, facilita a evolução da solução, permite melhor controle de escala e mantém coerência com uma arquitetura cloud-native.

### 4.2 Mensageria orientada a eventos
A comunicação assíncrona será feita com **Azure Service Bus Topic + Subscription**.

A API publicará o evento `feedback.created` em um tópico, e a Azure Function responsável pelo alerta consumirá a subscription correspondente. Essa abordagem permite adicionar novos consumidores futuramente sem alterar o produtor.

### 4.3 Confiabilidade de publicação
A solução adotará o padrão **Transactional Outbox**.

O feedback e o evento de integração serão registrados na mesma transação. Em seguida, um publicador assíncrono enviará o evento ao Service Bus. Isso reduz o risco de inconsistência entre persistência e publicação.

### 4.4 Observabilidade
A observabilidade será dividida em duas camadas:

#### Base operacional
- Azure Monitor
- Application Insights

Responsável por:
- logs
- traces
- exceptions
- latência
- disponibilidade operacional
- correlação entre API, publicação e Functions

#### Camada de dashboards e métricas
- Prometheus
- Grafana

Responsável por:
- dashboards técnicos
- dashboards de negócio
- visualização para demonstração em vídeo
- exploração de métricas expostas pelo Quarkus

---

## 5. Requisitos do enunciado e aderência da solução

### Serverless obrigatório
Atendido com:
- `CriticalFeedbackNotifier`
- `WeeklyFeedbackReportGenerator`

### Mínimo de duas funções
Atendido com duas Azure Functions com responsabilidades separadas.

### Notificações automáticas para itens críticos
Atendido pela função de notificação.

### Relatório semanal com média de avaliações
Atendido pela função agendada de relatório.

### Aplicação monitorada
Atendido com:
- Application Insights
- Azure Monitor
- métricas expostas pela aplicação
- dashboards

### Deploy automatizado
Atendido com GitHub Actions.

### Segurança e governança de acesso
Atendido com:
- secrets fora do código
- Key Vault
- Managed Identity
- configuração de acesso mínimo

---

## 6. Premissas funcionais

O PDF não fecha várias regras de negócio, então estas decisões passam a ser oficiais do projeto.

### 6.1 Regra de urgência

#### ALTA
- nota entre **0 e 3**
- ou presença de palavras-chave críticas na descrição

#### MEDIA
- nota entre **4 e 6**

#### BAIXA
- nota entre **7 e 10**

#### Palavras críticas iniciais
- erro
- travando
- bug
- péssimo
- horrível
- não funciona
- reclamação
- insuportável

### 6.2 Administradores
- não haverá módulo completo de usuários no MVP
- os administradores serão definidos por configuração
- exemplo: `ADMIN_EMAILS`

### 6.3 Privacidade
- não vamos coletar dados pessoais do estudante no MVP
- o feedback terá apenas:
  - descrição
  - nota
  - urgência derivada
  - timestamps técnicos
  - status operacionais

### 6.4 Relatório semanal
- será enviado por **e-mail em HTML**
- sem dashboard web no MVP
- CSV em anexo fica como melhoria futura

### 6.5 Endpoint oficial
O endpoint será:

- **POST /avaliacoes**

---

## 7. Fluxos do sistema

### Fluxo principal de recebimento
1. O cliente envia um feedback para a API Quarkus.
2. A API valida os dados.
3. A API classifica a urgência.
4. A API persiste o feedback no PostgreSQL.
5. A API grava um evento na tabela de outbox.
6. Um publicador assíncrono envia esse evento para o **Azure Service Bus Topic**.
7. A Azure Function `CriticalFeedbackNotifier` consome a subscription correspondente.
8. Se a urgência for alta, o sistema envia e-mail aos administradores.
9. O processamento é registrado para evitar duplicidade.

### Fluxo do relatório semanal
1. A Azure Function `WeeklyFeedbackReportGenerator` é acionada por agendamento.
2. Ela consulta os feedbacks do período semanal.
3. Calcula:
   - média das notas
   - quantidade por dia
   - quantidade por urgência
4. Monta o relatório em HTML.
5. Envia aos administradores.

---

## 8. Funções serverless

### 8.1 `CriticalFeedbackNotifier`
Responsabilidade única:
- consumir evento de feedback criado
- verificar urgência
- enviar alerta por e-mail quando necessário
- registrar sucesso, falha e idempotência do processamento

### 8.2 `WeeklyFeedbackReportGenerator`
Responsabilidade única:
- executar por agendamento
- consultar dados da semana
- consolidar métricas
- gerar relatório
- enviar e-mail aos administradores

Essas duas funções atendem ao requisito mínimo de serverless com responsabilidade única.

---

## 9. Modelo de dados inicial

### Entidade `Feedback`
Campos principais:
- `id`
- `descricao`
- `nota`
- `urgencia`
- `dataCriacao`
- `alertaEnviado`
- `dataEnvioAlerta`
- `statusProcessamento`

### Enum `Urgencia`
- `BAIXA`
- `MEDIA`
- `ALTA`

### Entidade `OutboxEvent`
Campos sugeridos:
- `id`
- `aggregateId`
- `eventType`
- `payload`
- `status`
- `createdAt`
- `publishedAt`
- `retryCount`

### Tabela opcional futura
`report_execution`
- `id`
- `periodoInicio`
- `periodoFim`
- `dataExecucao`
- `status`
- `mediaAvaliacoes`

No MVP, essa última pode ficar apenas em logs.

---

## 10. Contrato inicial da API

### Endpoint principal
`POST /avaliacoes`

### Payload
```json
{
  "descricao": "A aula foi boa, mas o áudio estava ruim",
  "nota": 5
}
```

### Resposta sugerida
```json
{
  "id": "uuid",
  "descricao": "A aula foi boa, mas o áudio estava ruim",
  "nota": 5,
  "urgencia": "MEDIA",
  "dataCriacao": "2026-04-21T10:30:00-03:00"
}
```

### Validações
- descrição obrigatória
- tamanho mínimo e máximo
- nota obrigatória
- nota entre 0 e 10

---

## 11. Banco de dados

### Escolha
- **Azure Database for PostgreSQL**

### Justificativa
- ótimo encaixe com Quarkus
- relacional e simples para o domínio
- adequado para consulta e agregação de relatórios
- solução madura e fácil de justificar academicamente

---

## 12. Mensageria

### Tecnologia
- **Azure Service Bus Topic**

### Estratégia
- a API publica `feedback.created`
- a Function `CriticalFeedbackNotifier` consome por uma **subscription**
- novos consumidores podem ser adicionados no futuro sem alterar o produtor

### Benefícios
- desacoplamento
- extensibilidade
- resiliência
- preparação para evolução arquitetural

---

## 13. Confiabilidade e integridade operacional

### Padrão adotado
- **Transactional Outbox**

### Objetivo
Garantir que:
- o feedback não seja salvo sem que o evento correspondente fique registrado
- o evento não seja perdido em caso de falha entre persistência e publicação

### Complementos importantes
- idempotência no processamento da Function
- retry controlado
- dead-letter handling no Service Bus
- registro de falhas em logs e telemetria

---

## 14. Monitoramento e observabilidade

### Base principal
- **Azure Monitor**
- **Application Insights**

### Responsabilidades dessa camada
- logs
- traces
- exceptions
- latência
- correlação entre API, publicação e Functions
- disponibilidade operacional

### Camada complementar
- **Prometheus**
- **Grafana**

### Papel dessa camada
- dashboards técnicos
- dashboards de negócio
- visualização para demonstração em vídeo
- exploração de métricas expostas pelo Quarkus

### Métricas técnicas
- total de requisições
- tempo médio de resposta
- taxa de erro
- health check
- consumo de memória
- tempo de processamento de eventos

### Métricas de negócio
- total de feedbacks recebidos
- quantidade por urgência
- total de alertas enviados
- média semanal de notas
- quantidade de feedbacks por dia

---

## 15. Segurança

### Estratégia do MVP
- sem sistema completo de login
- proteção simples, clara e defensável

### Medidas práticas
- secrets fora do código
- uso de `Managed Identity` quando possível
- uso de `Azure Key Vault`
- API Key para endpoints administrativos, se existirem
- acesso mínimo aos recursos
- logs sem dados sensíveis

### Observação
Isso atende ao espírito do enunciado, que exige segurança de dados e governança de acesso.

---

## 16. Deploy e operação

### Pipeline
- **GitHub Actions**

### Responsabilidades do pipeline
- build
- testes
- empacotamento
- build da imagem da API
- deploy no Azure Container Apps
- deploy das Azure Functions

### Hospedagem

#### API principal
- **Azure Container Apps**

#### Funções
- **Azure Functions**

#### Banco
- **Azure Database for PostgreSQL**

#### Mensageria
- **Azure Service Bus Topic**

#### Segredos
- **Azure Key Vault**

---

## 17. Escopo do MVP

### Entrará no MVP
- criar feedback
- validar entrada
- classificar urgência
- persistir feedback
- gravar outbox
- publicar evento
- enviar alerta para feedback crítico
- gerar relatório semanal
- monitorar aplicação
- pipeline automatizado
- documentação técnica
- vídeo de demonstração

### Não entrará no MVP
- dashboard administrativo web
- autenticação completa com usuários
- edição de feedback
- exclusão de feedback
- coleta de dados pessoais do aluno
- moderação avançada
- analytics complexa

---

## 18. Estrutura técnica

```text
src/main/java
  ├── domain
  │   ├── model
  │   ├── enums
  │   ├── services
  │   └── rules
  ├── application
  │   ├── usecase
  │   ├── dto
  │   ├── ports
  │   └── mapper
  ├── infrastructure
  │   ├── persistence
  │   ├── messaging
  │   ├── outbox
  │   ├── email
  │   ├── monitoring
  │   └── config
  └── entrypoint
      └── rest
```

### Diretrizes
- controller fino
- regras de negócio fora da infraestrutura
- portas apontando para dentro
- adapters implementando detalhes externos
- testes unitários e de integração desde cedo

---

## 19. Resumo executivo

Vamos construir uma **plataforma de feedback em Azure**, com:

- **API Quarkus**
- **Azure Container Apps**
- **Azure Database for PostgreSQL**
- **Azure Service Bus Topic + Subscription**
- **duas Azure Functions**
  - uma para alertas críticos
  - uma para relatório semanal
- **Transactional Outbox**
- **Application Insights + Azure Monitor** como observabilidade principal
- **Prometheus + Grafana** como camada complementar de dashboards e métricas
- **GitHub Actions** para deploy automatizado
- **Managed Identity + Key Vault** como base de segurança operacional

### Regra de urgência
- `ALTA`: nota 0–3 ou palavras críticas
- `MEDIA`: nota 4–6
- `BAIXA`: nota 7–10

### Estratégia de entrega
- foco em MVP bem feito
- arquitetura limpa
- separação de responsabilidades
- confiabilidade desde o início
- documentação forte
- narrativa técnica sólida para banca e vídeo
