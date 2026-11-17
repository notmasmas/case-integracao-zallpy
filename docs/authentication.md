# Autenticação

## Visão geral

O backend (Spring Boot, pacote `com.branch_master.ecovolt360`) autentica usuários por **e-mail e senha** e emite um **JWT assinado com HMAC-SHA256 (HS256)**, implementado manualmente (sem biblioteca JWT). As requisições seguintes enviam o token no header `Authorization: Bearer <token>`.

Características confirmadas no código:

- Aplicação **stateless**: sem sessão HTTP, CSRF desabilitado e logout padrão do Spring Security desabilitado (`SecurityConfiguration`).
- Dois perfis (`Role`): `CUSTOMER` e `SUPPORT`.
- O perfil é gravado no token e usado pela `SecurityFilterChain` para autorizar por path/método.
- A cada requisição autenticada, o `JwtTokenVerifier` **consulta o banco** (`users`) para comparar a `token_version` do token com a do usuário. É isso que permite invalidar tokens no logout.
- Senhas são verificadas com PBKDF2 (`Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8()`).

> **Divergência encontrada:** o arquivo `backend/src/main/java/com/branch_master/ecovolt360/auth/application/auth.md` está desatualizado em relação ao código. Ele afirma que o token tem quatro campos (sem `ver`), que só `POST /login` e `POST /customers` são públicos e que não há consulta ao banco por requisição. Este documento segue o **código**.

**Arquivos de suporte:** `auth/presentation/controller/LoginController.java`, `auth/application/service/LoginService.java`, `auth/infrastructure/config/SecurityConfiguration.java`, `auth/infrastructure/security/*`.

## Endpoints

Todos os endpoints de autenticação estão em `LoginController`.

| Método | Path | Acesso | Descrição |
| --- | --- | --- | --- |
| `POST` | `/login` | Público | Autentica com e-mail e senha e devolve o token. |
| `GET` | `/session` | Autenticado (qualquer perfil) | Devolve o perfil do usuário do token. |
| `POST` | `/logout` | Autenticado (qualquer perfil) | Invalida os tokens do usuário. Responde `204 No Content`. |

### `POST /login`

Requisição (`LoginRequest`):

```json
{ "email": "usuario@exemplo.com", "password": "senha" }
```

Validações: `email` obrigatório e em formato de e-mail; `password` obrigatório (`@NotBlank`).

Resposta `200` (`LoginResponse`):

```json
{
  "userId": "uuid",
  "name": "Nome do usuário",
  "role": "CUSTOMER",
  "accessToken": "<jwt>",
  "tokenType": "Bearer"
}
```

A senha nunca é devolvida (teste `LoginControllerTest.shouldReturnTokenForValidCredentials` verifica `$.password` inexistente).

### `GET /session`

Resposta `200` (`SessionResponse`): `{ "role": "CUSTOMER" | "SUPPORT" }`. Não devolve nome nem token.

### `POST /logout`

Sem corpo. Incrementa `token_version` do usuário, o que invalida todos os tokens emitidos anteriormente para ele (ver [Fluxo de autenticação](#fluxo-de-autenticação)).

### Regras de autorização dos demais endpoints

Definidas em `SecurityConfiguration` (avaliadas na ordem em que aparecem):

| Regra | Acesso |
| --- | --- |
| `POST /login`, `POST /customers`, `GET /faqs` | Público |
| `/swagger-ui.html`, `/swagger-ui/**`, `/v3/api-docs/**` | Público |
| `PATCH /support/tickets/{ticketId}/status` | `SUPPORT` |
| `PATCH /tickets/{ticketId}/evaluation` | `CUSTOMER` |
| `/tickets`, `/tickets/**` | `CUSTOMER` |
| `/support`, `/support/**` | `SUPPORT` |
| `GET /session`, `POST /logout` | Autenticado |
| `GET /projects` | `CUSTOMER` |
| Qualquer outra requisição | Negada (`denyAll`) |

Consequência: uma rota nova só funciona se for declarada na `SecurityConfiguration`; caso contrário é negada (`denyAll`): `403` para usuário autenticado e, para requisição sem token, o comportamento esperado do Spring Security é `401` (não verificado por teste).

**Arquivos:** `auth/presentation/controller/LoginController.java`, `auth/presentation/dto/LoginRequest.java`, `LoginResponse.java`, `SessionResponse.java`, `auth/infrastructure/config/SecurityConfiguration.java`.
**Testes:** `LoginControllerTest`, `SupportTicketSecurityTest`.

## Fluxo de autenticação

### 1. Login

1. `LoginController.login` valida o corpo e chama `LoginService.login(email, password)`.
2. `LoginService` busca o usuário por e-mail (`UserRepository.findByEmail`). Se não existir, lança `InvalidCredentialsException`.
3. Compara a senha com o hash armazenado via `PasswordVerifier` (`Pbkdf2PasswordVerifier`). Se não conferir, lança a **mesma** `InvalidCredentialsException` (a resposta não distingue e-mail inexistente de senha errada).
4. Chama `TokenIssuer.issue(userId, role, tokenVersion)` (`JwtTokenIssuer`) e devolve `LoginResult`, convertido em `LoginResponse` com `tokenType = "Bearer"`.

### 2. Estrutura do token

`JwtTokenIssuer` monta `header.payload.assinatura`, cada parte em Base64 URL sem padding:

- Header: `{"alg":"HS256","typ":"JWT"}`
- Payload:

| Claim | Conteúdo |
| --- | --- |
| `sub` | `users.id` (UUID) |
| `role` | `CUSTOMER` ou `SUPPORT` |
| `ver` | `users.token_version` no momento do login |
| `iat` | emissão (epoch em segundos) |
| `exp` | `iat` + `app.jwt.expiration-minutes` × 60 |

- Assinatura: HMAC-SHA256 de `header.payload` com `app.jwt.secret`.

Configuração (`application.properties`):

- `app.jwt.secret=${JWT_SECRET}` — obrigatório; sem valor padrão. O `JwtTokenIssuer` falha na inicialização (`IllegalStateException`) se o segredo tiver **menos de 32 bytes**.
- `app.jwt.expiration-minutes=${JWT_EXPIRATION_MINUTES:1440}` — padrão de **1440 minutos (24 h)**.

### 3. Requisição autenticada

O `JwtAuthenticationFilter` é registrado **antes** do `UsernamePasswordAuthenticationFilter`:

1. Se não houver header `Authorization` ou ele não começar com `Bearer `, o filtro apenas segue a cadeia (sem autenticar). Rotas públicas passam; rotas protegidas são barradas depois pela autorização (401).
2. Caso contrário, chama `TokenVerifier.verify(token)` (`JwtTokenVerifier`), que:
   - exige exatamente 3 partes separadas por ponto;
   - recalcula a assinatura e compara com `MessageDigest.isEqual`;
   - rejeita se `exp` <= agora;
   - exige o claim numérico `ver`;
   - converte `sub` em UUID e `role` em `Role`;
   - busca o usuário no banco (`findById`) e rejeita se não existir ou se `ver` != `token_version` atual.
   - Qualquer `RuntimeException` de parsing vira `InvalidTokenException`.
3. Se válido, o filtro grava no `SecurityContext` um `UsernamePasswordAuthenticationToken` com principal `AuthenticatedUser(userId, role)` e authority `ROLE_<role>`.
4. Se `InvalidTokenException`, limpa o contexto e aciona o `AuthenticationEntryPoint` (resposta 401 `UNAUTHORIZED`).
5. A autorização por perfil é aplicada pela `SecurityFilterChain`.
6. Os controllers recebem o usuário pelo parâmetro `@CurrentUser AuthenticatedUser`, resolvido por `CurrentUserArgumentResolver` (registrado em `WebAuthConfiguration`). Se não houver `AuthenticatedUser` no contexto, lança `InvalidTokenException`.

### 4. Logout

`POST /logout` → `LoginService.logout(userId)` (`@Transactional`): busca o usuário (se não existir, `InvalidTokenException`), chama `incrementTokenVersion()` e salva. Como o verifier compara `ver` a cada requisição, todos os tokens anteriores do usuário passam a ser rejeitados com 401.

### Outros pontos de configuração

- **CORS** (`CorsConfiguration`): origem permitida somente `http://localhost:5173`; métodos `GET, POST, PUT, PATCH, DELETE, OPTIONS`.
- **Swagger** (`SpringDocConfigurations`): declara um esquema de segurança `bearer-key` (HTTP bearer, formato JWT).
- `UserDetailsService` da configuração sempre lança `UsernameNotFoundException`; o login não usa o mecanismo padrão do Spring Security.

**Arquivos:** `LoginService.java`, `JwtTokenIssuer.java`, `JwtTokenVerifier.java`, `JwtAuthenticationFilter.java`, `Pbkdf2PasswordVerifier.java`, `CurrentUserArgumentResolver.java`, `CurrentUser.java`, `AuthenticatedUser.java`, `SecurityConfiguration.java`, `CorsConfiguration.java`, `WebAuthConfiguration.java`, `application.properties`, `dev/infra/springdoc/springdoc/SpringDocConfigurations.java`.
**Testes:** `LoginServiceTest`, `JwtTokenVerifierTest`, `JwtAuthenticationFilterTest`.

## Regras de negócio

- **Identificação por e-mail:** o login usa o e-mail como credencial; é a busca exata `findByEmail`, sem normalização de caixa vista no código.
- **Mensagem genérica:** e-mail inexistente e senha incorreta produzem o mesmo erro (`INVALID_CREDENTIALS`).
- **Perfis:** `CUSTOMER` (clientes; `/tickets/**`, `/projects`) e `SUPPORT` (suporte; `/support/**`). O perfil vem do campo `users.role` e do token; o service de negócio não precisa reler a role.
- **Perfil diferente do exigido** pela rota resulta em 403.
- **Usuário autenticado sem cadastro do perfil:** services de outros módulos lançam `ForbiddenException` (403) quando não encontram o registro vinculado (por exemplo, `customers` ou `support` pelo `user_id`) — usado em `TicketService`, `MessageService` e `ProjectService`.
- **Validade do token:** 24 h por padrão, configurável por `JWT_EXPIRATION_MINUTES`.
- **Invalidação:** apenas por logout (incremento de `token_version`) ou expiração. Não há blacklist por token individual; o logout invalida **todos** os tokens do usuário.
- **Armazenamento de senha:** hash PBKDF2 (`Pbkdf2PasswordHasher` no cadastro; `Pbkdf2PasswordVerifier` no login, ambos com `defaultsForSpringSecurity_v5_8()`). O cadastro de cliente (`CustomerService`) grava `passwordHasher.hash(password)`.
- **Dados sensíveis em logs:** `LoginRequest`, `LoginResponse` e `LoginResult` sobrescrevem `toString()` para não expor senha/token.
- **Usuários de demonstração** (migração `V11__insert-demo-user.sql`): um `CUSTOMER` e um `SUPPORT`, com e-mails e hash de senha fixos no script, além de registros vinculados em `customers` e `support`.

### Informações não confirmadas

- **Atribuição de `role` no cadastro de cliente:** `CustomerService` não chama nenhum setter de `role` (a entidade `user.domain.entity.User` tem `role` como `String` sem valor padrão) e o `grep` não encontrou outro ponto que defina `CUSTOMER`. Não foi possível confirmar como um cliente recém-cadastrado recebe a role necessária para logar (a coluna `role` em `V2__create-table-users.sql` aceita nulo, enquanto a entidade de auth a declara `nullable = false`).
- **Política de senha** (tamanho mínimo, complexidade), **bloqueio por tentativas**, **refresh token**, **recuperação de senha** e **normalização de e-mail**: nenhuma evidência no código; não confirmados.
- **Existência dos usuários de demonstração em produção:** não confirmado (depende de quais migrações são executadas no ambiente).
- A interpretação de `UUID` inválido em `sub` ou `role` desconhecida resulta em `InvalidTokenException` (via captura de `RuntimeException`), mas só o caso de token malformado/sem `ver` é coberto explicitamente por testes.

**Arquivos:** `LoginService.java`, `User.java` (auth), `Role.java`, `CustomerService.java`, `Pbkdf2PasswordHasher.java`, `V2__create-table-users.sql`, `V11__insert-demo-user.sql`.

## Tratamento de erros

Todas as respostas de erro de autenticação usam `ApiError`:

```json
{ "code": "CODIGO", "message": "Mensagem em português", "fieldErrors": { } }
```

| Situação | HTTP | `code` | Mensagem | Origem |
| --- | --- | --- | --- | --- |
| E-mail inexistente ou senha incorreta | 401 | `INVALID_CREDENTIALS` | "E-mail ou senha inválidos." | `AuthExceptionHandler` |
| Campos inválidos no login | 400 | `VALIDATION_ERROR` | "Verifique os campos informados." (+ `fieldErrors` por campo: `email`, `password`) | `AuthExceptionHandler` |
| Corpo JSON ausente/malformado no login | 400 | `INVALID_REQUEST_BODY` | "Envie um corpo JSON válido com e-mail e senha." | `AuthExceptionHandler` |
| Token ausente em rota protegida, malformado, com assinatura inválida, expirado, com `ver` desatualizado (pós-logout) ou usuário inexistente | 401 | `UNAUTHORIZED` | "Sessão inválida ou expirada. Faça login novamente." | `AuthenticationEntryPoint` (`SecurityConfiguration`) / `AccessExceptionHandler` |
| Perfil sem permissão para a rota (ex.: `CUSTOMER` em `/support/**`) | 403 | `FORBIDDEN` | "Você não tem permissão para acessar este recurso." | `AccessDeniedHandler` (`SecurityConfiguration`) |
| Usuário autenticado sem cadastro do perfil (`ForbiddenException`) | 403 | `FORBIDDEN` | idem | `AccessExceptionHandler` |
| Rota não mapeada na `SecurityConfiguration`, com usuário autenticado | 403 | `FORBIDDEN` | idem | `denyAll` + `AccessDeniedHandler` (sem token, esperado 401; não verificado por teste) |

Observações:

- `AuthExceptionHandler` é restrito a `LoginController` (`assignableTypes`); o tratamento de `INVALID_CREDENTIALS`, `VALIDATION_ERROR` e `INVALID_REQUEST_BODY` vale só para `/login`, `/session` e `/logout`. Outros módulos têm seus próprios handlers.
- `AccessExceptionHandler` é global e trata `InvalidTokenException` (401) e `ForbiddenException` (403) lançadas dentro de controllers/services.
- Requisição sem token a uma rota protegida: a resposta 401 vem do `authenticationEntryPoint` configurado em `exceptionHandling` (comportamento verificado em `SupportTicketSecurityTest.shouldReturnUnauthorizedWhenAssigningWithoutToken`). Rotas protegidas com perfil errado devolvem 403 (`shouldReturnForbiddenWhenCustomerAssignsTicket`).
- `server.error.include-message=always` e `include-stacktrace=never` em `application.properties`.

**Arquivos:** `AuthExceptionHandler.java`, `AccessExceptionHandler.java`, `ApiError.java`, `InvalidCredentialsException.java`, `InvalidTokenException.java`, `ForbiddenException.java`, `SecurityConfiguration.java`.
**Testes:** `LoginControllerTest`, `SupportTicketSecurityTest`, `JwtAuthenticationFilterTest`.

## Arquivos relacionados

Caminhos relativos a `backend/src/main/java/com/branch_master/ecovolt360/` (código) e `backend/src/test/java/com/branch_master/ecovolt360/` (testes), salvo indicação.

### Código

| Arquivo | Responsabilidade |
| --- | --- |
| `auth/presentation/controller/LoginController.java` | `POST /login`, `GET /session`, `POST /logout` |
| `auth/presentation/dto/LoginRequest.java`, `LoginResponse.java`, `SessionResponse.java`, `ApiError.java` | Contratos de entrada/saída e formato de erro |
| `auth/presentation/exception/AuthExceptionHandler.java` | Erros do login (401/400) |
| `auth/presentation/exception/AccessExceptionHandler.java` | 401/403 globais |
| `auth/presentation/security/CurrentUser.java`, `CurrentUserArgumentResolver.java` | Injeta o usuário autenticado nos controllers |
| `auth/application/service/LoginService.java` | Login e logout |
| `auth/application/port/PasswordVerifier.java`, `TokenIssuer.java`, `TokenVerifier.java` | Portas |
| `auth/application/dto/AuthenticatedUser.java`, `LoginResult.java` | DTOs internos |
| `auth/application/exception/*` | `InvalidCredentialsException`, `InvalidTokenException`, `ForbiddenException` |
| `auth/domain/entity/User.java`, `Role.java`; `auth/domain/repository/UserRepository.java` | Usuário (com `tokenVersion`) e perfis |
| `auth/infrastructure/persistence/JpaUserRepository.java`, `SpringDataUserRepository.java` | Persistência |
| `auth/infrastructure/security/JwtTokenIssuer.java`, `JwtTokenVerifier.java`, `JwtAuthenticationFilter.java`, `Pbkdf2PasswordVerifier.java` | JWT e verificação de senha |
| `auth/infrastructure/config/SecurityConfiguration.java`, `AuthConfiguration.java`, `CorsConfiguration.java`, `WebAuthConfiguration.java` | Segurança, beans, CORS e resolver |
| `user/application/port/PasswordHasher.java`, `user/infrastructure/security/Pbkdf2PasswordHasher.java` | Hash de senha no cadastro |
| `customer/application/service/CustomerService.java` | Cadastro (`POST /customers`, público) |
| `dev/infra/springdoc/springdoc/SpringDocConfigurations.java` | Esquema bearer do Swagger |
| `backend/src/main/resources/application.properties` | `app.jwt.*` |
| `backend/src/main/resources/db/migration/V2__create-table-users.sql`, `V11__insert-demo-user.sql` | Tabela `users` (`token_version`) e usuários de demonstração |

### Testes

| Arquivo | Cobertura |
| --- | --- |
| `auth/application/service/LoginServiceTest.java` | Login válido, e-mail inexistente, senha errada, incremento de versão no logout, logout de usuário inexistente |
| `auth/presentation/controller/LoginControllerTest.java` | Contrato HTTP de `/login` e `/session`, erros 401/400 |
| `auth/infrastructure/security/JwtTokenVerifierTest.java` | Token válido, expirado, assinatura inválida, malformado, sem `ver`, versão divergente, usuário inexistente, falha de banco |
| `auth/infrastructure/security/JwtAuthenticationFilterTest.java` | Sem header, header não Bearer, token válido, token inválido |
| `auth/infrastructure/persistence/JpaUserRepositoryIntegrationTest.java` | Busca de usuário por e-mail |
| `ticket/presentation/controller/SupportTicketSecurityTest.java` | 401/403/sucesso por perfil nas rotas de suporte |
