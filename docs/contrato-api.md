# Contrato de API — Portal do Cliente (EcoVolt 360)

Paths e payloads em **inglês** (paths `kebab-case`/`plural`, JSON **camelCase**). Alinhado ao [modelo de banco](modelo-banco-cliente.md). Publicado na wiki: [Endpoints possíveis](https://branchmaster.atlassian.net/wiki/spaces/SCRUM/pages/23298049/Endpoints+possiveis).

Sem prefixo `/api/v1`. Datas em ISO-8601 UTC. Auth: `Authorization: Bearer <jwt>` quando indicado.

---

## Convenções

| Item | Valor |
| --- | --- |
| Roles no JWT | `CUSTOMER` \| `SUPPORT` |
| Erro | `{ "code": "STRING", "message": "texto para o usuário" }` |
| IDs | UUID em path e JSON |
| Senha | 8–25 caracteres; `passwordConfirm` só no frontend |
| Idle | 15 minutos; `POST /session/activity` atualiza `lastAccessAt` |
| Mensagens do ticket | Refresh manual (sem polling/WebSocket) |

### Enums (iguais ao banco)

**`status`** (project e ticket):

`PENDING` | `IN_REVIEW` | `IN_PROGRESS` | `AWAITING_INSTALLATION` | `INSTALLATION_SCHEDULED` | `INSTALLED` | `COMPLETED`

**`ticket.category`** (fixo; wiki em PT → EN):

`INSTALLATION` | `PHOTOVOLTAIC_SYSTEM` | `MAINTENANCE` | `FINANCIAL` | `PROJECT` | `CUSTOMER_SERVICE`

**`faq_item.category`:**

`SOLAR_ENERGY` | `INSTALLATION_AND_PROJECTS` | `MAINTENANCE` | `PAYMENTS` | `SUPPORT`

---

## Tipos compartilhados

```json
{
  "zipCode": "90000000",
  "state": "RS",
  "city": "Porto Alegre",
  "neighborhood": "Centro",
  "street": "Rua Exemplo",
  "number": "100",
  "complement": "Apto 1"
}
```

`Address` — `complement` opcional.

```json
{
  "id": "uuid",
  "name": "Aline Oliveira",
  "email": "aline@ecovolt.com"
}
```

`AgentSummary` — usado em ticket e atribuição.

```json
{
  "id": "uuid",
  "code": "TK-2026-0012",
  "subject": "Inversor piscando",
  "category": "PHOTOVOLTAIC_SYSTEM",
  "status": "IN_PROGRESS",
  "projectId": "uuid",
  "projectCode": "SOLAR-7842-SP",
  "openedAt": "2026-09-01T12:00:00Z",
  "closedAt": null,
  "assignedAgent": { "id": "uuid", "name": "Aline Oliveira", "email": "aline@ecovolt.com" }
}
```

`TicketSummary` — `assignedAgent` nulo se não houver atribuição vigente.

---

## Autenticação e sessão (EP01)

### `GET /customers/{cpf}`

**Auth:** public. Pré-valida CPF na base EcoVolt **antes** do submit de cadastro.

**Path:** `cpf` — 11 dígitos.

**200**

```json
{ "cpf": "12345678901", "eligible": true }
```

**404** — `code: CPF_NOT_FOUND` — message: `você não possui cadastro ativo em nosso sistema`

Wiki listava `/users/{cpf}`; path oficial neste contrato: `/customers/{cpf}`.

### `POST /customers`

**Auth:** public. **201** sem token — o cliente vai ao login.

```json
{
  "cpf": "12345678901",
  "name": "Maria Silva",
  "email": "maria@email.com",
  "password": "Senha123",
  "phone": "51999999999",
  "address": {
    "zipCode": "90000000",
    "city": "Porto Alegre",
    "state": "RS",
    "neighborhood": "Centro",
    "street": "Rua Exemplo",
    "number": "100",
    "complement": "Apto 1"
  }
}
```

**201**

```json
{ "id": "uuid", "email": "maria@email.com" }
```

**409** — CPF: `este cpf já consta em nossa base de dados` · e-mail: `este email já consta em nossa base de dados`

### `POST /login`

**Auth:** public.

```json
{ "email": "maria@email.com", "password": "Senha123" }
```

**200**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "role": "CUSTOMER",
  "customer": {
    "id": "uuid",
    "name": "Maria Silva",
    "email": "maria@email.com"
  }
}
```

`role` `SUPPORT` omite `customer` e inclui `agent: { "id", "name", "email" }`. Senha validada no backend (não Keycloak).

### `POST /logout`

**Auth:** token. Body `{}`. Invalida a sessão. **204** vazio.

### `GET /session`

**Auth:** token.

**200**

```json
{
  "role": "CUSTOMER",
  "customer": {
    "id": "uuid",
    "name": "Maria Silva",
    "email": "maria@email.com",
    "cpf": "12345678901",
    "phone": "51999999999",
    "address": {
      "zipCode": "90000000",
      "state": "RS",
      "city": "Porto Alegre",
      "neighborhood": "Centro",
      "street": "Rua Exemplo",
      "number": "100",
      "complement": "Apto 1"
    }
  }
}
```

### `GET /home`

**Auth:** `CUSTOMER`. Agrega **todos** os projetos do titular.

**200**

```json
{
  "energyGeneratedKwh": 1240.5,
  "averageMonthlyConsumptionKwh": 380.0,
  "savingsBrl": 890.4,
  "projectCount": 2,
  "recentTickets": [
    {
      "id": "uuid",
      "code": "TK-2026-0015",
      "subject": "Painel e inversor",
      "category": "PHOTOVOLTAIC_SYSTEM",
      "status": "IN_PROGRESS",
      "projectId": "uuid",
      "projectCode": "SOLAR-7842-SP",
      "openedAt": "2026-09-01T12:00:00Z",
      "closedAt": null,
      "assignedAgent": { "id": "uuid", "name": "Roberto Souza", "email": "roberto@ecovolt.com" }
    }
  ]
}
```

### `POST /session/activity`

**Auth:** token. Body `{}`. Persiste `lastAccessAt`. **204**.

### `GET /session/activity`

**Auth:** token.

**200**

```json
{ "lastAccessAt": "2026-09-08T18:00:00Z", "idleExpiresAt": "2026-09-08T18:15:00Z" }
```

### `POST /password-recovery`

**Auth:** public. Sempre **204** (não revela se o e-mail existe). Envia link.

```json
{ "email": "maria@email.com" }
```

Não está na wiki; entra pelo fluxo de OTP/link do ER (`access_recovery`).

### `POST /password-reset`

**Auth:** public.

```json
{ "token": "token-do-link", "password": "NovaSenha123" }
```

**204**. **400** se token inválido/expirado/já usado.

---

## FAQ (EP02)

Mesmo conteúdo público e autenticado. Busca por palavra-chave é **local no frontend** (sem `GET /faq/search`).

Categorias são **enum** (sem CRUD). Wiki listava POST/PUT/DELETE de categorias para Support — fora deste contrato.

### `GET /faq`

**Auth:** public. Query opcional: `category`.

**200**

```json
{
  "items": [
    {
      "id": "uuid",
      "category": "SOLAR_ENERGY",
      "question": "Como funciona a energia solar?",
      "answer": "…",
      "sortOrder": 1
    }
  ]
}
```

### `GET /faq/{faqId}`

**Auth:** public. **200** — um item no mesmo formato (sem envelope `items`).

### `GET /faq/categories`

**Auth:** public.

**200**

```json
{
  "items": [
    { "value": "SOLAR_ENERGY", "label": "Energia solar" },
    { "value": "INSTALLATION_AND_PROJECTS", "label": "Instalação e projetos" },
    { "value": "MAINTENANCE", "label": "Manutenção" },
    { "value": "PAYMENTS", "label": "Pagamentos" },
    { "value": "SUPPORT", "label": "Suporte" }
  ]
}
```

### `POST /faq`

**Auth:** `SUPPORT`.

```json
{
  "category": "SOLAR_ENERGY",
  "question": "…",
  "answer": "…",
  "sortOrder": 1
}
```

**201** — objeto `faq_item`.

### `PUT /faq/{faqId}`

**Auth:** `SUPPORT`. Mesmo body do POST (campos parciais permitidos). **200** — item atualizado.

### `DELETE /faq/{faqId}`

**Auth:** `SUPPORT`. **204**. Preferir `published: false` via PUT se for despublicar.

---

## Tickets — Customer (EP06)

Abertura **sem anexo**. Ticket **obrigatório** a um `projectId`. Categoria **não** vai na abertura (suporte define no assign). Status inicial: `PENDING`.

`POST .../messages` só com ticket em `IN_PROGRESS`.

`POST .../feedback` só com ticket em `COMPLETED`.

### `POST /tickets`

**Auth:** `CUSTOMER`.

```json
{
  "projectId": "uuid",
  "subject": "Inversor piscando",
  "body": "Após a tempestade o LED ficou vermelho."
}
```

Regras: `subject` max **20**; `body` max **150**.

**201** — `TicketSummary` + `body` da mensagem inicial (a descrição vira a primeira `ticket_message` do customer).

### `GET /tickets`

**Auth:** `CUSTOMER`. Query: `status`, `q` (filtra `code`/`subject`).

**200**

```json
{ "items": [ { } ] }
```

Itens: `TicketSummary`.

### `GET /tickets/{ticketId}`

**Auth:** `CUSTOMER`. Só o próprio titular.

**200**

```json
{
  "id": "uuid",
  "code": "TK-2026-0012",
  "subject": "Inversor piscando luz vermelha",
  "category": "PHOTOVOLTAIC_SYSTEM",
  "status": "COMPLETED",
  "projectId": "uuid",
  "projectCode": "SOLAR-7842-SP",
  "openedAt": "2026-09-01T12:00:00Z",
  "closedAt": "2026-09-03T10:00:00Z",
  "assignedAgent": { "id": "uuid", "name": "Aline Oliveira", "email": "aline@ecovolt.com" },
  "review": {
    "rating": 5,
    "comment": "Resolveu rápido.",
    "createdAt": "2026-09-03T11:00:00Z"
  }
}
```

`review` nulo se ainda não avaliou. `category` nulo até o assign.

### `GET /tickets/{ticketId}/messages`

**Auth:** `CUSTOMER`.

**200**

```json
{
  "items": [
    {
      "id": "uuid",
      "body": "Após a tempestade…",
      "createdAt": "2026-09-01T12:00:00Z",
      "authorType": "CUSTOMER",
      "customerId": "uuid",
      "agentId": null
    },
    {
      "id": "uuid",
      "body": "Solicito o reset de 10 segundos…",
      "createdAt": "2026-09-01T13:00:00Z",
      "authorType": "AGENT",
      "customerId": null,
      "agentId": "uuid"
    }
  ]
}
```

### `POST /tickets/{ticketId}/messages`

**Auth:** `CUSTOMER`. Somente `IN_PROGRESS`.

```json
{ "body": "Já fiz o reset e continua vermelho." }
```

**201** — a mensagem criada.

### `POST /tickets/{ticketId}/feedback`

**Auth:** `CUSTOMER`. Ticket `COMPLETED`; uma avaliação; `rating` 1–5; `comment` opcional max **150**. Vincula ao agent vigente.

```json
{ "rating": 5, "comment": "Atendimento claro." }
```

**201**

```json
{
  "ticketId": "uuid",
  "agentId": "uuid",
  "rating": 5,
  "comment": "Atendimento claro.",
  "createdAt": "2026-09-03T11:00:00Z"
}
```

---

## Tickets — Support (EP02 / US06)

### `GET /support/queue`

**Auth:** `SUPPORT`. Tickets `PENDING` sem atribuição vigente.

**200** — `{ "items": [ TicketSummary ] }` (`assignedAgent` nulo).

### `GET /support/tickets`

**Auth:** `SUPPORT`. Query: `status`, `assignedToMe=true`.

**200** — `{ "items": [ TicketSummary ] }`.

### `GET /support/tickets/{ticketId}`

**Auth:** `SUPPORT`. Mesmo payload do detalhe do customer, incluindo `review` quando existir.

### `GET /support/tickets/{ticketId}/messages`

**Auth:** `SUPPORT`. Mesmo payload da thread do customer.

### `PATCH /support/tickets/{ticketId}/assign`

**Auth:** `SUPPORT`. Categoria obrigatória; status → `IN_PROGRESS`; grava `ticket_assignment`. **409** se já houver atribuição vigente (concorrência).

```json
{ "category": "INSTALLATION" }
```

**200** — `TicketSummary` com `assignedAgent` = o suporte autenticado.

### `PATCH /support/tickets/{ticketId}/status`

**Auth:** `SUPPORT`.

```json
{ "status": "COMPLETED" }
```

Valores: o enum unificado. `COMPLETED` preenche `closedAt`.

**200** — `TicketSummary`.

### `POST /support/tickets/{ticketId}/messages`

**Auth:** `SUPPORT`. Somente `IN_PROGRESS`.

```json
{ "body": "Solicito o reset de 10 segundos no botão azul." }
```

**201** — mensagem com `authorType: "AGENT"`.

---

## Projects — Customer (EP06 / US17)

### `GET /projects`

**Auth:** `CUSTOMER`.

**200**

```json
{
  "items": [
    {
      "id": "uuid",
      "code": "SOLAR-7842-SP",
      "name": "Instalação Residencial",
      "status": "IN_PROGRESS",
      "createdAt": "2026-04-18T00:00:00Z",
      "address": {
        "zipCode": "90000000",
        "state": "RS",
        "city": "Porto Alegre",
        "neighborhood": "Centro",
        "street": "Rua Exemplo",
        "number": "100",
        "complement": null
      },
      "metric": {
        "energyGeneratedKwh": 620.25,
        "averageMonthlyConsumptionKwh": 190.0,
        "savingsBrl": 445.2,
        "updatedAt": "2026-09-01T00:00:00Z"
      }
    }
  ]
}
```

### `GET /projects/{projectId}`

**Auth:** `CUSTOMER`. **200** — um item no mesmo formato (sem `items`).

### `GET /projects/{projectId}/history`

**Auth:** `CUSTOMER`. Histórico de status (payload previsto; tabela de auditoria ainda não está no ER).

**200**

```json
{
  "items": [
    {
      "status": "PENDING",
      "changedAt": "2026-04-01T10:00:00Z",
      "note": null
    },
    {
      "status": "IN_PROGRESS",
      "changedAt": "2026-04-18T10:00:00Z",
      "note": "Incluir placa fotovoltaica"
    }
  ]
}
```

---

## Projects — Support (EP06)

### `GET /support/projects`

**Auth:** `SUPPORT`. Query: `status`. **200** — `{ "items": [ ... ] }` no formato da lista do customer.

### `PATCH /support/projects/{projectId}/assign`

**Auth:** `SUPPORT`. Body `{}` (agente = token). Fora do MVP transferir entre suportes.

**200** — project atualizado.

### `PATCH /support/projects/{projectId}/status`

**Auth:** `SUPPORT`.

```json
{ "status": "AWAITING_INSTALLATION" }
```

Ao entrar em `AWAITING_INSTALLATION`, `expectedInstallationDate` é obrigatório (via PATCH do project, abaixo, na mesma transação ou **400** se ausente).

### `PATCH /support/projects/{projectId}`

**Auth:** `SUPPORT`.

```json
{ "expectedInstallationDate": "2026-12-01" }
```

Campo **não** está no ER atual; entra só neste contrato de Support até o modelo ganhar a coluna.

### `GET /support/projects/{projectId}/history`

**Auth:** `SUPPORT`. Mesmo payload de `GET /projects/{projectId}/history`.

---

## Satisfação — Support (EP05)

### `GET /support/feedback/metrics`

**Auth:** `SUPPORT`. Query: `period=year|quarter|month|semester`.

**200**

```json
{
  "period": "month",
  "averageRating": 4.6,
  "totalReviews": 42,
  "reviewedTickets": 42
}
```

Comentários individuais só no GET detalhe do ticket (sem lista agregada de comments).

---

## Fora do MVP (sem endpoint)

| Funcionalidade | Motivo |
| --- | --- |
| `POST /tickets/{id}/reopen` | Não reabrir chamado |
| `POST /tickets/{id}/attachments` | Sem anexos |
| `GET /support/feedback` | Comentários no detalhe |
| `GET /faq/search` | Busca no frontend |
| Transferência de project entre suportes | Fora do MVP |
| `POST/PUT/DELETE /faq/categories` | Categorias são enum |

---

## O que mudou em relação a 28/08

A wiki [Endpoints possíveis](https://branchmaster.atlassian.net/wiki/spaces/SCRUM/pages/23298049/Endpoints+possiveis) já está nesta versão.

| Antes (28/08) | Agora |
| --- | --- |
| `GET /users/{cpf}` | `GET /customers/{cpf}` |
| `POST /customers` `fullName` | `name` |
| FAQ `pergunta` / `resposta` / `categoriaFAQId` | `question` / `answer` / `category` |
| Ticket `title` / `description` | `subject` / `body` + `projectId` |
| Mensagem `content` | `body` |
| Feedback `score` | `rating` |
| Role `CLIENT` | `CUSTOMER` |
| Ticket status `Open` / `InProgress` / `Closed` | Enum unificado (`PENDING` … `COMPLETED`) |
| Project status sem `INSTALLATION_SCHEDULED` / `COMPLETED` | Enum unificado |
| Categoria de ticket em português | Enum inglês acima |
| Sem recuperação de senha | `POST /password-recovery` e `POST /password-reset` |
