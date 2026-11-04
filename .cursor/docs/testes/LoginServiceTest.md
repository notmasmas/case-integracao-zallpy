# Documentação de Testes Unitários — LoginService

## 1. Informações gerais

- **Serviço:** LoginService
- **Arquivo de implementação:** LoginService.java
- **Arquivo de testes:** LoginServiceTest.java
- **Framework de testes:** JUnit 5, Mockito
- **Dependências simuladas:** UserRepository, PasswordVerifier, TokenIssuer
- **Responsabilidade do serviço:** Autenticar por e-mail e senha, emitir JWT e invalidar sessão no logout incrementando a versão do token.

---

## 2. Objetivo dos testes

Proteger as regras de credenciais e de logout: token só sai com e-mail conhecido e senha válida; e-mail ausente ou senha errada viram a mesma `InvalidCredentialsException` sem emitir token; logout incrementa a versão e persiste, ou falha se o usuário não existe.

---

## 3. Cenários de testes

### 3.1. login

**Método:** login()

**Objetivo:**
Validar emissão de token e rejeição uniforme de credenciais inválidas.

#### Teste 01 — shouldReturnTokenForValidCredentials

- **Cenário:** Usuário CUSTOMER encontrado, senha confere, emissor devolve `test-token`.
- **Entrada:** e-mail `cliente@example.com`, senha `senhaTeste`.
- **Comportamento esperado:** Retorna `LoginResult` com id, nome, papel e token.
- **Validações (Assertions):**
  - `userId` igual a `11111111-1111-1111-1111-111111111111`.
  - `name` igual a `Cliente de teste`.
  - `role` igual a `CUSTOMER`.
  - `accessToken` igual a `test-token`.
- **Resultado:** Aprovado se as quatro asserções passarem.

#### Teste 02 — shouldRejectUnknownEmail

- **Cenário:** `findByEmail` devolve vazio.
- **Entrada:** e-mail `missing@example.com`, senha `senhaTeste`.
- **Comportamento esperado:** Lança `InvalidCredentialsException` sem verificar senha nem emitir token.
- **Validações (Assertions):**
  - `assertThrows(InvalidCredentialsException.class, ...)`.
  - `verifyNoInteractions(passwordVerifier, tokenIssuer)`.
- **Resultado:** Aprovado se a exceção e o verify passarem.

#### Teste 03 — shouldRejectWrongPassword

- **Cenário:** Usuário existe, `matches` retorna false.
- **Entrada:** e-mail `cliente@example.com`, senha `senhaErrada`.
- **Comportamento esperado:** Lança `InvalidCredentialsException` sem emitir token.
- **Validações (Assertions):**
  - `assertThrows(InvalidCredentialsException.class, ...)`.
  - `verifyNoInteractions(tokenIssuer)`.
- **Resultado:** Aprovado se a exceção e o verify passarem.

---

### 3.2. logout

**Método:** logout()

**Objetivo:**
Invalidar o JWT persistindo o incremento de versão, ou falhar se o id não existe.

#### Teste 04 — shouldIncrementTokenVersionOnLogout

- **Cenário:** `findById` devolve o usuário.
- **Entrada:** UUID `11111111-1111-1111-1111-111111111111`.
- **Comportamento esperado:** Incrementa a versão e salva o usuário.
- **Validações (Assertions):**
  - `verify(user).incrementTokenVersion()`.
  - `verify(userRepository).save(user)`.
- **Resultado:** Aprovado se os dois verifies passarem.

#### Teste 05 — shouldRejectLogoutWhenUserDoesNotExist

- **Cenário:** `findById` devolve vazio.
- **Entrada:** o mesmo UUID do teste 04.
- **Comportamento esperado:** Lança `InvalidTokenException`.
- **Validações (Assertions):**
  - `assertThrows(InvalidTokenException.class, ...)`.
- **Resultado:** Aprovado se a exceção for lançada.

---

## 4. Cenários de exceção e casos de borda

| Cenário | Comportamento esperado |
|---|---|
| E-mail inexistente | `InvalidCredentialsException`; verifier e issuer não são chamados |
| Senha incorreta | `InvalidCredentialsException`; issuer não é chamado |
| Logout com id inexistente | `InvalidTokenException` |

---

## 5. Dependências e mocks

| Dependência | Tipo | Comportamento simulado |
|---|---|---|
| UserRepository | Mock | `findByEmail`, `findById`, `save` |
| PasswordVerifier | Mock | `matches` true/false |
| TokenIssuer | Mock | `issue` devolve `test-token` |
| User | Mock | Getters e `incrementTokenVersion` stubados |

---

## 6. Cobertura dos requisitos

| Regra de negócio | Teste relacionado | Coberto? |
|---|---|---|
| Credenciais válidas devolvem token | Teste 01 | Sim |
| E-mail desconhecido rejeita login | Teste 02 | Sim |
| Senha errada rejeita login | Teste 03 | Sim |
| Logout incrementa versão e persiste | Teste 04 | Sim |
| Logout de usuário inexistente falha | Teste 05 | Sim |
| Contrato HTTP de login/sessão | LoginControllerTest | Não |

---

## 7. Observações

- A entidade `User` é mockada: o incremento real da versão está em `UserTest`.
- E-mail ausente e senha errada usam a mesma exceção, de propósito.
- Para executar só esta classe, a partir de `backend/`:

```powershell
.\mvnw.cmd -Dtest=LoginServiceTest test
```
