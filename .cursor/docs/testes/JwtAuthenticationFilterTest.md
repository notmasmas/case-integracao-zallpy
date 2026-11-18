# Documentação de Testes Unitários — JwtAuthenticationFilter

## 1. Informações gerais

- **Serviço:** JwtAuthenticationFilter
- **Arquivo de implementação:** JwtAuthenticationFilter.java
- **Arquivo de testes:** JwtAuthenticationFilterTest.java
- **Framework de testes:** JUnit 5, Mockito, MockHttpServletRequest/Response
- **Dependências simuladas:** TokenVerifier, AuthenticationEntryPoint, FilterChain
- **Responsabilidade do serviço:** Ler `Authorization: Bearer`, validar o token, preencher o `SecurityContext` ou delegar ao entry point.

---

## 2. Objetivo dos testes

Proteger o encadeamento do filtro: sem Bearer a cadeia segue anônima; Bearer válido autentica com `ROLE_*`; token inválido chama o entry point e não continua a chain.

---

## 3. Cenários de testes

### 3.1. doFilter

**Método:** doFilter()

**Objetivo:**
Validar presença de header, esquema Bearer, sucesso e falha de token.

#### Teste 01 — shouldContinueWhenAuthorizationHeaderIsMissing

- **Cenário:** Request sem `Authorization`.
- **Entrada:** `MockHttpServletRequest` vazio.
- **Comportamento esperado:** Chain continua; verifier não é chamado; sem autenticação.
- **Validações (Assertions):**
  - `verify(filterChain).doFilter(request, response)`.
  - `verify(tokenVerifier, never()).verify(any())`.
  - `SecurityContext` sem authentication.
- **Resultado:** Aprovado se chain, never e contexto nulo passarem.

#### Teste 02 — shouldContinueWhenAuthorizationHeaderIsNotBearer

- **Cenário:** Header `Basic abc`.
- **Entrada:** `Authorization: Basic abc`.
- **Comportamento esperado:** Igual ao teste 01 (segue anônimo).
- **Validações (Assertions):**
  - Chain continua, verifier nunca chamado, authentication nula.
- **Resultado:** Aprovado se as três asserções passarem.

#### Teste 03 — shouldAuthenticateValidBearerToken

- **Cenário:** Verifier devolve `AuthenticatedUser` CUSTOMER.
- **Entrada:** `Authorization: Bearer valid-token`.
- **Comportamento esperado:** Chain continua; principal e authority `ROLE_CUSTOMER`.
- **Validações (Assertions):**
  - Chain `doFilter` chamado.
  - Principal igual ao `AuthenticatedUser`.
  - Authority `ROLE_CUSTOMER`.
- **Resultado:** Aprovado se chain e contexto passarem.

#### Teste 04 — shouldDelegateToEntryPointWhenTokenIsInvalid

- **Cenário:** Verifier lança `InvalidTokenException`.
- **Entrada:** `Authorization: Bearer bad-token`.
- **Comportamento esperado:** Entry point com `BadCredentialsException`; chain não segue; contexto vazio.
- **Validações (Assertions):**
  - `verify(filterChain, never()).doFilter(...)`.
  - `authenticationEntryPoint.commence` com `BadCredentialsException`.
  - Authentication nula.
- **Resultado:** Aprovado se as três asserções passarem.

---

## 4. Cenários de exceção e casos de borda

| Cenário | Comportamento esperado |
|---|---|
| Sem header / não-Bearer | Chain continua, sem autenticação |
| Bearer válido | Principal e `ROLE_*` no contexto |
| Token inválido | Entry point; chain não continua |

---

## 5. Dependências e mocks

| Dependência | Tipo | Comportamento simulado |
|---|---|---|
| TokenVerifier | Mock | `verify` devolve usuário ou lança `InvalidTokenException` |
| AuthenticationEntryPoint | Mock | Recebe commence no token inválido |
| FilterChain | Mock | Deve ou não continuar |

---

## 6. Cobertura dos requisitos

| Regra de negócio | Teste relacionado | Coberto? |
|---|---|---|
| Request sem Bearer segue anônimo | Testes 01 e 02 | Sim |
| Bearer válido autentica | Teste 03 | Sim |
| Token inválido vai ao entry point | Teste 04 | Sim |
| Parse/assinatura do JWT | JwtTokenVerifierTest | Não |

---

## 7. Observações

- Sem Spring MVC: request/response mock do Spring Test.
- `@AfterEach` limpa o `SecurityContextHolder`.
- Para executar só esta classe, a partir de `backend/`:

```powershell
.\mvnw.cmd -Dtest=JwtAuthenticationFilterTest test
```
