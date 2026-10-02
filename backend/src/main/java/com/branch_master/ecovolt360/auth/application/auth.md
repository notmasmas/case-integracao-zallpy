# Como o auth é usado pelos outros módulos

A role não é buscada no banco a cada requisição. Ela entra no token no login e o Spring Security lê de volta nas rotas protegidas.

Outro módulo não valida JWT, não lê o header `Authorization` e não consulta a tabela `users` para descobrir o perfil. A `SecurityFilterChain` exige o perfil pelo path. O controller recebe o usuário já autenticado e, se precisar, converte o `userId` do token no id da própria tabela (`customers.id`, `supports.id`).

## O que o token carrega

No login, o `LoginService` chama `tokenIssuer.issue(user.getId(), user.getRole())`. O `JwtTokenIssuer` monta um JWT em três partes separadas por ponto: cabeçalho, payload e assinatura.

O payload é um JSON com quatro campos:

| Campo | Conteúdo |
| --- | --- |
| `sub` | id do usuário (`users.id`) |
| `role` | `CUSTOMER` ou `SUPPORT` |
| `iat` | instante em que o token foi emitido |
| `exp` | instante em que o token expira |

Esse JSON é só codificado em Base64. A terceira parte é uma assinatura HMAC-SHA256 do cabeçalho e do payload, feita com o segredo `app.jwt.secret`. Sem o segredo, alterar a role invalida a assinatura.

## O que acontece antes do controller

A `SecurityConfiguration` monta uma `SecurityFilterChain` sem sessão e sem CSRF. O `JwtAuthenticationFilter` roda antes do filtro de login do Spring Security.

Para uma rota protegida, a ordem é esta:

1. O filtro lê `Authorization: Bearer ...` e passa só o token ao `JwtTokenVerifier`.
2. O verifier separa as três partes, recalcula a assinatura e compara com `MessageDigest.isEqual`. Assinatura errada ou `exp` vencido vira 401 `UNAUTHORIZED`.
3. Ele monta um `AuthenticatedUser` com `UUID.fromString(sub)` e `Role.valueOf(role)`.
4. O filtro grava esse usuário no `SecurityContext`, com a authority `ROLE_CUSTOMER` ou `ROLE_SUPPORT`.
5. A cadeia exige `CUSTOMER` em `/tickets/**` e `SUPPORT` em `/support/**`. Perfil diferente vira 403 `FORBIDDEN`.
6. O `CurrentUserArgumentResolver` lê o principal do `SecurityContext` e entrega o `AuthenticatedUser` no parâmetro anotado com `@CurrentUser`.

`POST /login` e `POST /customers` são públicos. Qualquer outro path é recusado.

O `AccessExceptionHandler` continua tratando `ForbiddenException` lançada pelo service (usuário autenticado sem cadastro de customer), também como 403 `FORBIDDEN`.

## Contrato que um módulo usa

| Peça | Onde | Uso no módulo |
| --- | --- | --- |
| Path na `SecurityConfiguration` | `/tickets/**` ou `/support/**` | perfil exigido pela rota |
| `@CurrentUser AuthenticatedUser` | parâmetro do método | usuário logado; o módulo usa `userId()` |
| `ForbiddenException` | service | usuário autenticado sem o cadastro daquele perfil |

O `userId()` é `users.id`. A foreign key de ticket aponta para `customers.id` ou `supports.id`, então o service do módulo faz essa conversão. O service não lê a role: a cadeia já barrou o perfil errado.

Uma rota nova só fica protegida se o path estiver na `SecurityConfiguration`. Fora de `POST /login`, `POST /customers`, `/tickets` e `/support`, a resposta é 403.

## Ticket

`/tickets/**` exige `CUSTOMER`. O controller só recebe o usuário:

```java
@RequestMapping("/tickets")
public class TicketController {
    public ResponseEntity<TicketDetailsDTO> createTicket(
            @RequestBody @Valid TicketBodyDTO ticket,
            @CurrentUser AuthenticatedUser currentUser,
            UriComponentsBuilder uriBuilder) {
        ticketService.processTicket(currentUser.userId(), ticket);
    }
}
```

O `GET /tickets/{id}` faz o mesmo: passa `currentUser.userId()` para `getCustomerTicket`.

O `TicketService` recebe esse `userId` e chama `customerRepository.findByUserId`. Sem registro em `customers`, lança `ForbiddenException` (403). Com o `customer.id`:

- `processTicket` grava o chamado com esse `customerId`.
- `getCustomerTicket` busca com `findByIdAndCustomerId`. Chamado de outro cliente, ou inexistente, vira `TicketNotFoundException` (404 `TICKET_NOT_FOUND`), tratado pelo `TicketExceptionHandler`.

| Situação | Resposta |
| --- | --- |
| Sem token, token adulterado ou expirado | 401 `UNAUTHORIZED` |
| Token de `SUPPORT` em `/tickets` | 403 `FORBIDDEN` |
| Token de `CUSTOMER` sem linha em `customers` | 403 `FORBIDDEN` |
| Chamado de outro cliente | 404 `TICKET_NOT_FOUND` |

## Support

`Support`, `SupportRepository`, `SpringDataSupportRepository` e `JpaSupportRepository` já buscam o suporte por `userId`, no mesmo formato do customer. Ainda não há controller em `/support`. Quando existir, `/support/**` já exige `SUPPORT`. A conversão de `userId` para `supports.id` fica só na listagem "meus chamados".

## O que não usa auth

`LoginController` (`POST /login`) emite o token e não o consome. `CustomerController` (`/customers`) é cadastro público.
