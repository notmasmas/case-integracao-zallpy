# Documentação de Testes Unitários — MessageController

## 1. Informações gerais

- **Serviço:** MessageController
- **Arquivo de implementação:** MessageController.java
- **Arquivo de testes:** MessageControllerTest.java
- **Framework de testes:** JUnit 5, Mockito, Spring MockMvc (standalone)
- **Dependências simuladas:** MessageService, TicketService
- **Responsabilidade do serviço:** GET/POST `/tickets/{ticketId}/messages`. Lista via `TicketService`; criação via `MessageService`.

---

## 2. Objetivo dos testes

Proteger o contrato HTTP: listar 200, criar 201 com Location; ticket inexistente → 404; ticket que não aceita mensagem → 409 `TICKET_CLOSED`.

---

## 3. Cenários de testes

### 3.1. listMessages

**Método:** listMessages()

**Objetivo:**
Listar página de mensagens e traduzir ticket inexistente.

#### Teste 01 — shouldListCustomerMessages

- **Cenário:** `listCustomerMessages` devolve página vazia.
- **Entrada:** GET `/tickets/{ticketId}/messages`.
- **Comportamento esperado:** 200 com page 0 e total 0.
- **Validações (Assertions):**
  - `$.page` 0, `$.totalMessages` 0.
- **Resultado:** Aprovado se o JSON bater.

#### Teste 02 — shouldReturnNotFoundWhenTicketDoesNotExist

- **Cenário:** Service lança `TicketNotFoundException`.
- **Entrada:** GET da lista.
- **Comportamento esperado:** 404 `TICKET_NOT_FOUND`.
- **Validações (Assertions):**
  - Status 404, `$.code` `TICKET_NOT_FOUND`.
- **Resultado:** Aprovado se o 404 bater.

---

### 3.2. createMessage

**Método:** createMessage()

**Objetivo:**
Criar mensagem e traduzir chamado encerrado.

#### Teste 03 — shouldCreateMessage

- **Cenário:** `processMessage` devolve DTO CUSTOMER.
- **Entrada:** POST `{ "content": "Inversor voltou a comunicar" }`.
- **Comportamento esperado:** 201 e Location da mensagem.
- **Validações (Assertions):**
  - Status 201.
  - Location `http://localhost/tickets/{ticketId}/messages/{messageId}`.
  - `$.id`, `$.content`, `$.role` `CUSTOMER`.
- **Resultado:** Aprovado se header e JSON passarem.

#### Teste 04 — shouldReturnConflictWhenTicketIsClosed

- **Cenário:** Service lança `TicketClosedException`.
- **Entrada:** POST `{ "content": "Oi" }`.
- **Comportamento esperado:** 409 `TICKET_CLOSED`.
- **Validações (Assertions):**
  - Status 409, `$.code` `TICKET_CLOSED`.
  - Mensagem `Este chamado não aceita novas mensagens.`
- **Resultado:** Aprovado se o conflito HTTP bater.

---

## 4. Cenários de exceção e casos de borda

| Cenário | Comportamento esperado |
|---|---|
| Ticket inexistente na listagem | 404 `TICKET_NOT_FOUND` |
| Ticket fechado na criação | 409 `TICKET_CLOSED` |
| Forbidden / ownership | Não nesta suíte |

---

## 5. Dependências e mocks

| Dependência | Tipo | Comportamento simulado |
|---|---|---|
| TicketService | Mock | `listCustomerMessages` (sucesso ou 404) |
| MessageService | Mock | `processMessage` (sucesso ou 409) |
| SecurityContext | Autenticação fake | CUSTOMER, USER_ID fixo |

---

## 6. Cobertura dos requisitos

| Regra de negócio | Teste relacionado | Coberto? |
|---|---|---|
| Listar mensagens | Teste 01 | Sim |
| Criar mensagem | Teste 03 | Sim |
| 404 / 409 HTTP | Testes 02 e 04 | Sim |
| Ownership e status no domínio | MessageServiceTest | Não |

---

## 7. Observações

- GET usa `TicketService`; POST usa `MessageService`.
- Para executar só esta classe, a partir de `backend/`:

```powershell
.\mvnw.cmd -Dtest=MessageControllerTest test
```
