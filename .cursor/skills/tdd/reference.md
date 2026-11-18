# Seams e comandos

Teste só nestas bordas. O teste chama a interface pública e afirma o literal do critério de aceite.

## Contrato HTTP

MockMvc standalone, serviço real, ports mockados.

Modelo: `backend/src/test/java/com/branch_master/ecovolt360/auth/presentation/controller/LoginControllerTest.java`

- `@ExtendWith(MockitoExtension.class)`
- `@Mock` nos ports (`UserRepository`, `PasswordVerifier`, `TokenIssuer`)
- Serviço concreto no `setUp` (`new LoginService(...)`)
- `MockMvcBuilders.standaloneSetup(controller)`, com o advice e o argument resolver que o controller exige
- Nome no estilo `shouldReturnTokenForValidCredentials`
- Asserção de status e JSON com o valor do critério

## Persistência

Integração só na borda do repositório.

Modelo: `backend/src/test/java/com/branch_master/ecovolt360/auth/infrastructure/persistence/JpaUserRepositoryIntegrationTest.java`

- `@SpringBootTest(properties = "spring.flyway.enabled=false")`
- `@Autowired` no `UserRepository`
- `@MockitoBean` nos ports que o contexto sobe e o teste não exercita (`PasswordVerifier`, `TokenIssuer`)
- Linha conhecida via `JdbcTemplate` quando o critério exige um registro existente

## Regra pura

Teste unitário do tipo ou da função pública (validação, cálculo). Sem MockMvc e sem contexto Spring. O valor esperado é o literal do critério.

## Comando

A partir de `backend/`:

```bash
./mvnw -Dtest=NomeDaClasse test
```

No Windows, se o wrapper shell não executar, use `mvnw.cmd -Dtest=NomeDaClasse test` no mesmo diretório.

## Frontend

`frontend/package.json` não tem script de teste. Critério só de interface não ganha arquivo de teste. Registre o critério no checklist e informe que falta runner.

## Mapa na resposta

| Critério | Interface pública | Tipo | Arquivo |
| --- | --- | --- | --- |
| texto observável do card | método, rota ou tipo público | HTTP, persistência, regra pura ou sem runner | caminho do teste, ou vazio se não houver runner |
