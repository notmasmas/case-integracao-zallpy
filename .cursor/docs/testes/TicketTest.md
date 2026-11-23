# Documentação de Testes Unitários — Ticket

## 1. Informações gerais

- **Serviço:** Ticket (entidade de domínio)
- **Arquivo de implementação:** Ticket.java
- **Arquivo de testes:** TicketTest.java
- **Framework de testes:** JUnit 5
- **Dependências simuladas:** Nenhuma
- **Responsabilidade do serviço:** Representar o chamado de suporte e o estado inicial ao abrir.

---

## 2. Objetivo dos testes

Proteger o construtor de abertura: ticket novo fica `PENDING`, sem categoria, sem suporte, sem avaliação, com `createdAt` em `America/Sao_Paulo` igual a `updatedAt`.

---

## 3. Cenários de testes

### 3.1. construtor

**Método:** Ticket()

**Objetivo:**
Validar o estado imediatamente após `new Ticket(...)`.

#### Teste 01 — shouldOpenTicketAsPendingWithoutEvaluation

- **Cenário:** Construtor com customer, projeto, título e descrição.
- **Entrada:** customerId `22222222-...`, projectId `33333333-...`, título `Painel solar`, descrição `Inversor sem comunicacao`.
- **Comportamento esperado:** Campos de abertura preenchidos; status pendente; avaliação ausente.
- **Validações (Assertions):**
  - Customer, projeto, título e descrição iguais à entrada.
  - `supportId` e `category` nulos.
  - Status `PENDING`.
  - `evaluate`, `evaluateComment` nulos; `isEvaluated()` false.
  - `createdAt` não nulo, zona `America/Sao_Paulo`, mesmo instante que `updatedAt`.
- **Resultado:** Aprovado se todas as asserções passarem.

---

## 4. Cenários de exceção e casos de borda

| Cenário | Comportamento esperado |
|---|---|
| Ticket recém-aberto | PENDING, sem avaliação, sem suporte |

---

## 5. Dependências e mocks

| Dependência | Tipo | Comportamento simulado |
|---|---|---|
| — | — | Sem mocks |

---

## 6. Cobertura dos requisitos

| Regra de negócio | Teste relacionado | Coberto? |
|---|---|---|
| Chamado abre pendente e não avaliado | Teste 01 | Sim |
| Transições de status, avaliação, categoria | TicketServiceTest | Não |

---

## 7. Observações

- Só o estado inicial da entidade; regras de caso de uso estão no serviço.
- Para executar só esta classe, a partir de `backend/`:

```powershell
.\mvnw.cmd -Dtest=TicketTest test
```
