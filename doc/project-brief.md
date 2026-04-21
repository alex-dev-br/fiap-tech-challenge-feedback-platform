# Project Brief

## 1. Visão geral

Este projeto implementa uma **plataforma de feedback para aulas e cursos online**, permitindo que estudantes enviem avaliações, que feedbacks críticos sejam identificados automaticamente e que administradores recebam alertas e relatórios consolidados.

A solução será desenvolvida em **Microsoft Azure**, com uso obrigatório de **serverless**, conforme o desafio acadêmico. A arquitetura foi definida com foco em **Clean Architecture**, **Clean Code**, **baixo acoplamento**, **testabilidade**, **observabilidade** e **facilidade de evolução**.

---

## 2. Objetivo do produto

Disponibilizar um sistema capaz de:

- receber avaliações de estudantes
- classificar automaticamente a urgência dos feedbacks
- notificar administradores sobre situações críticas
- gerar relatório semanal com consolidação das avaliações
- manter rastreabilidade, confiabilidade operacional e monitoramento

---

## 3. Objetivo técnico

Construir uma solução cloud-native com:

- **API Quarkus** como ponto principal de entrada
- **Azure Functions** para responsabilidades serverless
- **Azure Service Bus** para comunicação assíncrona
- **Azure Database for PostgreSQL** para persistência relacional
- **Transactional Outbox** para confiabilidade entre persistência e publicação
- **Azure Monitor + Application Insights** para observabilidade principal
- **Prometheus + Grafana** para dashboards de métricas
- **GitHub Actions** para CI/CD

---

## 4. Escopo funcional do MVP

O MVP deverá contemplar:

- criação de feedback
- validação de entrada
- classificação de urgência
- persistência do feedback
- gravação do evento em outbox
- publicação assíncrona do evento `feedback.created`
- envio de alerta automático para feedback crítico
- geração de relatório semanal por e-mail
- monitoramento da API e das Azure Functions
- pipeline automatizado de build, testes e deploy
- documentação técnica da solução

---

## 5. Fora do escopo do MVP

Não farão parte da primeira entrega:

- dashboard administrativo web
- autenticação completa com usuários
- edição de feedback
- exclusão de feedback
- coleta de dados pessoais do estudante
- analytics avançada
- anexos complexos no relatório
- gestão de múltiplos perfis administrativos em banco

---

## 6. Usuários e atores do sistema

### Estudante
Responsável por enviar o feedback.

### Administrador
Responsável por receber alertas críticos e relatórios semanais.

### Sistema de monitoramento
Responsável por acompanhar métricas, logs, falhas e comportamento operacional.

### Serviços de infraestrutura
Componentes da Azure responsáveis por execução, mensageria, persistência, segredos e observabilidade.

---

## 7. Premissas funcionais

### Regra de urgência

- **ALTA**
  - nota entre 0 e 3
  - ou descrição com palavras-chave críticas
- **MEDIA**
  - nota entre 4 e 6
- **BAIXA**
  - nota entre 7 e 10

### Palavras críticas iniciais

- erro
- travando
- bug
- péssimo
- horrível
- não funciona
- reclamação
- insuportável

### Administradores

Os administradores serão definidos via configuração, por exemplo:

- `ADMIN_EMAILS`

### Privacidade

No MVP, não serão coletados dados pessoais do estudante.

### Relatório semanal

Será enviado por **e-mail em HTML**, contendo:

- média das avaliações
- quantidade de avaliações por dia
- quantidade de avaliações por urgência
- resumo dos feedbacks do período

---

## 8. Requisitos funcionais

- Permitir envio de feedback com descrição e nota.
- Validar se a nota está entre 0 e 10.
- Classificar a urgência automaticamente.
- Persistir feedbacks em banco relacional.
- Publicar evento de feedback criado.
- Processar alertas para feedbacks críticos.
- Gerar relatório semanal agendado.
- Enviar relatório aos administradores.

---

## 9. Requisitos não funcionais

- Execução em cloud
- Uso obrigatório de serverless
- Pelo menos duas funções serverless
- Separação clara de responsabilidades
- Monitoramento completo da aplicação
- Deploy automatizado
- Segurança e governança de acesso
- Logs, métricas e rastreabilidade operacional
- Arquitetura preparada para evolução futura

---

## 10. Arquitetura resumida

### Backend principal
- **Quarkus**
- hospedado em **Azure Container Apps**

### Persistência
- **Azure Database for PostgreSQL**

### Mensageria
- **Azure Service Bus Topic + Subscription**

### Serverless
- **Azure Functions**
  - `CriticalFeedbackNotifier`
  - `WeeklyFeedbackReportGenerator`

### Confiabilidade
- **Transactional Outbox**

### Observabilidade
- **Azure Monitor + Application Insights**
- **Prometheus + Grafana**

### Segurança
- **Managed Identity**
- **Azure Key Vault**
- segredos fora do código
- princípio do menor privilégio

### CI/CD
- **GitHub Actions**

---

## 11. Fluxo principal do sistema

1. O cliente envia um feedback para a API.
2. A API valida e classifica a urgência.
3. O feedback é persistido no PostgreSQL.
4. Um evento é gravado na tabela de outbox.
5. Um publicador assíncrono envia o evento ao Service Bus Topic.
6. A Azure Function `CriticalFeedbackNotifier` consome a subscription.
7. Se o feedback for crítico, envia alerta por e-mail.
8. A Azure Function `WeeklyFeedbackReportGenerator` executa semanalmente.
9. O relatório consolidado é enviado aos administradores.

---

## 12. Endpoint inicial

### `POST /avaliacoes`

#### Exemplo de entrada

```json
{
  "descricao": "A aula foi boa, mas o áudio estava ruim",
  "nota": 5
}
```

#### Exemplo de resposta

```json
{
  "id": "uuid",
  "descricao": "A aula foi boa, mas o áudio estava ruim",
  "nota": 5,
  "urgencia": "MEDIA",
  "dataCriacao": "2026-04-21T10:30:00-03:00"
}
```

---

## 13. Critérios de sucesso do projeto

O projeto será considerado bem-sucedido se:

- o feedback puder ser criado com sucesso
- a urgência for calculada corretamente
- o evento for publicado de forma confiável
- alertas críticos forem enviados automaticamente
- o relatório semanal for gerado e enviado
- a aplicação estiver monitorada
- o deploy estiver automatizado
- a documentação estiver clara e reutilizável
- a solução estiver demonstrável em vídeo com narrativa técnica consistente

---

## 14. Estratégia de entrega

A implementação seguirá uma abordagem incremental:

1. fundação do projeto e estrutura arquitetural
2. API e persistência
3. outbox e publicação de eventos
4. função de alerta crítico
5. função de relatório semanal
6. observabilidade
7. segurança e configuração
8. pipeline e documentação final

---

## 15. Riscos principais

- complexidade de integração entre API, outbox, Service Bus e Functions
- configuração inicial da infraestrutura na Azure
- definição de monitoramento sem excesso de complexidade
- tratamento de idempotência e falhas transitórias
- controle de escopo para manter o MVP viável

---

## 16. Diretriz final

O projeto será conduzido com foco em:

- qualidade arquitetural
- simplicidade do MVP
- aderência ao enunciado
- redução de retrabalho futuro
- documentação técnica forte
- solução demonstrável e bem defendida em banca
