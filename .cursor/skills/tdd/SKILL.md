---
name: tdd
description: >-
  Conduz test-driven development a partir dos critérios de aceite de um card
  do Jira. Use quando o desenvolvedor inicia uma tarefa, cita uma chave do
  Jira, critérios de aceite, TDD, red-green-refactor, ou pede para gerar o
  teste que falha e parar no vermelho.
---

# TDD a partir do Jira

Ao iniciar uma tarefa, este fluxo produz um teste que falha a partir dos critérios de aceite. Cada seção vale em todo ciclo: consulte antes e durante o loop, não depois.

Seams, exemplos e comando: [reference.md](reference.md).

## Iniciar a tarefa

O desenvolvedor passa a chave (por exemplo `PROJ-123`) ou cola o card.

```
TDD:
- [ ] Ler o card
- [ ] Extrair critérios observáveis
- [ ] Mapear seams
- [ ] Escrever um teste que falha
- [ ] Rodar só essa classe e confirmar o vermelho
- [ ] Parar, sem código de produção
```

### 1. Ler o card

Use o MCP da Atlassian:

1. `getAccessibleAtlassianResources` uma vez nesta sessão e guarde o `cloudId`.
2. `getJiraIssue` com esse `cloudId`, a chave em `issueIdOrKey` e `view: "evidence"`, para trazer campos customizados.

Critérios, nesta ordem:

- Campo de acceptance criteria ou critérios de aceite, se existir.
- Senão, a descrição: títulos "Critérios de aceite", "Acceptance Criteria" ou checklist.

Sem critério observável, pare e peça o que falta. Não invente comportamento.

### 2. Mapear seams

Antes de escrever o teste, mostre na mesma resposta o mapa: critério, interface pública, tipo de teste, arquivo. Use só os seams de [reference.md](reference.md).

Seam ambíguo: pergunte. Seam já praticado no repositório: siga.

Critério só de tela: o frontend não tem runner. Registre no checklist e diga que falta runner. Não crie arquivo de teste especulativo.

### 3. Um teste vermelho

Escreva um teste: o primeiro critério ainda sem cobertura. Um seam, um comportamento, o mínimo para falhar pela asserção do critério.

Rode só essa classe, a partir de `backend/`:

```bash
./mvnw -Dtest=NomeDaClasse test
```

O vermelho certo é a asserção do critério. Erro de compilação ou de setup não conta: corrija o teste e rode de novo.

### 4. Parar

Não implemente produção neste turno. Não antecipe o próximo critério.

No pedido "ficar verde", implemente só o suficiente para esse teste passar, rode a mesma classe e só então volte ao próximo critério sem cobertura.

## O que é um bom teste

O teste observa comportamento pela interface pública. O nome lê como especificação (`shouldReturnTokenForValidCredentials`). O valor esperado vem do critério: um literal ou um exemplo conhecido. Não recalcule o esperado pela mesma fórmula da implementação.

## Proibições

- Não testar método privado nem mockar colaborador interno. Mock só nos ports da borda já usada no seam.
- Não afirmar um valor recalculado do mesmo jeito que o código.
- Não escrever a suíte inteira e depois a implementação. O card vira checklist; cada ciclo entrega um teste.
- Não refatorar dentro do ciclo. Vermelho, depois o mínimo para verde.
