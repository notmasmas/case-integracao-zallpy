# Documentação de Testes Unitários — MessageService

## 1. Informações gerais

- **Serviço:** MessageService
- **Arquivo de implementação:** MessageService.java
- **Arquivo de testes:** MessageServiceTest.java
- **Framework de testes:** JUnit 5, Mockito
- **Dependências simuladas:** MessageRepository, TicketRepository, CustomerRepository
- **Responsabilidade do serviço:** Criar mensagem em chamado do cliente, validando ownership e se o status ainda aceita mensagens.

---

## 2. Objetivo dos testes

Proteger: só customer dono do ticket aberto persiste mensagem; sem customer → `ForbiddenException`; ticket de outro → `TicketNotFoundException`; `RESOLVED`/`CLOSED` → `TicketClosedException` sem save.

---

## 3. Cenários de testes

### 3.1. processMessage

**Método:** processMessage()

**Objetivo:**
Persistir mensagem válida e rejeitar papel, ownership e status.

#### Teste 01 — shouldCreateMessageForOpenCustomerTicket

- **Cenário:** Customer e ticket PENDING encontrados; save atribui messageId.
- **Entrada:** userId `11111111-...`, role CUSTOMER, ticketId `44444444-...`, conteúdo `Inversor voltou a comunicar`.
- **Comportamento esperado:** DTO com id, remetente, role e conteúdo.
- **Validações (Assertions):**
  - `id` `77777777-...`, `senderUserId` o userId, role `CUSTOMER`, content da entrada.
- **Resultado:** Aprovado se o DTO bater.

#### Teste 02 — shouldRejectMessageWhenUserIsNotCustomer

- **Cenário:** `findByUserId` vazio.
- **Entrada:** conteúdo `Oi`.
- **Comportamento esperado:** `ForbiddenException`; não salva.
- **Validações (Assertions):**
  - `assertThrows(ForbiddenException.class, ...)`.
  - `verify(messageRepository, never()).save(any())`.
- **Resultado:** Aprovado se exceção e never passarem.

#### Teste 03 — shouldRejectMessageWhenTicketDoesNotBelongToCustomer

- **Cenário:** Customer existe; `findByIdAndCustomerId` vazio.
- **Entrada:** conteúdo `Oi`.
- **Comportamento esperado:** `TicketNotFoundException`.
- **Validações (Assertions):**
  - `assertThrows(TicketNotFoundException.class, ...)`.
- **Resultado:** Aprovado se a exceção for lançada.

#### Teste 04 — shouldRejectMessageWhenTicketDoesNotAcceptNewMessages

- **Cenário:** Parametrizado `RESOLVED` e `CLOSED`.
- **Entrada:** Ticket do cliente com esse status, conteúdo `Oi`.
- **Comportamento esperado:** `TicketClosedException`; não salva.
- **Validações (Assertions):**
  - `assertThrows(TicketClosedException.class, ...)`.
  - `never().save`.
- **Resultado:** Aprovado se cada status parametrizado rejeitar.

---

## 4. Cenários de exceção e casos de borda

| Cenário | Comportamento esperado |
|---|---|
| Sem registro de customer | `ForbiddenException` |
| Ticket de outro cliente | `TicketNotFoundException` |
| Status RESOLVED ou CLOSED | `TicketClosedException`, sem persistir |
| Ticket aberto do dono | Mensagem salva |

---

## 5. Dependências e mocks

| Dependência | Tipo | Comportamento simulado |
|---|---|---|
| CustomerRepository | Mock | findByUserId |
| TicketRepository | Mock | findByIdAndCustomerId |
| MessageRepository | Mock | save ou never |

---

## 6. Cobertura dos requisitos

| Regra de negócio | Teste relacionado | Coberto? |
|---|---|---|
| Mensagem em chamado aberto do cliente | Teste 01 | Sim |
| Não-customer / ticket alheio | Testes 02 e 03 | Sim |
| Chamado encerrado não aceita mensagem | Teste 04 | Sim |
| Contrato HTTP | MessageControllerTest | Não |

---

## 7. Observações

- O teste 02 passa `Role.CUSTOMER` mas o repositório não acha customer — cobre ausência de linha, não role SUPPORT.
- Para executar só esta classe, a partir de `backend/`:

```powershell
.\mvnw.cmd -Dtest=MessageServiceTest test
```
