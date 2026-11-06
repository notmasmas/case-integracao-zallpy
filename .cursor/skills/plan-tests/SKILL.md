---
name: plan-tests
description: >-
  Orquestra a verificação de cobertura JaCoCo, identifica classes sem testes
  unitários e monta um plano de ação priorizado. Delega a escrita dos testes
  a um subagente com o modelo grok 4.6 medium seguindo red-green-refactor.
  Use quando o desenvolvedor pede um plano de testes, quer saber o que falta
  testar, menciona lacunas de cobertura, ou quer que os testes sejam escritos
  automaticamente a partir do relatório de cobertura.
---

# Plano de testes unitários

Orquestra a skill de cobertura, prioriza as demandas e delega a escrita ao
subagente correto. Não escreve testes diretamente neste turno — só planeja.

Seams, exemplos e critérios de priorização: [reference.md](reference.md).

## Checklist

```
Plano de testes:
- [ ] Carregar e executar a skill de cobertura
- [ ] Identificar demandas (classes abaixo de 80 % ou com 0 %)
- [ ] Priorizar e montar o plano
- [ ] Apresentar o plano ao usuário
- [ ] Spawnar subagente para escrever os testes
```

## 1. Carregar a skill de cobertura

Leia `../.cursor/skills/coverage/SKILL.md` (caminho relativo ao workspace:
`.cursor/skills/coverage/SKILL.md`) e execute **todos os seus passos**:

1. Verificar o plugin JaCoCo no `pom.xml`
2. Executar o JaCoCo
3. Ler `backend/target/site/jacoco/jacoco.csv`
4. Apresentar o resumo de cobertura

Se o CSV não existir após a execução, mostre o erro do terminal e **pare**.

## 2. Identificar demandas

A partir do CSV já lido, aplique as regras em ordem:

1. **0 %** — classes com zero cobertura de linhas (prioridade Alta)
2. **< 80 %** — classes com cobertura abaixo de 80 % (prioridade Média)
3. Ignore classes onde `LINE_COVERED + LINE_MISSED == 0` (interfaces, enums sem corpo)

Para cada classe demandada, determine o tipo de seam pelo sufixo do nome:

| Sufixo | Seam | Exemplo de abordagem |
|--------|------|----------------------|
| `Controller` | HTTP (MockMvc standalone) | `MockMvc`, `@WebMvcTest` |
| `Repository` | Persistência | `@DataJpaTest`, `TestEntityManager` |
| `Service` | Regra pura | instanciar direto, mockar ports |
| Demais | Verificar contexto | regra pura ou consultar `../tdd/reference.md` |

Consulte os seams completos em [reference.md](reference.md).

## 3. Montar o plano

Apresente ao usuário a tabela de demandas antes de qualquer escrita de teste:

```
| Classe | Pacote | Cobertura | Seam | Prioridade |
|--------|--------|-----------|------|------------|
| ...    | ...    | 0 %       | ...  | Alta       |
| ...    | ...    | 45 %      | ...  | Média      |
```

Ordene por prioridade (Alta → Média) e, dentro da mesma prioridade, pelo
percentual crescente.

**Aguarde confirmação ou ajuste do usuário antes de continuar.**

## 4. Escrever os testes

Após confirmação do usuário, spawne um subagente usando o modelo
`cursor-grok-4.6-medium` com o seguinte prompt, substituindo os marcadores:

```
Você recebeu um plano de testes unitários para o backend Java/Spring.

Plano de demandas:
{{PLANO_TABELA}}

Para cada classe da tabela, escreva os testes seguindo red-green-refactor:
- Um teste por critério observável
- Siga os seams e exemplos em `.cursor/skills/tdd/reference.md`
- Rode cada classe individualmente: `./mvnw -Dtest=NomeDaClasse test`
- Confirme o vermelho antes de implementar produção
- Não escreva a suíte inteira de uma vez

Referência de seams: `.cursor/skills/tdd/reference.md`
```

O subagente deve seguir o ciclo completo: vermelho → mínimo para verde →
refatorar → próxima classe.
