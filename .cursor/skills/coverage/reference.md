# Referência de cobertura

## Formato do CSV do JaCoCo

`backend/target/site/jacoco/jacoco.csv` — uma linha de cabeçalho, depois uma
linha por classe.

Colunas (nesta ordem):

| Posição | Nome | Descrição |
| --- | --- | --- |
| 0 | GROUP | Nome do artefato Maven (ignora) |
| 1 | PACKAGE | Pacote Java separado por `/` (ex.: `com/branch_master/ecovolt360/auth/application/service`) |
| 2 | CLASS | Nome simples da classe (ex.: `LoginService`) |
| 3 | INSTRUCTION_MISSED | Bytecode instructions não cobertas |
| 4 | INSTRUCTION_COVERED | Bytecode instructions cobertas |
| 5 | BRANCH_MISSED | Branches não cobertas |
| 6 | BRANCH_COVERED | Branches cobertas |
| 7 | LINE_MISSED | Linhas não cobertas |
| 8 | LINE_COVERED | Linhas cobertas |
| 9 | COMPLEXITY_MISSED | Complexidade ciclomática não coberta |
| 10 | COMPLEXITY_COVERED | Complexidade ciclomática coberta |
| 11 | METHOD_MISSED | Métodos não cobertos |
| 12 | METHOD_COVERED | Métodos cobertos |

Use `LINE_COVERED` e `LINE_MISSED` (posições 8 e 7) como métrica principal.

## Testes que precisam do banco

Os testes abaixo usam `@SpringBootTest` e exigem PostgreSQL:

- `Ecovolt360ApplicationTests`
- `JpaUserRepositoryIntegrationTest`

Para subir o banco: `docker compose up` na raiz do repositório.

Sem o banco, execute:

```bash
./mvnw test jacoco:report -Dspring.flyway.enabled=false
```

Esses testes falharão com `Connection refused` ou `Unable to acquire JDBC Connection`.
Isso é esperado. Liste-os como não executados e continue com o relatório dos demais.

## Comandos

| Situação | Comando (a partir de `backend/`) |
| --- | --- |
| Banco rodando | `./mvnw verify` |
| Sem banco | `./mvnw test jacoco:report -Dspring.flyway.enabled=false` |
| Windows sem script shell | `mvnw.cmd verify` ou `mvnw.cmd test jacoco:report -Dspring.flyway.enabled=false` |

## Relatório HTML

O relatório navegável fica em `backend/target/site/jacoco/index.html`.
Use-o para inspeção visual; para análise automatizada prefira o CSV.
