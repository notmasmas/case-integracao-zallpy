# Documentação de Testes Unitários — Ecovolt360Application

## 1. Informações gerais

- **Serviço:** Ecovolt360Application (smoke do contexto Spring)
- **Arquivo de implementação:** Ecovolt360Application.java
- **Arquivo de testes:** Ecovolt360ApplicationTests.java
- **Framework de testes:** JUnit 5, Spring Boot Test
- **Dependências simuladas:** PasswordVerifier, TokenIssuer
- **Responsabilidade do serviço:** Subir a aplicação Spring Boot. O teste só verifica se o contexto carrega.

---

## 2. Objetivo dos testes

Garantir que o contexto da aplicação sobe com Flyway desligado e ports de auth mockados no contexto.

---

## 3. Cenários de testes

### 3.1. contextLoads

**Método:** contextLoads()

**Objetivo:**
Falhar na inicialização se o contexto não subir.

#### Teste 01 — contextLoads

- **Cenário:** `@SpringBootTest(properties = "spring.flyway.enabled=false")`.
- **Entrada:** Nenhuma (método vazio).
- **Comportamento esperado:** Contexto carrega sem exceção.
- **Validações (Assertions):**
  - Nenhuma asserção explícita; falha se o contexto não inicializar.
- **Resultado:** Aprovado se o teste terminar sem erro de startup.

---

## 4. Cenários de exceção e casos de borda

| Cenário | Comportamento esperado |
|---|---|
| Contexto inválido / bean quebrado | Falha na subida, antes do método |
| Banco indisponível | Costuma falhar o DataSource mesmo com Flyway off |

---

## 5. Dependências e mocks

| Dependência | Tipo | Comportamento simulado |
|---|---|---|
| PasswordVerifier | MockitoBean | Evita implementação real de senha |
| TokenIssuer | MockitoBean | Evita emissão real de JWT |

---

## 6. Cobertura dos requisitos

| Regra de negócio | Teste relacionado | Coberto? |
|---|---|---|
| Aplicação sobe o contexto Spring | Teste 01 | Sim |
| Regras de domínio (login, cadastro, tickets) | Suítes de cada módulo | Não |

---

## 7. Observações

- PostgreSQL precisa estar acessível (`docker compose up` na raiz).
- Para executar só esta classe, a partir de `backend/`:

```powershell
.\mvnw.cmd -Dtest=Ecovolt360ApplicationTests test
```
