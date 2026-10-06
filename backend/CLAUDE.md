# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

Backend do Portal do Cliente EcoVolt360 (case integrador Zallpy Academy). Spring Boot 4.1, Java 25, Maven, PostgreSQL + Flyway, Spring Security com JWT próprio. O frontend (React + TS) fica em `../frontend` e tem seu próprio `AGENTS.md`. Código, mensagens de erro e documentação do projeto são em português.

## Comandos

Rode a partir de `backend/` (no Windows use `mvnw.cmd` ou `./mvnw` no Git Bash):

```bash
./mvnw spring-boot:run                       # sobe a API
./mvnw test                                  # todos os testes
./mvnw test -Dtest=LoginControllerTest       # uma classe
./mvnw test -Dtest=LoginControllerTest#nomeDoMetodo   # um método
./mvnw clean package                         # build do jar
```

Banco local: `docker compose up -d` na raiz do repositório sobe o Postgres (`ecovolt-postgres`) expondo a porta **5433** no host, enquanto `application.properties` monta a URL com porta fixa `5432` — ajuste `DB_HOST`/porta conforme o ambiente.

## Configuração

`application.properties` importa `optional:file:.env[.properties]`, resolvido a partir do diretório de execução (ou seja, `backend/.env` ao rodar de `backend/`; o `docker-compose.yml` usa o `.env` da raiz). Variáveis necessárias:

- `DB_HOST`, `DB_NAME`, `DB_USER`, `DB_PASSWORD` (listadas no `.env.example` da raiz)
- `app.jwt.secret` (mínimo 32 bytes, senão o `JwtTokenIssuer` falha na inicialização) e `app.jwt.expiration-minutes` — **não** estão no `.env.example`

`spring.jpa.hibernate.ddl-auto=validate`: o schema vem só das migrations. Toda mudança em entidade exige migration nova em `src/main/resources/db/migration` com versão `V<n>__descricao.sql` **única** (versões repetidas quebram o Flyway na subida — já houve correções por isso no histórico).

Os testes `@SpringBootTest` (`Ecovolt360ApplicationTests`, `JpaUserRepositoryIntegrationTest`) precisam de um Postgres acessível e das variáveis acima; `LoginControllerTest` é MockMvc standalone com Mockito e roda sem banco.

## Arquitetura

Pacotes por módulo de negócio em `com.branch_master.ecovolt360` (`auth`, `user`, `customer`, `address`, `ticket`, `support`), cada um com camadas no estilo hexagonal:

- `domain/` — entidades JPA e interfaces de repositório (portas) sem Spring Data
- `application/` — services, DTOs (records), exceptions e `port/` para dependências externas (hash de senha, emissão/verificação de token)
- `infrastructure/` — `persistence/` com o par `SpringData*Repository` (interface Spring Data) + `Jpa*Repository` (`@Repository` que implementa a porta do domínio delegando ao Spring Data); `config/` com `@Configuration` que instancia os services via `@Bean`; `security/` com adapters
- `presentation/` — controllers, DTOs de request/response e `@RestControllerAdvice(assignableTypes = XController.class)` por módulo

Convenções que valem ao adicionar código:

- **Services não são anotados com `@Service`**: são POJOs com construtor, registrados no `*Configuration` do módulo. Novo service → novo `@Bean`.
- Erros de API usam `ApiError(code, message, details)` (`auth/presentation/dto`) com `code` em SCREAMING_SNAKE_CASE (ex.: `TICKET_NOT_FOUND`, `CPF_ALREADY_REGISTERED`). Cada módulo mapeia suas exceptions no seu próprio exception handler.
- A tabela `users` é mapeada por **duas** entidades: `auth.domain.entity.User` (entity name `AuthUser`, só leitura para login, `Role` enum) e `user.domain.entity.User` (Lombok, usada no cadastro em `CustomerService`, `role` como `String`). Existem também dois `UserRepository` distintos; cuidado com imports.
- `customers` e `support` têm `user_id` apontando para `users.id`; tickets referenciam `customers.id`/`support.id`, não `users.id`.

### Autenticação e autorização

Detalhado em `src/main/java/com/branch_master/ecovolt360/auth/application/auth.md` — leia antes de mexer em rotas protegidas. Resumo:

- `POST /login` (`LoginService`) valida a senha (PBKDF2) e emite um JWT HMAC-SHA256 feito à mão (`JwtTokenIssuer`/`JwtTokenVerifier`, sem biblioteca JWT) com `sub` = `users.id` e `role` = `CUSTOMER` | `SUPPORT`.
- `SecurityConfiguration` é stateless, sem CSRF, e autoriza **por path**: `POST /login` e `POST /customers` públicos, `/tickets/**` exige `CUSTOMER`, `/support/**` exige `SUPPORT`, **qualquer outra rota é `denyAll`**. Endpoint novo fora desses prefixos precisa ser registrado ali, senão responde 403.
- `JwtAuthenticationFilter` popula o `SecurityContext`; controllers recebem o usuário com `@CurrentUser AuthenticatedUser` (resolvido por `CurrentUserArgumentResolver`, registrado em `WebAuthConfiguration`).
- O service do módulo converte `userId` para o id do perfil (`customerRepository.findByUserId`) e lança `ForbiddenException` se não houver cadastro; não reconsulta a role.
- CORS em `auth/infrastructure/config/CorsConfiguration.java` libera só `http://localhost:5173` (Vite do frontend).
