# Documentação de Testes Unitários — CustomerExceptionHandler

## 1. Informações gerais

- **Serviço:** CustomerExceptionHandler (`@RestControllerAdvice` do módulo customer; não é um service de aplicação)
- **Arquivo de implementação:** CustomerExceptionHandler.java
- **Arquivo de testes:** CustomerExceptionHandlerTest.java
- **Framework de testes:** JUnit 5, Mockito, Spring MockMvc (standalone)
- **Dependências simuladas:** CustomerService
- **Responsabilidade do serviço:** Mapear exceções de cadastro e de bean validation para `ApiError` (status HTTP, `code`, `message` e, na validação, `fieldErrors`). A suíte exercita o advice via `POST /customers` (`CustomerController` real + `CustomerService` mockado).

---

## 2. Objetivo dos testes

Validar o contrato HTTP dos erros de cadastro de cliente: quando o serviço já lançou uma exceção de domínio, ou quando a validação de bean rejeita o corpo, a API responde com o status, o código e a mensagem combinados com o cliente.

Estes testes não protegem a regra “só cadastra quem tem CPF/CNPJ pré-cadastrado”. Essa regra fica em `CustomerService` / `CustomerServiceTest`. Aqui o 404 só aparece depois que o mock já lançou `FiscalIdNotFoundException`.

---

## 3. Cenários de testes

### 3.1. handleFiscalIdNotFound

**Método:** handleFiscalIdNotFound()

**Objetivo:**
Garantir que CPF/CNPJ não pré-cadastrado (na visão HTTP: o serviço lançou `FiscalIdNotFoundException`) vira 404 com o payload de erro estável.

#### Teste 01 — shouldReturnNotFoundWhenCpfIsUnknown

- **Cenário:** `processCustomer` lança `FiscalIdNotFoundException`.
- **Entrada:** `POST /customers` com o JSON de `validBody()` (fiscalId `98765432100`, senha `Senha1234`).
- **Comportamento esperado:** O advice converte a exceção em `ApiError` com código `FISCAL_ID_NOT_FOUND`.
- **Validações (Assertions):**
  - Status HTTP 404.
  - `$.code` igual a `FISCAL_ID_NOT_FOUND`.
  - `$.message` igual a `CPF/CNPJ não cadastrado.`
- **Resultado:** Aprovado se as três asserções passarem.

---

### 3.2. handleFiscalIdAlreadyRegistered

**Método:** handleFiscalIdAlreadyRegistered()

**Objetivo:**
Garantir que CPF/CNPJ que já completou o cadastro (já existe customer) vira 409, distinto do 404 de pré-cadastro ausente.

#### Teste 02 — shouldReturnConflictWhenCpfIsAlreadyRegistered

- **Cenário:** `processCustomer` lança `FiscalIdAlreadyRegisteredException`.
- **Entrada:** `POST /customers` com o JSON de `validBody()`.
- **Comportamento esperado:** O advice responde conflito com código `FISCAL_ID_ALREADY_REGISTERED`.
- **Validações (Assertions):**
  - Status HTTP 409.
  - `$.code` igual a `FISCAL_ID_ALREADY_REGISTERED`.
  - `$.message` igual a `Este CPF/CNPJ já possui cadastro.`
- **Resultado:** Aprovado se as três asserções passarem.

---

### 3.3. handleEmailAlreadyInUse

**Método:** handleEmailAlreadyInUse()

**Objetivo:**
Garantir que e-mail já usado por outro usuário vira 409 com código próprio, sem reutilizar o código de CPF/CNPJ.

#### Teste 03 — shouldReturnConflictWhenEmailIsAlreadyInUse

- **Cenário:** `processCustomer` lança `EmailAlreadyInUseException`.
- **Entrada:** `POST /customers` com o JSON de `validBody()`.
- **Comportamento esperado:** O advice responde conflito com código `EMAIL_ALREADY_IN_USE`.
- **Validações (Assertions):**
  - Status HTTP 409.
  - `$.code` igual a `EMAIL_ALREADY_IN_USE`.
  - `$.message` igual a `Este e-mail já está em uso.`
- **Resultado:** Aprovado se as três asserções passarem.

---

### 3.4. handleValidation

**Método:** handleValidation()

**Objetivo:**
Garantir que falha de bean validation (senha em branco) vira 400 com `fieldErrors`, sem depender do serviço de negócio.

#### Teste 04 — shouldReturnValidationErrorForBlankPassword

- **Cenário:** Corpo válido com a senha substituída por string vazia; o mock de `CustomerService` não é stubado.
- **Entrada:** `POST /customers` com `validBody()` após `replace("Senha1234", "")`.
- **Comportamento esperado:** A validação do controller dispara `MethodArgumentNotValidException`; o advice devolve `VALIDATION_ERROR` e o campo `user.password`.
- **Validações (Assertions):**
  - Status HTTP 400.
  - `$.code` igual a `VALIDATION_ERROR`.
  - `$.message` igual a `Verifique os campos informados.`
  - `$['fieldErrors']['user.password']` existe.
- **Resultado:** Aprovado se as quatro asserções passarem.

---

## 4. Cenários de exceção e casos de borda

| Cenário | Comportamento esperado |
|---|---|
| CPF/CNPJ não pré-cadastrado (`FiscalIdNotFoundException`) | 404, `FISCAL_ID_NOT_FOUND`, mensagem `CPF/CNPJ não cadastrado.` |
| CPF/CNPJ de outro papel (SUPPORT) | O serviço lança a mesma `FiscalIdNotFoundException`; no HTTP cai no Teste 01. Esta suíte não distingue o papel. |
| CPF/CNPJ já com linha em `customers` | 409, `FISCAL_ID_ALREADY_REGISTERED` |
| E-mail em uso por outro usuário | 409, `EMAIL_ALREADY_IN_USE` |
| Senha em branco | 400, `VALIDATION_ERROR`, `fieldErrors['user.password']` |

---

## 5. Dependências e mocks

| Dependência | Tipo | Comportamento simulado |
|---|---|---|
| CustomerService | Mock | Nos testes 01–03, `processCustomer` lança a exceção do cenário. No teste 04 não é stubado (a validação interrompe antes). |
| CustomerController | Instância real | Recebe o `POST /customers` no `MockMvc` standalone. |
| CustomerExceptionHandler | Instância real | Registrado via `setControllerAdvice`. |

---

## 6. Cobertura dos requisitos

| Regra de negócio | Teste relacionado | Coberto? |
|---|---|---|
| CPF/CNPJ não pré-cadastrado responde 404 HTTP | Teste 01 — shouldReturnNotFoundWhenCpfIsUnknown | Sim (contrato HTTP; o serviço já lançou a exceção) |
| CPF/CNPJ com cadastro completo responde 409 | Teste 02 — shouldReturnConflictWhenCpfIsAlreadyRegistered | Sim |
| E-mail em uso responde 409 | Teste 03 — shouldReturnConflictWhenEmailIsAlreadyInUse | Sim |
| Senha em branco responde 400 | Teste 04 — shouldReturnValidationErrorForBlankPassword | Sim |
| Lookup de CPF/CNPJ, papel CUSTOMER e persistência do cadastro | CustomerServiceTest | Não |

---

## 7. Observações

- “CPF/CNPJ não cadastrado” no HTTP é falta de pré-cadastro; “Este CPF/CNPJ já possui cadastro” é cadastro já concluído. São estados diferentes.
- Esta suíte não prova que o cadastro exige CPF/CNPJ pré-cadastrado; só traduz a exceção já lançada.
- `shouldReturnValidationErrorForBlankPassword` sobrepõe `CustomerControllerTest.shouldRejectBlankPassword`.
- Papel SUPPORT com CPF/CNPJ existente não tem teste HTTP próprio: o handler trata a mesma `FiscalIdNotFoundException`.
- Para executar só esta classe, a partir de `backend/`:

```powershell
.\mvnw.cmd -Dtest=CustomerExceptionHandlerTest test
```
