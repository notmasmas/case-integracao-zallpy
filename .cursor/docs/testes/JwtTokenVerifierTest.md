# Documentação de Testes Unitários — JwtTokenVerifier

## 1. Informações gerais

- **Serviço:** JwtTokenVerifier
- **Arquivo de implementação:** JwtTokenVerifier.java
- **Arquivo de testes:** JwtTokenVerifierTest.java
- **Framework de testes:** JUnit 5, Mockito
- **Dependências simuladas:** UserRepository (`JwtTokenIssuer` real com segredo fixo)
- **Responsabilidade do serviço:** Validar JWT (formato, assinatura, expiração, claim de versão) e reconciliar com o usuário persistido.

---

## 2. Objetivo dos testes

Proteger a aceitação do token: só autentica quando formato, assinatura, expiração e versão batem com o usuário no repositório; falha de acesso a dados se propaga; demais falhas viram `InvalidTokenException`.

---

## 3. Cenários de testes

### 3.1. verify

**Método:** verify()

**Objetivo:**
Cobrir o caminho feliz e as rejeições de token e de lookup.

#### Teste 01 — shouldRethrowDataAccessFailureWhenLookingUpUser

- **Cenário:** Token válido; `findById` lança `DataAccessResourceFailureException`.
- **Entrada:** JWT emitido para um UUID aleatório, role CUSTOMER, versão 1.
- **Comportamento esperado:** Propaga a mesma `DataAccessException`.
- **Validações (Assertions):**
  - `assertThrows(DataAccessException.class, ...)`.
  - `assertSame(failure, thrown)`.
- **Resultado:** Aprovado se a mesma instância for relançada.

#### Teste 02 — shouldRejectTokenWhenUserDoesNotExist

- **Cenário:** Token válido, repositório vazio.
- **Entrada:** JWT CUSTOMER versão 0.
- **Comportamento esperado:** `InvalidTokenException`.
- **Validações (Assertions):**
  - `assertThrows(InvalidTokenException.class, ...)`.
- **Resultado:** Aprovado se a exceção for lançada.

#### Teste 03 — shouldRejectTokenWhenVersionDoesNotMatch

- **Cenário:** JWT versão 2, usuário persistido versão 1.
- **Entrada:** Token SUPPORT/CUSTOMER emitido com versão 2.
- **Comportamento esperado:** `InvalidTokenException`.
- **Validações (Assertions):**
  - `assertThrows(InvalidTokenException.class, ...)`.
- **Resultado:** Aprovado se a exceção for lançada.

#### Teste 04 — shouldRejectMalformedToken

- **Cenário:** String `a.b.c` não é JWT assinado válido.
- **Entrada:** `"a.b.c"`.
- **Comportamento esperado:** `InvalidTokenException`.
- **Validações (Assertions):**
  - `assertThrows(InvalidTokenException.class, ...)`.
- **Resultado:** Aprovado se a exceção for lançada.

#### Teste 05 — shouldReturnAuthenticatedUserWhenTokenAndVersionMatch

- **Cenário:** Token SUPPORT versão 3; usuário com versão 3.
- **Entrada:** JWT para `11111111-1111-1111-1111-111111111111`.
- **Comportamento esperado:** `AuthenticatedUser` com id e role do token.
- **Validações (Assertions):**
  - `userId` igual ao UUID fixo.
  - `role` igual a `SUPPORT`.
- **Resultado:** Aprovado se id e role baterem.

#### Teste 06 — shouldRejectExpiredToken

- **Cenário:** Emissor com TTL 0.
- **Entrada:** Token recém-emitido já expirado.
- **Comportamento esperado:** `InvalidTokenException`.
- **Validações (Assertions):**
  - `assertThrows(InvalidTokenException.class, ...)`.
- **Resultado:** Aprovado se a exceção for lançada.

#### Teste 07 — shouldRejectTokenWithInvalidSignature

- **Cenário:** Últimos dois caracteres da assinatura trocados por `aa`.
- **Entrada:** Token adulterado.
- **Comportamento esperado:** `InvalidTokenException`.
- **Validações (Assertions):**
  - `assertThrows(InvalidTokenException.class, ...)`.
- **Resultado:** Aprovado se a exceção for lançada.

#### Teste 08 — shouldRejectTokenWithoutThreeParts

- **Cenário:** String `only-two.parts`.
- **Entrada:** Token com dois segmentos.
- **Comportamento esperado:** `InvalidTokenException`.
- **Validações (Assertions):**
  - `assertThrows(InvalidTokenException.class, ...)`.
- **Resultado:** Aprovado se a exceção for lançada.

#### Teste 09 — shouldRejectTokenWithoutVersionClaim

- **Cenário:** Payload sem claim de versão, assinatura reaproveitada de outro token.
- **Entrada:** JWT montado à mão.
- **Comportamento esperado:** `InvalidTokenException`.
- **Validações (Assertions):**
  - `assertThrows(InvalidTokenException.class, ...)`.
- **Resultado:** Aprovado se a exceção for lançada.

---

## 4. Cenários de exceção e casos de borda

| Cenário | Comportamento esperado |
|---|---|
| Falha de JDBC/DataAccess | Propaga `DataAccessException` |
| Usuário ausente, versão divergente, formato, expiração, assinatura, claim | `InvalidTokenException` |
| Falha do Mac/HMAC na JVM | Não coberto |

---

## 5. Dependências e mocks

| Dependência | Tipo | Comportamento simulado |
|---|---|---|
| UserRepository | Mock | `findById` vazio, usuário, ou DataAccessException |
| JwtTokenIssuer | Instância real | Segredo `0123456789abcdef0123456789abcdef`, TTL 60 s (0 no teste expirado) |
| ObjectMapper | Instância real | Parse do payload |

---

## 6. Cobertura dos requisitos

| Regra de negócio | Teste relacionado | Coberto? |
|---|---|---|
| Token e versão válidos autenticam | Teste 05 | Sim |
| Usuário inexistente / versão divergente | Testes 02 e 03 | Sim |
| Token malformado, expirado, assinatura, partes, claim | Testes 04, 06–09 | Sim |
| Falha de acesso a dados propaga | Teste 01 | Sim |
| Falha do provedor HMAC da JVM | — | Não |

---

## 7. Observações

- Tokens válidos são gerados pelo mesmo segredo do verifier.
- O filtro HTTP que consome `verify` está em `JwtAuthenticationFilterTest`.
- Para executar só esta classe, a partir de `backend/`:

```powershell
.\mvnw.cmd -Dtest=JwtTokenVerifierTest test
```
