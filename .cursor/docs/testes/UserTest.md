# Documentação de Testes Unitários — User

## 1. Informações gerais

- **Serviço:** User (entidade de domínio em `auth.domain.entity`)
- **Arquivo de implementação:** User.java
- **Arquivo de testes:** UserTest.java
- **Framework de testes:** JUnit 5
- **Dependências simuladas:** Nenhuma
- **Responsabilidade do serviço:** Representar o usuário autenticável e a versão do token usada para invalidar JWT no logout.

---

## 2. Objetivo dos testes

Proteger o estado inicial da entidade e o incremento de versão chamado no logout.

---

## 3. Cenários de testes

### 3.1. estado inicial e incrementTokenVersion

**Método:** incrementTokenVersion()

**Objetivo:**
Garantir versão 0 no construtor padrão e incremento cumulativo.

#### Teste 01 — shouldStartWithTokenVersionZero

- **Cenário:** `new User()` sem setters.
- **Entrada:** Nenhuma.
- **Comportamento esperado:** Versão 0; demais campos nulos.
- **Validações (Assertions):**
  - `getTokenVersion()` igual a 0.
  - `id`, `name`, `email`, `password` e `role` nulos.
- **Resultado:** Aprovado se versão e nulos passarem.

#### Teste 02 — shouldIncrementTokenVersionOnLogout

- **Cenário:** Dois incrementos seguidos.
- **Entrada:** `incrementTokenVersion()` duas vezes.
- **Comportamento esperado:** Versão 2.
- **Validações (Assertions):**
  - `getTokenVersion()` igual a 2.
- **Resultado:** Aprovado se a versão for 2.

---

## 4. Cenários de exceção e casos de borda

| Cenário | Comportamento esperado |
|---|---|
| Usuário recém-construído | Versão 0, campos nulos |
| Dois incrementos | Versão 2 |

---

## 5. Dependências e mocks

| Dependência | Tipo | Comportamento simulado |
|---|---|---|
| — | — | Sem mocks |

---

## 6. Cobertura dos requisitos

| Regra de negócio | Teste relacionado | Coberto? |
|---|---|---|
| Token version inicia em 0 | Teste 01 | Sim |
| Logout incrementa a versão na entidade | Teste 02 | Sim |
| Persistência do incremento | LoginServiceTest | Não |

---

## 7. Observações

- Há outra entidade `User` em `user.domain.entity`; esta suíte cobre a de auth.
- Para executar só esta classe, a partir de `backend/`:

```powershell
.\mvnw.cmd -Dtest=UserTest test
```
