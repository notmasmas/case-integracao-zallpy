# Documentação de Testes Unitários — TicketService

## 1. Informações gerais

- **Serviço:** TicketService
- **Arquivo de implementação:** TicketService.java
- **Arquivo de testes:** TicketServiceTest.java
- **Framework de testes:** JUnit 5, Mockito
- **Dependências simuladas:** TicketRepository, CustomerRepository, MessageRepository, UserRepository, SupportRepository
- **Responsabilidade do serviço:** Criar chamados, listar (cliente, suporte, todos), detalhar com paginação de mensagens, avaliar, alterar status e categoria.

---

## 2. Objetivo dos testes

Proteger as regras de ownership e estado: só customer cria; só suporte lista os seus; detalhe e mensagens paginam; avaliação só em ticket fechável e ainda não avaliado; status não muda se já encerrado; categoria exige ticket existente. Inclui corridas em que `update*` devolve 0.

---

## 3. Cenários de testes

### 3.1. processTicket

**Método:** processTicket()

**Objetivo:**
Criar chamado pendente para customer válido e rejeitar quem não tem registro de cliente.

#### Teste 01 — shouldCreatePendingTicketForCustomer

- **Cenário:** `findByUserId` devolve customer; `save` atribui id do ticket.
- **Entrada:** userId `11111111-...`, `TicketBodyDTO` com projeto `33333333-...`, título `Painel solar`.
- **Comportamento esperado:** Detalhe `PENDING`, sem categoria, sem mensagens, não avaliado.
- **Validações (Assertions):**
  - `id` `44444444-...`, título e descrição da entrada, status `PENDING`.
  - `category` nulo, `supportName` `""`, mensagens vazias, páginas 0, `isEvaluated` false.
- **Resultado:** Aprovado se o DTO de abertura bater.

#### Teste 02 — shouldRejectTicketCreationWhenUserIsNotCustomer

- **Cenário:** `findByUserId` vazio.
- **Entrada:** Mesmo body do teste 01.
- **Comportamento esperado:** `ForbiddenException`.
- **Validações (Assertions):**
  - `assertThrows(ForbiddenException.class, ...)`.
- **Resultado:** Aprovado se a exceção for lançada.

---

### 3.2. listAllTickets

**Método:** listAllTickets()

**Objetivo:**
Listar todos os chamados com nome do atendente quando houver suporte.

#### Teste 03 — shouldListAllTicketsWithSupportName

- **Cenário:** Um ticket com supportId; user do suporte chama-se `Ana Suporte`.
- **Entrada:** Nenhuma além dos stubs de repositório.
- **Comportamento esperado:** Lista de um item com título e nome do suporte.
- **Validações (Assertions):**
  - Tamanho 1, título `Chamado aberto`, `supportName` `Ana Suporte`.
- **Resultado:** Aprovado se título e nome baterem.

---

### 3.3. listCustomerTickets

**Método:** listCustomerTickets()

**Objetivo:**
Listar só os chamados do customer do userId.

#### Teste 04 — shouldListCustomerTickets

- **Cenário:** Customer encontrado; um ticket sem suporte.
- **Entrada:** userId `11111111-...`.
- **Comportamento esperado:** Um summary com título `Meu chamado` e `supportName` vazio.
- **Validações (Assertions):**
  - Tamanho 1, título e `supportName` `""`.
- **Resultado:** Aprovado se a lista bater.

---

### 3.4. listSupportTickets

**Método:** listSupportTickets()

**Objetivo:**
Listar chamados do atendente logado; rejeitar quem não é suporte.

#### Teste 05 — shouldListSupportTickets

- **Cenário:** Support encontrado; ticket atribuído; `findByIdIn` vazio (nome fica `""`).
- **Entrada:** userId `66666666-...`.
- **Comportamento esperado:** Um item `Chamado atribuido`.
- **Validações (Assertions):**
  - Tamanho 1, título `Chamado atribuido`, `supportName` `""`.
- **Resultado:** Aprovado se a lista bater.

#### Teste 06 — shouldRejectSupportTicketListWhenUserIsNotSupport

- **Cenário:** `findByUserId` de suporte vazio.
- **Entrada:** userId `66666666-...`.
- **Comportamento esperado:** `ForbiddenException`.
- **Validações (Assertions):**
  - `assertThrows(ForbiddenException.class, ...)`.
- **Resultado:** Aprovado se a exceção for lançada.

---

### 3.5. getCustomerTicket

**Método:** getCustomerTicket()

**Objetivo:**
Detalhe do chamado do cliente com primeira página de mensagens (mais recentes); 404 se não for do cliente.

#### Teste 07 — shouldReturnCustomerTicketDetailsWithNewestMessagesOnFirstPage

- **Cenário:** Cinco mensagens; página 0.
- **Entrada:** userId, ticketId, page `0`.
- **Comportamento esperado:** Página 0 de 2, 5 mensagens no total, 4 na página, conteúdo `msg-2` … `msg-5`.
- **Validações (Assertions):**
  - Título `Detalhe`, `page` 0, `totalPages` 2, `totalMessages` 5, size 4.
  - Primeiro conteúdo `msg-2`, último `msg-5`, role `CUSTOMER`.
- **Resultado:** Aprovado se paginação e conteúdos baterem.

#### Teste 08 — shouldRejectUnknownCustomerTicket

- **Cenário:** Customer existe; `findByIdAndCustomerId` vazio.
- **Entrada:** page 0.
- **Comportamento esperado:** `TicketNotFoundException`.
- **Validações (Assertions):**
  - `assertThrows(TicketNotFoundException.class, ...)`.
- **Resultado:** Aprovado se a exceção for lançada.

---

### 3.6. listCustomerMessages

**Método:** listCustomerMessages()

**Objetivo:**
Páginas seguintes e página além do fim.

#### Teste 09 — shouldReturnOlderMessagesOnSecondPage

- **Cenário:** Mesmas cinco mensagens; página 1.
- **Entrada:** page `1`.
- **Comportamento esperado:** Uma mensagem, conteúdo `msg-1`.
- **Validações (Assertions):**
  - `page` 1, `totalPages` 2, `totalMessages` 5, size 1, conteúdo `msg-1`.
- **Resultado:** Aprovado se a página 2 bater.

#### Teste 10 — shouldReturnEmptyMessagePageWhenPageIsPastEnd

- **Cenário:** Página 2 com só duas páginas reais.
- **Entrada:** page `2`.
- **Comportamento esperado:** Lista vazia, totais inalterados.
- **Validações (Assertions):**
  - `page` 2, `totalPages` 2, `totalMessages` 5, mensagens vazias.
- **Resultado:** Aprovado se a página vazia bater.

---

### 3.7. evaluateTicket

**Método:** evaluateTicket()

**Objetivo:**
Avaliar ticket encerrado ainda não avaliado; rejeitar aberto, já avaliado e corridas.

#### Teste 11 — shouldEvaluateClosedTicket

- **Cenário:** Status `CLOSED`; `updateEvaluation` retorna 1.
- **Entrada:** nota 5, comentário `Excelente`.
- **Comportamento esperado:** Detalhe avaliado.
- **Validações (Assertions):**
  - `isEvaluated` true, `evaluate` 5, `evaluateComment` `Excelente`.
- **Resultado:** Aprovado se a avaliação persistida aparecer no DTO.

#### Teste 12 — shouldRejectEvaluationWhenTicketIsStillOpen

- **Cenário:** Status `IN_PROGRESS`.
- **Entrada:** nota 4.
- **Comportamento esperado:** `TicketNotEvaluatedException`.
- **Validações (Assertions):**
  - `assertThrows(TicketNotEvaluatedException.class, ...)`.
- **Resultado:** Aprovado se a exceção for lançada.

#### Teste 13 — shouldRejectEvaluationWhenTicketWasAlreadyEvaluated

- **Cenário:** `RESOLVED` já avaliado.
- **Entrada:** nota 3.
- **Comportamento esperado:** `TicketAlreadyEvaluatedException`.
- **Validações (Assertions):**
  - `assertThrows(TicketAlreadyEvaluatedException.class, ...)`.
- **Resultado:** Aprovado se a exceção for lançada.

#### Teste 14 — shouldRejectEvaluationWhenConcurrentUpdateAlreadyEvaluated

- **Cenário:** `updateEvaluation` retorna 0; reload já avaliado.
- **Entrada:** nota 2.
- **Comportamento esperado:** `TicketAlreadyEvaluatedException`.
- **Validações (Assertions):**
  - `assertThrows(TicketAlreadyEvaluatedException.class, ...)`.
- **Resultado:** Aprovado se a exceção for lançada.

#### Teste 15 — shouldRejectEvaluationWhenConcurrentUpdateLeavesTicketNotEvaluable

- **Cenário:** `updateEvaluation` retorna 0; reload `RESOLVED` sem evaluated.
- **Entrada:** nota 1.
- **Comportamento esperado:** `TicketNotEvaluatedException`.
- **Validações (Assertions):**
  - `assertThrows(TicketNotEvaluatedException.class, ...)`.
- **Resultado:** Aprovado se a exceção for lançada.

---

### 3.8. updateStatus

**Método:** updateStatus()

**Objetivo:**
Suporte altera status; recusa ticket fechado e corridas.

#### Teste 16 — shouldUpdateTicketStatusForSupport

- **Cenário:** Support dono do ticket; `updateStatus` retorna 1; novo status `IN_PROGRESS`.
- **Entrada:** userId suporte, `TicketBodyStatusDTO(IN_PROGRESS)`.
- **Comportamento esperado:** DTO com status `IN_PROGRESS`.
- **Validações (Assertions):**
  - `result.status()` igual a `IN_PROGRESS`.
- **Resultado:** Aprovado se o status bater.

#### Teste 17 — shouldRejectStatusUpdateWhenTicketIsAlreadyClosed

- **Cenário:** Ticket já `CLOSED`.
- **Entrada:** tentativa `IN_PROGRESS`.
- **Comportamento esperado:** `TicketAlreadyClosedException`.
- **Validações (Assertions):**
  - `assertThrows(TicketAlreadyClosedException.class, ...)`.
- **Resultado:** Aprovado se a exceção for lançada.

#### Teste 18 — shouldRejectStatusUpdateWhenConcurrentCloseWins

- **Cenário:** `updateStatus` retorna 0; reload `CLOSED`.
- **Entrada:** `IN_PROGRESS`.
- **Comportamento esperado:** `TicketAlreadyClosedException`.
- **Validações (Assertions):**
  - `assertThrows(TicketAlreadyClosedException.class, ...)`.
- **Resultado:** Aprovado se a exceção for lançada.

#### Teste 19 — shouldRejectStatusUpdateWhenConcurrentUpdateFindsNoTicket

- **Cenário:** `updateStatus` retorna 0; reload ainda aberto com outro status.
- **Entrada:** `RESOLVED`.
- **Comportamento esperado:** `TicketNotFoundException`.
- **Validações (Assertions):**
  - `assertThrows(TicketNotFoundException.class, ...)`.
- **Resultado:** Aprovado se a exceção for lançada.

---

### 3.9. updateCategory

**Método:** updateCategory()

**Objetivo:**
Alterar categoria quando o update afeta uma linha; 404 se id some.

#### Teste 20 — shouldUpdateTicketCategory

- **Cenário:** `updateCategory` retorna 1; `findById` devolve ticket `EQUIPMENT`.
- **Entrada:** `TicketBodyCategoryDTO(EQUIPMENT)`.
- **Comportamento esperado:** DTO com categoria `EQUIPMENT`.
- **Validações (Assertions):**
  - `result.category()` igual a `EQUIPMENT`.
- **Resultado:** Aprovado se a categoria bater.

#### Teste 21 — shouldRejectCategoryUpdateWhenTicketDoesNotExist

- **Cenário:** `updateCategory` retorna 0.
- **Entrada:** `PAYMENT`.
- **Comportamento esperado:** `TicketNotFoundException`.
- **Validações (Assertions):**
  - `assertThrows(TicketNotFoundException.class, ...)`.
- **Resultado:** Aprovado se a exceção for lançada.

#### Teste 22 — shouldRejectCategoryUpdateWhenTicketDisappearsAfterUpdate

- **Cenário:** Update retorna 1; `findById` vazio.
- **Entrada:** `ACCESS`.
- **Comportamento esperado:** `TicketNotFoundException`.
- **Validações (Assertions):**
  - `assertThrows(TicketNotFoundException.class, ...)`.
- **Resultado:** Aprovado se a exceção for lançada.

---

## 4. Cenários de exceção e casos de borda

| Cenário | Comportamento esperado |
|---|---|
| User sem customer / sem support | `ForbiddenException` |
| Ticket de outro cliente | `TicketNotFoundException` |
| Página além do fim | Página vazia, totais iguais |
| Avaliar aberto / já avaliado / corrida | `TicketNotEvaluatedException` ou `TicketAlreadyEvaluatedException` |
| Status em ticket fechado / close concorrente | `TicketAlreadyClosedException` |
| Categoria com 0 linhas ou reload vazio | `TicketNotFoundException` |

---

## 5. Dependências e mocks

| Dependência | Tipo | Comportamento simulado |
|---|---|---|
| TicketRepository | Mock | save, find*, updateEvaluation, updateStatus, updateCategory |
| CustomerRepository | Mock | findByUserId |
| MessageRepository | Mock | findByTicketId (cinco mensagens ou vazio) |
| UserRepository | Mock | findByIdIn (nome do suporte / remetente) |
| SupportRepository | Mock | findByUserId, findById |

---

## 6. Cobertura dos requisitos

| Regra de negócio | Teste relacionado | Coberto? |
|---|---|---|
| Customer cria chamado pendente | Testes 01 e 02 | Sim |
| Listagens admin / cliente / suporte | Testes 03–06 | Sim |
| Detalhe e paginação de mensagens | Testes 07–10 | Sim |
| Avaliação e corridas | Testes 11–15 | Sim |
| Status suporte e corridas | Testes 16–19 | Sim |
| Categoria | Testes 20–22 | Sim |
| Contrato HTTP | CustomerTicketControllerTest, SupportTicketControllerTest | Não |

---

## 7. Observações

- Maior suíte do backend (22 testes). Entidades costumam ser reais ou `mock` de `User`.
- `listCustomerMessages` tem testes próprios (09 e 10); a página 0 entra via `getCustomerTicket`.
- Para executar só esta classe, a partir de `backend/`:

```powershell
.\mvnw.cmd -Dtest=TicketServiceTest test
```
