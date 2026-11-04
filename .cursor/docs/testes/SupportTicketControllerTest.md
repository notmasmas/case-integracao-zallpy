# Documentação de Testes Unitários — SupportTicketController

## 1. Informações gerais

- **Serviço:** SupportTicketController
- **Arquivo de implementação:** SupportTicketController.java
- **Arquivo de testes:** SupportTicketControllerTest.java
- **Framework de testes:** JUnit 5, Mockito, Spring MockMvc (standalone)
- **Dependências simuladas:** TicketService
- **Responsabilidade do serviço:** API HTTP do atendente: listar atribuídos, listar todos, atualizar status.

---

## 2. Objetivo dos testes

Proteger o contrato HTTP autenticado como SUPPORT: listagens 200 e PATCH de status 200; ticket já fechado → 409 `TICKET_ALREADY_CLOSED`.

---

## 3. Cenários de testes

### 3.1. listTickets

**Método:** listTickets()

**Objetivo:**
GET dos tickets atribuídos ao suporte logado.

#### Teste 01 — shouldListAssignedSupportTickets

- **Cenário:** `listSupportTickets` devolve um summary `Meu chamado`.
- **Entrada:** GET `/support/tickets`.
- **Comportamento esperado:** 200 com o título.
- **Validações (Assertions):**
  - `$[0].title` `Meu chamado`.
- **Resultado:** Aprovado se o JSON bater.

---

### 3.2. listAllTickets

**Método:** listAllTickets()

**Objetivo:**
GET da visão ampla.

#### Teste 02 — shouldListAllTickets

- **Cenário:** `listAllTickets` devolve summary `Todos`.
- **Entrada:** GET `/support/tickets/all`.
- **Comportamento esperado:** 200 com o título.
- **Validações (Assertions):**
  - `$[0].title` `Todos`.
- **Resultado:** Aprovado se o JSON bater.

---

### 3.3. updateStatus

**Método:** updateStatus()

**Objetivo:**
PATCH de status e conflito se o chamado já está encerrado.

#### Teste 03 — shouldUpdateTicketStatus

- **Cenário:** Service devolve detalhe `IN_PROGRESS`.
- **Entrada:** PATCH `/support/tickets/{id}/status` com `"status": "IN_PROGRESS"`.
- **Comportamento esperado:** 200 com status no JSON.
- **Validações (Assertions):**
  - Status 200, `$.status` `IN_PROGRESS`.
- **Resultado:** Aprovado se o JSON bater.

#### Teste 04 — shouldReturnConflictWhenClosedTicketCannotChangeStatus

- **Cenário:** Service lança `TicketAlreadyClosedException`.
- **Entrada:** Mesmo PATCH `IN_PROGRESS`.
- **Comportamento esperado:** 409 com mensagem de chamado encerrado.
- **Validações (Assertions):**
  - Status 409, `$.code` `TICKET_ALREADY_CLOSED`.
  - `$.message` `Chamado encerrado não pode ter o status alterado.`
- **Resultado:** Aprovado se o payload de conflito bater.

---

## 4. Cenários de exceção e casos de borda

| Cenário | Comportamento esperado |
|---|---|
| Ticket já CLOSED | 409 `TICKET_ALREADY_CLOSED` |
| User que não é suporte | Não coberto aqui (`ForbiddenException` no TicketServiceTest) |

---

## 5. Dependências e mocks

| Dependência | Tipo | Comportamento simulado |
|---|---|---|
| TicketService | Mock | Listagens e updateStatus |
| SecurityContext | Autenticação fake | SUPPORT, USER_ID `66666666-...` |

---

## 6. Cobertura dos requisitos

| Regra de negócio | Teste relacionado | Coberto? |
|---|---|---|
| Listar atribuídos e todos | Testes 01 e 02 | Sim |
| Atualizar status | Teste 03 | Sim |
| Fechado não muda status (HTTP) | Teste 04 | Sim |
| Papel SUPPORT e corridas | TicketServiceTest | Não |

---

## 7. Observações

- Mesmo arranjo de resolver e advices que o controller do cliente.
- Para executar só esta classe, a partir de `backend/`:

```powershell
.\mvnw.cmd -Dtest=SupportTicketControllerTest test
```
