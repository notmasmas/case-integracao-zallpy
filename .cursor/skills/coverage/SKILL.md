---
name: coverage
description: >-
  Verifica a cobertura de testes unitários do backend. Use quando o
  desenvolvedor pede cobertura de testes, coverage, jacoco, quais classes
  não estão cobertas, ou verificar testes unitários.
---

# Cobertura de testes unitários

Executa o JaCoCo, lê o CSV gerado e apresenta o resumo. Não escreve testes
nem refatora código neste turno — só reporta.

> Modelo recomendado: **Claude Sonnet 4.6 medium**. Se o modelo atual for diferente,
> informe o desenvolvedor e aguarde a troca antes de executar os passos abaixo.

Formato do CSV e comandos: [reference.md](reference.md).

## Checklist

```
Cobertura:
- [ ] Verificar se o plugin JaCoCo está no pom.xml
- [ ] Executar a cobertura
- [ ] Ler backend/target/site/jacoco/jacoco.csv
- [ ] Apresentar resumo
```

## 1. Verificar o plugin

Procure `jacoco-maven-plugin` no `backend/pom.xml`. Se não existir, adicione o
bloco abaixo na seção `<build><plugins>` antes de continuar:

```xml
<plugin>
  <groupId>org.jacoco</groupId>
  <artifactId>jacoco-maven-plugin</artifactId>
  <version>0.8.12</version>
  <executions>
    <execution>
      <id>prepare-agent</id>
      <goals><goal>prepare-agent</goal></goals>
    </execution>
    <execution>
      <id>report</id>
      <phase>verify</phase>
      <goals><goal>report</goal></goals>
    </execution>
  </executions>
</plugin>
```

## 2. Executar a cobertura

A partir de `backend/`. Escolha conforme o banco:

**Com PostgreSQL rodando** (`docker compose up` na raiz):

```bash
./mvnw verify
```

**Sem banco:**

```bash
./mvnw test jacoco:report -Dspring.flyway.enabled=false
```

Neste segundo caso, `@SpringBootTest` vai falhar (`JpaUserRepositoryIntegrationTest`,
`Ecovolt360ApplicationTests`). Liste-os como "não executados por falta de banco",
mas continue — o relatório ainda é gerado para os testes que passaram.

No Windows, se o script shell não executar: `mvnw.cmd` no lugar de `./mvnw`.

## 3. Ler o relatório

Leia `backend/target/site/jacoco/jacoco.csv`. Se o arquivo não existir, o
`verify` falhou; mostre o erro do terminal e pare.

O formato está em [reference.md](reference.md).

## 4. Apresentar o resumo

Calcule para cada linha: `cobertura = LINE_COVERED / (LINE_COVERED + LINE_MISSED)`.
Ignore linhas onde `LINE_COVERED + LINE_MISSED == 0` (interfaces, enums sem corpo).

Apresente nesta ordem:

**Cobertura geral de linhas** — soma de todos os `LINE_COVERED` / soma de todos
os `(LINE_COVERED + LINE_MISSED)`.

**Cobertura por pacote** — agrupe pelo campo `PACKAGE`, calcule o mesmo
percentual e ordene do menor para o maior.

**Classes abaixo de 80 %** — tabela com `CLASS`, `PACKAGE` e `%` de linhas
cobertas, ordenada pelo percentual crescente.

**Classes com 0 %** — para cada uma, indique qual seam aplicar com base no
nome e pacote:
- Nome termina em `Controller` → seam HTTP (MockMvc standalone)
- Nome termina em `Repository` → seam de persistência
- Demais → regra pura ou verifique o contexto

Consulte os seams e exemplos em [../tdd/reference.md](../tdd/reference.md).

## Proibição

Não escreva testes nem altere código de produção neste turno.
