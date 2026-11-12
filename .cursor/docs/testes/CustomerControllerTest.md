# Documentação de Testes Unitários — CustomerController

## 1. Informações gerais

- **Serviço:** CustomerController
- **Arquivo de implementação:** CustomerController.java
- **Arquivo de testes:** CustomerControllerTest.java
- **Framework de testes:** JUnit 5, Mockito, Spring MockMvc (standalone)
- **Dependências simuladas:** CustomerService
- **Responsabilidade do serviço:** `POST /customers` com validação de bean no corpo (incluindo senha). A suíte registra `CustomerExceptionHandler`.

---

## 2. Objetivo dos testes

Proteger o contrato HTTP da senha no cadastro: senhas fora da regra ou em branco viram 400 sem chamar o serviço; senhas válidas viram 201 e invocam `processCustomer`. Não cobre pré-cadastro de CPF.

---

## 3. Cenários de testes

### 3.1. createCustomer

**Método:** createCustomer()

**Objetivo:**
Validar binding e regra de senha no JSON de cadastro.

#### Teste 01 — shouldRejectPasswordOutsideBusinessRule

- **Cenário:** Parametrizado: `1`, `Abc1234`, `12345678`, `abcdefgh`, `Abc12345Abc12345Abc1234567`.
- **Entrada:** POST `/customers` com `bodyWithPassword(password)`.
- **Comportamento esperado:** 400 `VALIDATION_ERROR`; serviço não é chamado.
- **Validações (Assertions):**
  - Status 400, `$.code` `VALIDATION_ERROR`.
  - `$['fieldErrors']['user.password']` existe.
  - `verifyNoInteractions(customerService)`.
- **Resultado:** Aprovado se cada valor parametrizado falhar a validação.

#### Teste 02 — shouldAcceptPasswordWithinBusinessRule

- **Cenário:** Parametrizado: `Senha1234`, `Senha@123`, `Ab1Ab1Ab1Ab1Ab1Ab1Ab1Ab1A`. Service devolve DTO com ids aleatórios.
- **Entrada:** POST `/customers` com senha válida.
- **Comportamento esperado:** 201 com `id` e `userId`; serviço invocado.
- **Validações (Assertions):**
  - Status 201.
  - `$.id` e `$.userId` iguais aos UUIDs stubados.
  - `verify(customerService).processCustomer(...)`.
- **Resultado:** Aprovado se cada senha válida criar o recurso.

#### Teste 03 — shouldRejectBlankPassword

- **Cenário:** Senha vazia.
- **Entrada:** POST `/customers` com `password` `""`.
- **Comportamento esperado:** 400; serviço não é chamado.
- **Validações (Assertions):**
  - Status 400, `VALIDATION_ERROR`, `fieldErrors['user.password']`.
  - `verifyNoInteractions(customerService)`.
- **Resultado:** Aprovado se validação e never passarem.

---

## 4. Cenários de exceção e casos de borda

| Cenário | Comportamento esperado |
|---|---|
| Senha curta, só letras, só números, longa demais | 400, serviço não chamado |
| Senha em branco | 400, serviço não chamado |
| Senha dentro da regra | 201 (serviço mockado devolve sucesso) |
| CPF não pré-cadastrado / e-mail em uso | Não nesta suíte |

---

## 5. Dependências e mocks

| Dependência | Tipo | Comportamento simulado |
|---|---|---|
| CustomerService | Mock | `thenReturn(CustomerDetailsDTO)` só no teste de sucesso |
| CustomerController / CustomerExceptionHandler | Instâncias reais | MockMvc standalone |

---

## 6. Cobertura dos requisitos

| Regra de negócio | Teste relacionado | Coberto? |
|---|---|---|
| Senha fora da regra → 400 | Teste 01 | Sim |
| Senha válida → 201 e chama o serviço | Teste 02 | Sim |
| Senha em branco → 400 | Teste 03 | Sim |
| CPF pré-cadastrado / duplicidade | CustomerServiceTest, CustomerExceptionHandlerTest | Não |

---

## 7. Observações

- O fluxo feliz HTTP não prova pré-cadastro de CPF: o mock sempre “cadastra”.
- Senha em branco também aparece em `CustomerExceptionHandlerTest`.
- Para executar só esta classe, a partir de `backend/`:

```powershell
.\mvnw.cmd -Dtest=CustomerControllerTest test
```
