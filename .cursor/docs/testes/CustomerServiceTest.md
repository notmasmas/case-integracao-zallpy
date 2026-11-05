# Documentação de Testes Unitários — CustomerService

## 1. Informações gerais

- **Serviço:** CustomerService
- **Arquivo de implementação:** CustomerService.java
- **Arquivo de testes:** CustomerServiceTest.java
- **Framework de testes:** JUnit 5, Mockito
- **Dependências simuladas:** CustomerRepository, AddressRepository, UserRepository, PasswordHasher
- **Responsabilidade do serviço:** Completar o cadastro de um CPF já pré-existente como `User` com papel `CUSTOMER`: validar duplicidade, persistir endereço e customer, atualizar nome/e-mail/senha.

---

## 2. Objetivo dos testes

Proteger a regra de pré-cadastro: só registra se `findByCpf` achar um usuário `CUSTOMER` ainda sem linha em `customers` e com e-mail livre. CPF ausente ou de outro papel viram `CpfNotFoundException`; customer já existente ou e-mail de outro usuário viram as exceções de conflito.

---

## 3. Cenários de testes

### 3.1. processCustomer

**Método:** processCustomer()

**Objetivo:**
Validar persistência no fluxo feliz e as rejeições de CPF, papel, duplicidade e e-mail.

#### Teste 01 — shouldRegisterCustomerWhenCpfExistsAndEmailIsFree

- **Cenário:** User CUSTOMER com CPF `98765432100`, sem customer, e-mail livre.
- **Entrada:** `customerBody()` (nome `Cliente de teste`, e-mail `cliente@email.com`, senha `Senha1234`, mesmo CPF).
- **Comportamento esperado:** Atualiza o user existente, persiste endereço e customer; não cria user do zero.
- **Validações (Assertions):**
  - `result.id()` e `result.userId()` iguais aos UUIDs stubados.
  - Nome, e-mail, senha hash (`hashed-password`) e `addressId` no user.
  - `verify(passwordHasher).hash("Senha1234")`.
- **Resultado:** Aprovado se DTO e estado do user baterem.

#### Teste 02 — shouldRejectUnknownCpf

- **Cenário:** `findByCpf` vazio.
- **Entrada:** `customerBody()`.
- **Comportamento esperado:** `CpfNotFoundException`; não persiste customer.
- **Validações (Assertions):**
  - `assertThrows(CpfNotFoundException.class, ...)`.
  - `verify(customerRepository, never()).save(any())`.
- **Resultado:** Aprovado se a exceção e o never passarem.

#### Teste 03 — shouldRejectCpfThatDoesNotBelongToCustomerRole

- **Cenário:** User com o CPF existe, role `SUPPORT`.
- **Entrada:** `customerBody()`.
- **Comportamento esperado:** Mesma `CpfNotFoundException` do CPF ausente.
- **Validações (Assertions):**
  - `assertThrows(CpfNotFoundException.class, ...)`.
- **Resultado:** Aprovado se a exceção for lançada.

#### Teste 04 — shouldRejectCpfAlreadyRegisteredAsCustomer

- **Cenário:** User CUSTOMER existe e `existsByUserId` é true.
- **Entrada:** `customerBody()`.
- **Comportamento esperado:** `CpfAlreadyRegisteredException`.
- **Validações (Assertions):**
  - `assertThrows(CpfAlreadyRegisteredException.class, ...)`.
- **Resultado:** Aprovado se a exceção for lançada.

#### Teste 05 — shouldRejectEmailAlreadyInUseByAnotherUser

- **Cenário:** CPF pré-cadastrado, ainda sem customer, `existsByEmailAndIdNot` true.
- **Entrada:** `customerBody()`.
- **Comportamento esperado:** `EmailAlreadyInUseException`.
- **Validações (Assertions):**
  - `assertThrows(EmailAlreadyInUseException.class, ...)`.
- **Resultado:** Aprovado se a exceção for lançada.

---

## 4. Cenários de exceção e casos de borda

| Cenário | Comportamento esperado |
|---|---|
| CPF inexistente | `CpfNotFoundException`, sem save de customer |
| CPF de papel SUPPORT | `CpfNotFoundException` |
| Já existe customer para o user | `CpfAlreadyRegisteredException` |
| E-mail de outro usuário | `EmailAlreadyInUseException` |
| CPF CUSTOMER pré-existente e e-mail livre | Persiste endereço, atualiza user, cria customer |

---

## 5. Dependências e mocks

| Dependência | Tipo | Comportamento simulado |
|---|---|---|
| UserRepository | Mock | `findByCpf`, `existsByEmailAndIdNot`, `save` |
| CustomerRepository | Mock | `existsByUserId`, `save` |
| AddressRepository | Mock | `save` atribui id de endereço |
| PasswordHasher | Mock | `hash("Senha1234")` → `hashed-password` |

---

## 6. Cobertura dos requisitos

| Regra de negócio | Teste relacionado | Coberto? |
|---|---|---|
| Só cadastra com CPF pré-cadastrado CUSTOMER | Testes 01, 02 e 03 | Sim |
| Cadastro já concluído | Teste 04 | Sim |
| E-mail em uso | Teste 05 | Sim |
| Contrato HTTP 404/409 | CustomerExceptionHandlerTest | Não |

---

## 7. Observações

- “CPF não cadastrado” = falta de pré-cadastro (ou papel errado). “Já possui cadastro” = linha em `customers`.
- O mapeamento HTTP dessas exceções está em `CustomerExceptionHandlerTest`.
- Para executar só esta classe, a partir de `backend/`:

```powershell
.\mvnw.cmd -Dtest=CustomerServiceTest test
```
