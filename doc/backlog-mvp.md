# Backlog MVP

## 1. Objetivo do backlog

Este backlog organiza a implementação do MVP em uma sequência pragmática, reduzindo riscos técnicos e priorizando a entrega incremental de valor.

Cada item está descrito com:

- objetivo
- entregáveis
- critérios de pronto
- dependências principais

---

## 2. Ordem macro de execução

1. Fundação do projeto
2. Modelagem de domínio e persistência
3. API de criação de feedback
4. Outbox e publicação de eventos
5. Função de notificação crítica
6. Função de relatório semanal
7. Observabilidade
8. Segurança e configuração
9. CI/CD
10. Documentação final e roteiro de demonstração

---

## 3. Backlog detalhado

## Épico 1 — Fundação do projeto

### Item 1.1 — Criar repositório e estrutura inicial
**Objetivo**
Criar a base do projeto com organização coerente com Clean Architecture.

**Entregáveis**
- repositório criado
- estrutura inicial de pacotes
- README inicial
- `.gitignore`
- convenções básicas do projeto

**Critérios de pronto**
- projeto versionado
- estrutura mínima definida
- convenções de nomenclatura estabelecidas

**Dependências**
- nenhuma

---

### Item 1.2 — Inicializar aplicação Quarkus
**Objetivo**
Criar a API principal com dependências essenciais.

**Entregáveis**
- projeto Quarkus inicializado
- build funcionando
- endpoint de health check disponível

**Critérios de pronto**
- aplicação sobe localmente
- build executa sem erro
- health check responde corretamente

**Dependências**
- item 1.1

---

### Item 1.3 — Definir padrão arquitetural e convenções
**Objetivo**
Formalizar como domínio, aplicação e infraestrutura serão separados.

**Entregáveis**
- estrutura de pacotes validada
- convenções de DTOs, portas e adapters
- diretriz de nomenclatura e organização

**Critérios de pronto**
- equipe consegue implementar sem dúvida estrutural
- pastas e camadas possuem responsabilidades claras

**Dependências**
- item 1.2

---

## Épico 2 — Modelagem de domínio e persistência

### Item 2.1 — Modelar domínio de feedback
**Objetivo**
Definir entidades, enums e regras centrais.

**Entregáveis**
- entidade `Feedback`
- enum `Urgencia`
- regra de classificação de urgência
- contratos iniciais do domínio

**Critérios de pronto**
- domínio compilando
- regra de urgência testável
- nomenclatura consistente

**Dependências**
- item 1.3

---

### Item 2.2 — Modelar outbox
**Objetivo**
Criar o modelo de evento persistente para publicação confiável.

**Entregáveis**
- entidade `OutboxEvent`
- status de publicação
- contrato de serialização de payload

**Critérios de pronto**
- entidade modelada
- preparada para persistência e retry

**Dependências**
- item 2.1

---

### Item 2.3 — Configurar PostgreSQL e migrations
**Objetivo**
Preparar persistência relacional e versionamento de schema.

**Entregáveis**
- conexão com PostgreSQL
- migrations iniciais
- tabelas `feedback` e `outbox_event`

**Critérios de pronto**
- banco sobe localmente
- migrations executam com sucesso
- tabelas criadas conforme modelo

**Dependências**
- item 2.2

---

### Item 2.4 — Implementar repositórios
**Objetivo**
Disponibilizar persistência do domínio e do outbox.

**Entregáveis**
- repositório de feedback
- repositório de outbox
- testes de integração de persistência

**Critérios de pronto**
- entidades persistem corretamente
- consultas principais funcionam
- testes passam

**Dependências**
- item 2.3

---

## Épico 3 — API de criação de feedback

### Item 3.1 — Definir contrato REST
**Objetivo**
Criar endpoint `POST /avaliacoes`.

**Entregáveis**
- DTO de request
- DTO de response
- validações de entrada
- documentação inicial do endpoint

**Critérios de pronto**
- endpoint definido
- payload validado
- resposta padronizada

**Dependências**
- item 2.4

---

### Item 3.2 — Implementar caso de uso de criação de feedback
**Objetivo**
Orquestrar validação, classificação de urgência, persistência e criação do outbox.

**Entregáveis**
- use case `CreateFeedback`
- mapeamento DTO → domínio
- persistência transacional de feedback + outbox

**Critérios de pronto**
- feedback é salvo com urgência correta
- evento fica registrado no outbox
- controller permanece fino

**Dependências**
- item 3.1

---

### Item 3.3 — Criar testes da API
**Objetivo**
Garantir o comportamento correto do endpoint principal.

**Entregáveis**
- testes unitários
- testes de integração
- cenários de erro e sucesso

**Critérios de pronto**
- cobertura dos fluxos principais
- validações quebradas retornam erro adequado
- sucesso retorna payload esperado

**Dependências**
- item 3.2

---

## Épico 4 — Outbox e publicação de eventos

### Item 4.1 — Implementar publicador de outbox
**Objetivo**
Publicar eventos pendentes no Service Bus Topic.

**Entregáveis**
- componente de leitura do outbox
- publicação em `feedback.created`
- atualização de status do evento

**Critérios de pronto**
- eventos pendentes são publicados
- status é atualizado corretamente
- falhas são registradas

**Dependências**
- item 3.2

---

### Item 4.2 — Configurar Azure Service Bus Topic
**Objetivo**
Preparar mensageria assíncrona da solução.

**Entregáveis**
- topic criado
- subscription principal criada
- configuração da aplicação atualizada

**Critérios de pronto**
- publicação e consumo podem ser testados
- nomes e contratos de evento estão definidos

**Dependências**
- item 4.1

---

### Item 4.3 — Implementar retry, idempotência e tratamento de falhas
**Objetivo**
Aumentar confiabilidade da publicação.

**Entregáveis**
- retry controlado
- logging estruturado
- base para dead-letter handling

**Critérios de pronto**
- falhas transitórias são tratadas
- reprocessamento não gera efeitos indevidos

**Dependências**
- item 4.2

---

## Épico 5 — Função de notificação crítica

### Item 5.1 — Criar Azure Function `CriticalFeedbackNotifier`
**Objetivo**
Consumir feedbacks criados e notificar administradores em casos críticos.

**Entregáveis**
- Function criada
- consumo da subscription
- leitura do evento de feedback

**Critérios de pronto**
- evento é recebido corretamente
- urgência é interpretada corretamente

**Dependências**
- item 4.2

---

### Item 5.2 — Implementar envio de e-mail
**Objetivo**
Enviar alerta aos administradores.

**Entregáveis**
- adaptador de e-mail
- template HTML de alerta
- suporte a múltiplos administradores

**Critérios de pronto**
- e-mail enviado em caso de urgência alta
- conteúdo contém descrição, urgência e data de envio

**Dependências**
- item 5.1

---

### Item 5.3 — Garantir idempotência do alerta
**Objetivo**
Evitar alertas duplicados.

**Entregáveis**
- registro de processamento
- verificação de duplicidade
- testes do fluxo crítico

**Critérios de pronto**
- reentrega da mensagem não duplica alerta
- comportamento é auditável

**Dependências**
- item 5.2

---

## Épico 6 — Função de relatório semanal

### Item 6.1 — Criar Azure Function `WeeklyFeedbackReportGenerator`
**Objetivo**
Executar geração de relatório por agendamento.

**Entregáveis**
- Function criada
- cron/agendamento configurado
- integração com banco

**Critérios de pronto**
- execução pode ser disparada por timer
- consulta ao período semanal funciona

**Dependências**
- item 2.4

---

### Item 6.2 — Implementar agregações semanais
**Objetivo**
Calcular métricas exigidas pelo enunciado.

**Entregáveis**
- média de avaliações
- quantidade por dia
- quantidade por urgência
- resumo do período

**Critérios de pronto**
- métricas são calculadas corretamente
- período semanal está documentado e consistente

**Dependências**
- item 6.1

---

### Item 6.3 — Implementar envio do relatório
**Objetivo**
Enviar relatório semanal em HTML.

**Entregáveis**
- template HTML do relatório
- envio para administradores
- logs de execução

**Critérios de pronto**
- relatório chega por e-mail
- conteúdo contém dados exigidos pelo desafio

**Dependências**
- item 6.2

---

## Épico 7 — Observabilidade

### Item 7.1 — Integrar Application Insights e Azure Monitor
**Objetivo**
Instrumentar logs, traces e exceptions.

**Entregáveis**
- integração da API
- integração das Functions
- correlação básica entre componentes

**Critérios de pronto**
- logs aparecem na plataforma
- erros podem ser rastreados
- latência básica é observável

**Dependências**
- itens 3.2, 5.1 e 6.1

---

### Item 7.2 — Expor métricas da API
**Objetivo**
Disponibilizar métricas técnicas e de negócio.

**Entregáveis**
- métricas com Micrometer
- indicadores de requisição, erro e latência
- indicadores de feedback e alertas

**Critérios de pronto**
- métricas são consultáveis
- nomes e tags fazem sentido para dashboards

**Dependências**
- item 7.1

---

### Item 7.3 — Criar dashboards
**Objetivo**
Construir visualização para operação e demonstração.

**Entregáveis**
- dashboard técnico
- dashboard de negócio
- visualização adequada para vídeo

**Critérios de pronto**
- dashboards mostram informações úteis
- leitura visual está clara

**Dependências**
- item 7.2

---

## Épico 8 — Segurança e configuração

### Item 8.1 — Externalizar segredos e configurações
**Objetivo**
Retirar informações sensíveis do código.

**Entregáveis**
- variáveis de ambiente
- integração com Key Vault, quando aplicável
- documentação de configuração

**Critérios de pronto**
- aplicação roda sem secrets hardcoded
- configuração por ambiente está clara

**Dependências**
- item 1.2

---

### Item 8.2 — Aplicar menor privilégio
**Objetivo**
Restringir acessos na Azure.

**Entregáveis**
- permissões mínimas definidas
- identities configuradas
- revisão básica de acesso

**Critérios de pronto**
- serviços possuem apenas as permissões necessárias
- arquitetura fica defensável perante banca

**Dependências**
- item 8.1

---

## Épico 9 — CI/CD

### Item 9.1 — Criar pipeline da API
**Objetivo**
Automatizar build, testes e deploy da API.

**Entregáveis**
- workflow GitHub Actions
- build da imagem
- deploy em Azure Container Apps

**Critérios de pronto**
- pipeline executa com sucesso
- deploy pode ser repetido com previsibilidade

**Dependências**
- itens 3.3 e 8.1

---

### Item 9.2 — Criar pipeline das Functions
**Objetivo**
Automatizar build e deploy das Azure Functions.

**Entregáveis**
- workflow GitHub Actions
- empacotamento das Functions
- deploy automatizado

**Critérios de pronto**
- pipeline entrega funções atualizadas com sucesso

**Dependências**
- itens 5.3, 6.3 e 8.1

---

## Épico 10 — Documentação e demonstração

### Item 10.1 — Consolidar documentação técnica
**Objetivo**
Preparar documentação reutilizável para banca e repositório.

**Entregáveis**
- README principal
- arquitetura da solução
- instruções de deploy
- monitoramento
- funções serverless

**Critérios de pronto**
- projeto pode ser entendido por terceiros
- documentação está coerente com a implementação

**Dependências**
- itens anteriores concluídos

---

### Item 10.2 — Preparar roteiro do vídeo
**Objetivo**
Organizar a demonstração final.

**Entregáveis**
- roteiro da demo
- sequência de evidências
- pontos de fala da arquitetura

**Critérios de pronto**
- vídeo pode ser gravado de forma objetiva
- narrativa técnica cobre os critérios do desafio

**Dependências**
- item 10.1

---

## 4. Priorização sugerida

### Prioridade máxima
- itens 1.1 a 4.2
- itens 5.1 a 5.3
- itens 6.1 a 6.3

### Prioridade alta
- itens 7.1 e 8.1
- itens 9.1 e 9.2

### Prioridade média
- item 7.3
- item 8.2
- item 10.2

---

## 5. Definição de pronto do MVP

O MVP estará pronto quando:

- o endpoint `POST /avaliacoes` estiver funcionando
- o feedback for persistido corretamente
- o outbox publicar eventos com confiabilidade básica
- a Function de alerta enviar e-mail para feedback crítico
- a Function de relatório semanal enviar o consolidado
- a aplicação estiver monitorada
- o deploy estiver automatizado
- a documentação principal estiver atualizada

---

## 6. Próximos entregáveis naturais após o MVP

- endpoint administrativo para consulta de feedbacks
- anexo CSV no relatório semanal
- consumer adicional para analytics
- dashboard web administrativo
- autenticação mais robusta
- histórico persistido de execuções de relatório
