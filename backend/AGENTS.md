# AGENTS.md

This file provides guidance to AI coding agents (e.g. Claude Code) when working with code in this repository.

Backend of **EcoVolt360 – Portal do Cliente** (customer portal: installation/project status, system monitoring, support tickets). Spring Boot 4.1, Java 25, PostgreSQL, Flyway, Spring Security with JWT, Lombok, springdoc-openapi. The React frontend lives in `../frontend` (it has its own AGENTS.md). Code, messages and commits are in Portuguese/English mix; user-facing error messages are in Portuguese.

## Commands

Run from `backend/` (on Windows use `mvnw.cmd` or `./mvnw` in Git Bash):

```bash
docker compose up                # from repo root: starts Postgres (host port ${DB_PORT:-5433})
./mvnw spring-boot:run           # run the API (default port 8080)
./mvnw test                      # all tests
./mvnw test -Dtest=LoginControllerTest              # single test class
./mvnw test -Dtest=LoginControllerTest#methodName   # single test method
./mvnw clean package             # build jar
```

- Config comes from a `.env` file in the working directory (`spring.config.import=optional:file:.env[.properties]`); see `../.env.example` for keys (`DB_*`, `JWT_SECRET`, `JWT_EXPIRATION_MINUTES`). The root `.env` feeds docker compose; `backend/.env` feeds the app.
- `@SpringBootTest` tests (`Ecovolt360ApplicationTests`, `JpaUserRepositoryIntegrationTest`) need a reachable Postgres with the schema already migrated — they disable Flyway and `ddl-auto=validate` checks the schema. Controller tests are plain Mockito + `MockMvcBuilders.standaloneSetup` and need no DB.
- Swagger UI: `http://localhost:8080/swagger-ui.html`. A `bearer-key` JWT scheme is defined in `dev/infra/springdoc/.../SpringDocConfigurations.java`, but controllers must add `@SecurityRequirement(name = "bearer-key")` for Swagger to send the token.

## Architecture

Package-by-feature under `com.branch_master.ecovolt360`, each feature (`auth`, `customer`, `ticket`, `messages`, `support`, `user`, `address`) split into hexagonal-style layers:

- `domain/entity` – JPA entities (Lombok). `domain/repository` – plain repository **interfaces** (ports), no Spring types.
- `application/service` – use-case services. **Services are not `@Service`-annotated**; they are plain classes wired manually as `@Bean`s in the feature's `infrastructure/config/*Configuration.java`. When adding a dependency to a service, update that configuration class too. `application/dto` holds request/response records; `application/exception` holds domain exceptions; `application/port` holds abstractions for infra concerns (e.g. `TokenIssuer`, `PasswordVerifier`, `PasswordHasher`).
- `infrastructure/persistence` – two classes per repository: `SpringData*Repository` (Spring Data JPA interface, queries) and `Jpa*Repository` (`@Repository` adapter implementing the domain interface by delegating to it).
- `presentation/controller` – REST controllers. `presentation/exception` – a `@RestControllerAdvice(assignableTypes = {...})` scoped to that feature's controllers, mapping exceptions to `ApiError(code, message, details)` (`auth/presentation/dto/ApiError`). New controllers must be added to the relevant advice's `assignableTypes`, and new exceptions need a handler there.

Cross-feature code reuses other features' domain repositories directly (e.g. `TicketService` depends on `CustomerRepository`, `MessageRepository`, `SupportRepository`, auth `UserRepository`).

There are two `User` entities mapping the same `users` table: `auth.domain.entity.User` (`@Entity(name = "AuthUser")`, used for login/role checks) and `user.domain.entity.User` (used for account creation with `PasswordHasher`). Keep their columns consistent.

### Auth & authorization

- `POST /login` (`LoginService`) verifies a PBKDF2 hash and issues a JWT (`JwtTokenIssuer`, secret `app.jwt.secret`). `JwtAuthenticationFilter` validates the bearer token on every request; stateless sessions, CSRF off.
- Controllers get the caller via `@CurrentUser AuthenticatedUser currentUser` (resolver registered in `WebAuthConfiguration`); use `currentUser.userId()` and scope queries by owner (e.g. `findByIdAndCustomerId`, `findByIdAndSupportId`) so users only touch their own records.
- Roles: `CUSTOMER`, `SUPPORT`. Route access is declared centrally in `auth/infrastructure/config/SecurityConfiguration.java` and ends with `.anyRequest().denyAll()` — **every new endpoint must get a `requestMatchers` rule there or it will return 403.** Current convention: `/tickets/**` → CUSTOMER, `/support/**` → SUPPORT; public: `POST /login`, `POST /customers`, Swagger paths. More specific matchers must come before broader ones.
- 401/403 responses are written as `ApiError` JSON by the entry point/denied handler in `SecurityConfiguration`.

### Database

- Schema is owned by Flyway migrations in `src/main/resources/db/migration` (`V<n>__description.sql`); Hibernate only validates (`ddl-auto=validate`), so every entity change needs a new migration. Never edit an applied migration — add the next version. Seed/demo data (users, tickets) is also inserted via migrations.

## Workflow

- Branch off `develop` (PRs target `develop`); branch names like `feature/...`, `refactor/...`, `docs/...`; commits follow conventional style (`feat:`, `refactor:`, `docs:`).
- PR description follows `../.github/pull_request_template.md`: Ticket (Jira id), O que foi feito, Como reproduzir, Revisado por.
