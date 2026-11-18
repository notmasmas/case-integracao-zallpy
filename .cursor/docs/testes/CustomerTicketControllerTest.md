# Documentação de Testes Unitários — CustomerTicketController

## 1. Informações gerais

- **Serviço:** CustomerTicketController
- **Arquivo de implementação:** CustomerTicketController.java
- **Arquivo de testes:** CustomerTicketControllerTest.java
- **Framework de testes:** JUnit 5, Mockito, Spring MockMvc (standalone)
- **Dependências simuladas:** TicketService
- **Responsabilidade do serviço:** API HTTP do cliente: criar, listar, detalhar, avaliar, status e categoria. Advice: `TicketExceptionHandler`, `AccessExceptionHandler`.

---

## 2. Objetivo dos testes

Proteger o contrato HTTP autenticado como CUSTOMER: rotas devolvem 201/200 com JSON esperado; `TicketNotFoundException` → 404; `TicketAlreadyEvaluatedException` → 409. Regras de domínio ficam no serviço.

---

## 3. Cenários de testes

### 3.1. createTicket

**Método:** createTicket()

**Objetivo:**
POST `/tickets` cria o chamado.

#### Teste 01 — shouldCreateTicket

- **Cenário:** Service devolve `details()` PENDING.
- **Entrada:** JSON com projectId, título `Painel solar`, descrição `Inversor sem comunicacao`.
- **Comportamento esperado:** 201 e Location `/tickets/{id}`.
- **Validações (Assertions):**
  - Status 201, header Location, `$.id`, `$.title`, `$.status` `PENDING`.
- **Resultado:** Aprovado se status, header e JSON passarem.

---

### 3.2. listTickets

**Método:** listTickets()

**Objetivo:**
GET `/tickets` lista os chamados do cliente.

#### Teste 02 — shouldListCustomerTickets

- **Cenário:** Service devolve um `TicketSummaryDTO`.
- **Entrada:** GET `/tickets`.
- **Comportamento esperado:** 200 com o título do stub.
- **Validações (Assertions):**
  - `$[0].id` e `$[0].title` `Painel solar`.
- **Resultado:** Aprovado se a lista JSON bater.

---

### 3.3. getTicket

**Método:** getTicket()

**Objetivo:**
GET por id e 404 quando o serviço não acha o chamado.

#### Teste 03 — shouldGetCustomerTicket

- **Cenário:** Service devolve `details()`.
- **Entrada:** GET `/tickets/{id}`.
- **Comportamento esperado:** 200 com id e `totalMessages` 0.
- **Validações (Assertions):**
  - `$.id`, `$.totalMessages` 0.
- **Resultado:** Aprovado se o detalhe bater.

#### Teste 04 — shouldReturnNotFoundWhenTicketDoesNotExist

- **Cenário:** Service lança `TicketNotFoundException`.
- **Entrada:** GET `/tickets/{id}`.
- **Comportamento esperado:** 404 `TICKET_NOT_FOUND`.
- **Validações (Assertions):**
  - Status 404, `$.code` `TICKET_NOT_FOUND`, mensagem `Chamado não encontrado.`
- **Resultado:** Aprovado se o payload de erro bater.

---

### 3.4. evaluateTicket

**Método:** evaluateTicket()

**Objetivo:**
PATCH de avaliação e 409 se já avaliado.

#### Teste 05 — shouldEvaluateTicket

- **Cenário:** Service devolve ticket CLOSED avaliado com 5 / `Excelente`.
- **Entrada:** PATCH evaluation `{ "evaluation": 5, "comment": "Excelente" }`.
- **Comportamento esperado:** 200 com nota e comentário.
- **Validações (Assertions):**
  - `$.evaluate` 5, `$.evaluateComment` `Excelente`.
  - `verify` em `evaluateTicket`.
- **Resultado:** Aprovado se JSON e verify passarem.

#### Teste 06 — shouldReturnConflictWhenTicketWasAlreadyEvaluated

- **Cenário:** Service lança `TicketAlreadyEvaluatedException`.
- **Entrada:** PATCH evaluation nota 4.
- **Comportamento esperado:** 409 `TICKET_ALREADY_EVALUATED`.
- **Validações (Assertions):**
  - Status 409, `$.code` `TICKET_ALREADY_EVALUATED`.
- **Resultado:** Aprovado se o conflito HTTP bater.

---

### 3.5. updateStatus

**Método:** updateStatus()

**Objetivo:**
PATCH de status do cliente.

#### Teste 07 — shouldUpdateStatus

- **Cenário:** Service devolve `details()`.
- **Entrada:** PATCH status `IN_PROGRESS`.
- **Comportamento esperado:** 200 com id do ticket.
- **Validações (Assertions):**
  - Status 200, `$.id`.
- **Resultado:** Aprovado se o 200 bater.

---

### 3.6. updateCategory

**Método:** updateCategory()

**Objetivo:**
PATCH de categoria.

#### Teste 08 — shouldUpdateCategory

- **Cenário:** Service devolve DTO com `EQUIPMENT`.
- **Entrada:** PATCH `{ "category": "EQUIPMENT" }`.
- **Comportamento esperado:** 200 com a categoria.
- **Validações (Assertions):**
  - `$.category` `EQUIPMENT`.
- **Resultado:** Aprovado se a categoria JSON bater.

---

## 4. Cenários de exceção e casos de borda

| Cenário | Comportamento esperado |
|---|---|
| Ticket inexistente no GET | 404 `TICKET_NOT_FOUND` |
| Avaliação já feita | 409 `TICKET_ALREADY_EVALUATED` |
| Demais falhas de domínio (aberto, fechado, forbidden) | Não nesta suíte |

---

## 5. Dependências e mocks

| Dependência | Tipo | Comportamento simulado |
|---|---|---|
| TicketService | Mock | DTOs stubados ou exceções |
| SecurityContext | Autenticação fake | `AuthenticatedUser` CUSTOMER, USER_ID fixo |

---

## 6. Cobertura dos requisitos

| Regra de negócio | Teste relacionado | Coberto? |
|---|---|---|
| CRUD HTTP do cliente | Testes 01–03, 05, 07, 08 | Sim |
| 404 / 409 de ticket | Testes 04 e 06 | Sim |
| Ownership, avaliação, corridas | TicketServiceTest | Não |

---

## 7. Observações

- Sem filtro JWT: o `@BeforeEach` preenche o `SecurityContext`.
- Para executar só esta classe, a partir de `backend/`:

```powershell
.\mvnw.cmd -Dtest=CustomerTicketControllerTest test
```
