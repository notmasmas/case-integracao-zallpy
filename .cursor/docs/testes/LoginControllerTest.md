# Documentação de Testes Unitários — LoginController

## 1. Informações gerais

- **Serviço:** LoginController
- **Arquivo de implementação:** LoginController.java
- **Arquivo de testes:** LoginControllerTest.java
- **Framework de testes:** JUnit 5, Mockito, Spring MockMvc (standalone)
- **Dependências simuladas:** UserRepository, PasswordVerifier, TokenIssuer (`LoginService` é real)
- **Responsabilidade do serviço:** Expor login, sessão atual e logout via HTTP. A suíte usa `CurrentUserArgumentResolver` e `AuthExceptionHandler`.

---

## 2. Objetivo dos testes

Proteger o contrato HTTP de autenticação: login válido devolve token e papel; sessão devolve só o papel; credenciais inválidas viram 401; payload inválido ou JSON quebrado viram 400 sem tocar os ports.

---

## 3. Cenários de testes

### 3.1. login

**Método:** login()

**Objetivo:**
Validar POST `/login` para papéis conhecidos e os erros de credencial e de corpo.

#### Teste 01 — shouldReturnTokenForValidCredentials

- **Cenário:** Parametrizado por `Role`; usuário encontrado, senha confere, issuer devolve `test-token`.
- **Entrada:** POST `/login` com e-mail `cliente@example.com` e senha `senhaTeste`.
- **Comportamento esperado:** 200 com token Bearer e sem o campo `password`.
- **Validações (Assertions):**
  - Status 200.
  - `$.userId`, `$.name` (`Cliente de teste`), `$.role`, `$.accessToken` (`test-token`), `$.tokenType` (`Bearer`).
  - `$.password` não existe.
  - `passwordVerifier.matches` e `tokenIssuer.issue` chamados uma vez.
- **Resultado:** Aprovado se status, JSON e verifies passarem para cada `Role`.

#### Teste 02 — shouldRejectUnknownEmail

- **Cenário:** `findByEmail` vazio.
- **Entrada:** POST `/login` com `missing@example.com`.
- **Comportamento esperado:** 401 `INVALID_CREDENTIALS` sem token.
- **Validações (Assertions):**
  - Status 401, `$.code` `INVALID_CREDENTIALS`, mensagem `E-mail ou senha inválidos.`
  - `$.accessToken` não existe.
  - `verifyNoInteractions(passwordVerifier, tokenIssuer)`.
- **Resultado:** Aprovado se HTTP e verifies passarem.

#### Teste 03 — shouldRejectWrongPassword

- **Cenário:** Usuário existe, senha não confere.
- **Entrada:** POST `/login` com senha `senhaErrada`.
- **Comportamento esperado:** 401 `INVALID_CREDENTIALS` sem emitir token.
- **Validações (Assertions):**
  - Status 401, código e mensagem iguais ao teste 02.
  - `tokenIssuer` sem interações.
- **Resultado:** Aprovado se HTTP e verify passarem.

#### Teste 04 — shouldRejectInvalidFields

- **Cenário:** E-mail inválido e senha vazia.
- **Entrada:** POST `/login` com `"email": "email-invalido"` e `"password": ""`.
- **Comportamento esperado:** 400 de validação, ports não chamados.
- **Validações (Assertions):**
  - Status 400, `$.code` `VALIDATION_ERROR`.
  - `$.fieldErrors.email` e `$.fieldErrors.password` existem.
  - `verifyNoInteractions` nos três ports.
- **Resultado:** Aprovado se HTTP e verifies passarem.

#### Teste 05 — shouldRejectMalformedJson

- **Cenário:** Corpo `{`.
- **Entrada:** POST `/login` com JSON incompleto.
- **Comportamento esperado:** 400 `INVALID_REQUEST_BODY`.
- **Validações (Assertions):**
  - Status 400, `$.code` `INVALID_REQUEST_BODY`.
  - Ports sem interações.
- **Resultado:** Aprovado se HTTP e verifies passarem.

---

### 3.2. session

**Método:** session()

**Objetivo:**
Devolver só o papel do usuário autenticado.

#### Teste 06 — shouldReturnRoleForCurrentSession

- **Cenário:** Parametrizado por `Role`; `SecurityContext` com `AuthenticatedUser`.
- **Entrada:** GET `/session`.
- **Comportamento esperado:** 200 com `role`; sem nome e sem token.
- **Validações (Assertions):**
  - Status 200, `$.role` igual a `role.name()`.
  - `$.name` e `$.accessToken` não existem.
- **Resultado:** Aprovado se as asserções passarem para cada `Role`.

---

## 4. Cenários de exceção e casos de borda

| Cenário | Comportamento esperado |
|---|---|
| E-mail inexistente | 401 `INVALID_CREDENTIALS` |
| Senha incorreta | 401 `INVALID_CREDENTIALS` |
| Campos inválidos | 400 `VALIDATION_ERROR` com fieldErrors |
| JSON malformado | 400 `INVALID_REQUEST_BODY` |
| Logout HTTP | Não coberto nesta suíte |

---

## 5. Dependências e mocks

| Dependência | Tipo | Comportamento simulado |
|---|---|---|
| UserRepository | Mock | `findByEmail` |
| PasswordVerifier | Mock | `matches` |
| TokenIssuer | Mock | `issue` |
| LoginService | Instância real | Orquestra os ports |
| LoginController / AuthExceptionHandler | Instâncias reais | MockMvc standalone |

---

## 6. Cobertura dos requisitos

| Regra de negócio | Teste relacionado | Coberto? |
|---|---|---|
| Login válido devolve token e papel | Teste 01 | Sim |
| Sessão devolve o papel autenticado | Teste 06 | Sim |
| Credenciais inválidas → 401 | Testes 02 e 03 | Sim |
| Validação e JSON inválido → 400 | Testes 04 e 05 | Sim |
| Logout HTTP | — | Não |

---

## 7. Observações

- Diferente de `CustomerControllerTest`, o service é real e os mocks estão nos ports.
- `@AfterEach` limpa o `SecurityContextHolder`.
- Para executar só esta classe, a partir de `backend/`:

```powershell
.\mvnw.cmd -Dtest=LoginControllerTest test
```
