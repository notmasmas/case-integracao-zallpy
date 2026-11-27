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

### 3.2. validateFiscalIdFormat

**Método:** Bean Validation do `UserBodyDTO`

**Objetivo:**
Aceitar o formato de CNPJ de 14 dígitos e rejeitar fiscalIds que não tenham 11 ou 14 dígitos.

#### Teste 02 — shouldAcceptFourteenDigitFiscalId

- **Cenário:** `validBody()` contém fiscalId de 14 dígitos e o serviço lança `FiscalIdNotFoundException`.
- **Entrada:** `POST /customers` com fiscalId `12345678987654`.
- **Comportamento esperado:** A validação aceita o formato de CNPJ e o controller chama o serviço, cuja exceção vira 404.
- **Validações (Assertions):** Status HTTP 404 e código `FISCAL_ID_NOT_FOUND`.

#### Teste 03 — shouldRejectFiscalIdWithTwelveOrThirteenDigits

- **Cenário:** O corpo contém fiscalId com 12 ou 13 dígitos, quantidade que não é CPF nem CNPJ.
- **Entrada:** `POST /customers` com `validBody()` e fiscalId substituído por 12 ou 13 dígitos.
- **Comportamento esperado:** Bean Validation rejeita a requisição antes de chamar o serviço.
- **Validações (Assertions):**
  - Status HTTP 400 e código `VALIDATION_ERROR`.
  - `fieldErrors['user.fiscalId']` igual a `O CPF/CNPJ deve conter 11 ou 14 dígitos numéricos.`
- **Resultado:** Aprovado se as duas quantidades inválidas forem rejeitadas com a mensagem esperada.

### 3.3. handleFiscalIdAlreadyRegistered

**Método:** handleFiscalIdAlreadyRegistered()

**Objetivo:**
Garantir que CPF/CNPJ que já completou o cadastro (já existe customer) vira 409, distinto do 404 de pré-cadastro ausente.

#### Teste 04 — shouldReturnConflictWhenCpfIsAlreadyRegistered

- **Cenário:** `processCustomer` lança `FiscalIdAlreadyRegisteredException`.
- **Entrada:** `POST /customers` com o JSON de `validBody()`.
- **Comportamento esperado:** O advice responde conflito com código `FISCAL_ID_ALREADY_REGISTERED`.
- **Validações (Assertions):**
  - Status HTTP 409.
  - `$.code` igual a `FISCAL_ID_ALREADY_REGISTERED`.
  - `$.message` igual a `Este CPF/CNPJ já possui cadastro.`
- **Resultado:** Aprovado se as três asserções passarem.

---

### 3.4. handleEmailAlreadyInUse

**Método:** handleEmailAlreadyInUse()

**Objetivo:**
Garantir que e-mail já usado por outro usuário vira 409 com código próprio, sem reutilizar o código de CPF/CNPJ.

#### Teste 05 — shouldReturnConflictWhenEmailIsAlreadyInUse

- **Cenário:** `processCustomer` lança `EmailAlreadyInUseException`.
- **Entrada:** `POST /customers` com o JSON de `validBody()`.
- **Comportamento esperado:** O advice responde conflito com código `EMAIL_ALREADY_IN_USE`.
- **Validações (Assertions):**
  - Status HTTP 409.
  - `$.code` igual a `EMAIL_ALREADY_IN_USE`.
  - `$.message` igual a `Este e-mail já está em uso.`
- **Resultado:** Aprovado se as três asserções passarem.

---

### 3.5. handleValidation

**Método:** handleValidation()

**Objetivo:**
Garantir que falha de bean validation (senha em branco) vira 400 com `fieldErrors`, sem depender do serviço de negócio.

#### Teste 06 — shouldReturnValidationErrorForBlankPassword

- **Cenário:** Corpo válido com a senha substituída por string vazia; o mock de `CustomerService` não é stubado.
- **Entrada:** `POST /customers` com `validBody()` após `replace("Senha1234", "")`.
- **Comportamento esperado:** A validação do controller dispara `MethodArgumentNotValidException`; o advice devolve `VALIDATION_ERROR` e o campo `user.password`.
- **Validações (Assertions):**
  - Status HTTP 400.
  - `$.code` igual a `VALIDATION_ERROR`.
  - `$.message` igual a `Verifique os campos informados.`
  - `$['fieldErrors']['user.password']` existe.
- **Resultado:** Aprovado se as quatro asserções passarem.

#### Teste 07 — shouldReturnFieldErrorsWithFriendlyMessagesWithoutTechnicalDetails

- **Cenário:** FiscalId, e-mail e nome inválidos no corpo.
- **Entrada:** `POST /customers` com fiscalId `123`, e-mail `abc` e nome vazio.
- **Comportamento esperado:** HTTP 400 com mensagens amigáveis, sem detalhes técnicos.
- **Validações (Assertions):** Mensagens para `user.fiscalId`, `user.email` e `user.name`; `trace` e `errors` não aparecem.

#### Teste 08 — shouldReturnApiErrorWhenBodyIsNotValidJson

- **Cenário:** O corpo da requisição não é JSON válido.
- **Entrada:** `POST /customers` com `{ isto nao e json`.
- **Comportamento esperado:** HTTP 400 com código `INVALID_REQUEST_BODY`, sem stack trace.

---

## 4. Cenários de exceção e casos de borda

| Cenário | Comportamento esperado |
|---|---|
| CPF/CNPJ não pré-cadastrado (`FiscalIdNotFoundException`) | 404, `FISCAL_ID_NOT_FOUND`, mensagem `CPF/CNPJ não cadastrado.` |
| CPF/CNPJ de outro papel (SUPPORT) | O serviço lança a mesma `FiscalIdNotFoundException`; no HTTP cai no Teste 01. Esta suíte não distingue o papel. |
| CPF/CNPJ já com linha em `customers` | 409, `FISCAL_ID_ALREADY_REGISTERED` |
| E-mail em uso por outro usuário | 409, `EMAIL_ALREADY_IN_USE` |
| FiscalId com 12 ou 13 dígitos | 400, `VALIDATION_ERROR`, mensagem informa 11 ou 14 dígitos |
| Senha em branco | 400, `VALIDATION_ERROR`, `fieldErrors['user.password']` |

---

## 5. Dependências e mocks

| Dependência | Tipo | Comportamento simulado |
|---|---|---|
| CustomerService | Mock | Nos testes 01, 02, 04 e 05, `processCustomer` lança a exceção do cenário. Nos testes de validação, a requisição é rejeitada antes de chamar o serviço. |
| CustomerController | Instância real | Recebe o `POST /customers` no `MockMvc` standalone. |
| CustomerExceptionHandler | Instância real | Registrado via `setControllerAdvice`. |

---

## 6. Cobertura dos requisitos

| Regra de negócio | Teste relacionado | Coberto? |
|---|---|---|
| CPF/CNPJ não pré-cadastrado responde 404 HTTP | Teste 01 — shouldReturnNotFoundWhenCpfIsUnknown | Sim (contrato HTTP; o serviço já lançou a exceção) |
| CNPJ de 14 dígitos passa pela validação de formato | Teste 02 — shouldAcceptFourteenDigitFiscalId | Sim |
| CPF/CNPJ com cadastro completo responde 409 | Teste 04 — shouldReturnConflictWhenCpfIsAlreadyRegistered | Sim |
| E-mail em uso responde 409 | Teste 05 — shouldReturnConflictWhenEmailIsAlreadyInUse | Sim |
| Senha em branco responde 400 | Teste 06 — shouldReturnValidationErrorForBlankPassword | Sim |
| FiscalId de 12 ou 13 dígitos responde 400 | Teste 03 — shouldRejectFiscalIdWithTwelveOrThirteenDigits | Sim |
| Erros de validação não expõem detalhes técnicos | Teste 07 — shouldReturnFieldErrorsWithFriendlyMessagesWithoutTechnicalDetails | Sim |
| JSON inválido responde com erro controlado | Teste 08 — shouldReturnApiErrorWhenBodyIsNotValidJson | Sim |
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
