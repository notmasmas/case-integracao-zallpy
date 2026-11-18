# Documentação de Testes Unitários — JpaUserRepository

## 1. Informações gerais

- **Serviço:** JpaUserRepository (port `UserRepository`)
- **Arquivo de implementação:** JpaUserRepository.java
- **Arquivo de testes:** JpaUserRepositoryIntegrationTest.java
- **Framework de testes:** JUnit 5, Spring Boot Test, JdbcTemplate
- **Dependências simuladas:** PasswordVerifier, TokenIssuer (`@MockitoBean` no contexto)
- **Responsabilidade do serviço:** Implementar o port de usuário via JPA.

---

## 2. Objetivo dos testes

Proteger `findByEmail`: vazio quando o e-mail não existe; entidade completa quando a linha foi inserida via JDBC.

---

## 3. Cenários de testes

### 3.1. findByEmail

**Método:** findByEmail()

**Objetivo:**
Validar ausência e presença no PostgreSQL.

#### Teste 01 — shouldReturnEmptyWhenEmailDoesNotExist

- **Cenário:** E-mail aleatório não inserido.
- **Entrada:** `"missing- " + UUID + "@example.com"`.
- **Comportamento esperado:** `Optional` vazio.
- **Validações (Assertions):**
  - `assertTrue(result.isEmpty())`.
- **Resultado:** Aprovado se o Optional estiver vazio.

#### Teste 02 — shouldFindExistingUserByEmail

- **Cenário:** INSERT via `JdbcTemplate` em `users`; método `@Transactional`.
- **Entrada:** E-mail `it-{uuid}@e.com`, CPF `00000000000`, role CUSTOMER.
- **Comportamento esperado:** Entidade com id, nome, e-mail, hash e role iguais ao insert.
- **Validações (Assertions):**
  - `isPresent()`.
  - `id`, `name` (`Usuário de integração`), `email`, `password` (`hash-placeholder-for-persistence-test`), `role` CUSTOMER.
- **Resultado:** Aprovado se presença e campos baterem.

---

## 4. Cenários de exceção e casos de borda

| Cenário | Comportamento esperado |
|---|---|
| E-mail inexistente | Optional vazio |
| Linha inserida via JDBC | Optional presente com campos do insert |
| `findByCpf` / save | Não cobertos nesta suíte |

---

## 5. Dependências e mocks

| Dependência | Tipo | Comportamento simulado |
|---|---|---|
| UserRepository | Autowired real | Adaptador JPA |
| JdbcTemplate | Autowired real | INSERT conhecido |
| PasswordVerifier | MockitoBean | Contexto Spring, não exercitado |
| TokenIssuer | MockitoBean | Contexto Spring, não exercitado |

---

## 6. Cobertura dos requisitos

| Regra de negócio | Teste relacionado | Coberto? |
|---|---|---|
| E-mail inexistente → vazio | Teste 01 | Sim |
| E-mail existente → entidade | Teste 02 | Sim |
| Lookup por CPF | CustomerService / UserRepository de customer | Não |

---

## 7. Observações

- `@SpringBootTest(properties = "spring.flyway.enabled=false")` ainda exige JDBC/PostgreSQL (`docker compose up` na raiz).
- Para executar só esta classe, a partir de `backend/`:

```powershell
.\mvnw.cmd -Dtest=JpaUserRepositoryIntegrationTest test
```
