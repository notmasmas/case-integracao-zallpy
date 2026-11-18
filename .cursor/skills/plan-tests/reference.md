# Referência de plan-tests

## Critérios de priorização

| Critério | Prioridade | Ação |
|----------|------------|------|
| Cobertura = 0 % | Alta | Escrever testes do zero |
| Cobertura entre 1 % e 79 % | Média | Complementar cobertura existente |
| Cobertura ≥ 80 % | Ignorar | Fora do escopo deste plano |
| `LINE_COVERED + LINE_MISSED == 0` | Ignorar | Interface ou enum sem corpo |

## Mapeamento de seams por sufixo

| Sufixo da classe | Tipo de seam | Dependências típicas |
|------------------|-------------|----------------------|
| `Controller` | HTTP — MockMvc standalone | `MockMvc`, mocks dos services |
| `Service` | Regra pura | Instância direta, mocks dos repositories/ports |
| `Repository` | Persistência | `@DataJpaTest`, `TestEntityManager` |
| `UseCase` | Regra de aplicação | Instância direta, mocks dos ports |
| `Mapper` / `Converter` | Transformação pura | Instância direta, sem mocks |
| Demais | Verificar contexto | Consultar `.cursor/skills/tdd/reference.md` |

Para seams detalhados com exemplos de código, consulte
`.cursor/skills/tdd/reference.md`.

## Template do plano de saída

Use este template ao apresentar o plano ao usuário na etapa 3:

```markdown
## Plano de testes — {{DATA}}

**Cobertura geral atual:** {{PERCENTUAL_GERAL}} %

### Demandas de Alta prioridade (0 %)

| Classe | Pacote | Cobertura | Seam | Arquivo sugerido |
|--------|--------|-----------|------|-----------------|
| FooController | com/example/foo/presentation | 0 % | MockMvc standalone | FooControllerTest.java |

### Demandas de Média prioridade (< 80 %)

| Classe | Pacote | Cobertura | Seam | Arquivo sugerido |
|--------|--------|-----------|------|-----------------|
| BarService | com/example/bar/application | 45 % | Regra pura | BarServiceTest.java |

**Total de classes a cobrir:** {{N}}
**Estimativa de ciclos red-green-refactor:** {{N}} (um por classe)
```

## Caminho dos arquivos de teste

Convenção do projeto: `backend/src/test/java/{{PACKAGE}}/{{CLASS}}Test.java`

Exemplo:
- Classe: `com.branch_master.ecovolt360.auth.application.service.LoginService`
- Teste: `backend/src/test/java/com/branch_master/ecovolt360/auth/application/service/LoginServiceTest.java`

## Prompt completo para o subagente

Ao spawnar o subagente na etapa 4, passe o contexto completo:

```
Você recebeu um plano de testes unitários para o backend Java/Spring (Maven).

Plano de demandas:
{{PLANO_TABELA_COMPLETA}}

Instruções:
1. Para cada classe na tabela, escreva testes seguindo red-green-refactor
2. Siga os seams e exemplos em `.cursor/skills/tdd/reference.md`
3. Um teste por critério observável — não escreva a suíte inteira de uma vez
4. Rode cada classe: `./mvnw -Dtest=NomeDaClasse test` (Windows: `mvnw.cmd`)
5. Confirme o vermelho antes de qualquer código de produção
6. Após verde, refatore se necessário, depois passe para a próxima classe

Referências:
- Seams e exemplos: `.cursor/skills/tdd/reference.md`
- Cobertura atual: relatório JaCoCo já executado nesta sessão
```

## Modelo a usar no subagente

Slug: `cursor-grok-4.6-medium`

Invoke via ferramenta `Task` com `subagent_type: generalPurpose` e
`model: cursor-grok-4.6-medium`.
