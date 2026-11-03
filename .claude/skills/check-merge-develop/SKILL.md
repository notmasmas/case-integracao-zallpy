---
name: check-merge-develop
description: Simula o merge da branch atual com a develop (sem alterar nenhum arquivo, index ou branch) e retorna um relatório dos conflitos, ou "branch pronta para mergear" se não houver nenhum. Use sempre que o usuário quiser saber se a branch atual tem conflitos com a develop, se dá para mergear / abrir PR, se precisa fazer rebase, "simular merge", "checar conflitos", "testar merge com a develop", "minha branch está pronta?", mesmo que ele não diga a palavra "simular". Também serve para outra branch-alvo se o usuário pedir (ex.: main, qa).
---

# Simular merge com a develop

O objetivo é responder uma pergunta: "se eu mergear a branch atual na develop agora, vai dar conflito?" — sem mexer em nada. O usuário pode ter trabalho em andamento, então qualquer coisa que altere o working tree, o index ou as branches locais é inaceitável aqui.

## Por que não usar `git merge --no-commit`

O jeito "clássico" (`git merge --no-commit --no-ff` seguido de `git merge --abort`) escreve no working tree e no index, e pode deixar o repositório num estado de merge pela metade se algo falhar no caminho. Por isso a skill usa `git merge-tree --write-tree`, que calcula o merge inteiro só no banco de objetos do git: nenhum arquivo do projeto é tocado. Não use `git merge`, `git checkout`, `git switch`, `git stash`, `git reset`, `git rebase` nem `git pull` durante esta skill.

O único comando "de escrita" executado é `git fetch origin develop`, que apenas atualiza a referência remota `origin/develop` para que a comparação seja contra a versão mais recente — ele não altera código nem branches locais.

## Passos

1. Rode o script incluído (bash / Git Bash). Se o usuário pediu outra branch-alvo, passe o nome como argumento:

   ```bash
   bash "$(git rev-parse --show-toplevel)/.claude/skills/check-merge-develop/scripts/simulate_merge.sh" [develop]
   ```

   Exit code: `0` = sem conflitos, `1` = conflitos, `2` = erro (repo inválido, branch-alvo inexistente, git < 2.38).

2. Leia a saída e monte a resposta conforme o resultado.

### Sem conflitos (`status: SEM_CONFLITOS`)

Responda exatamente:

```
branch pronta para mergear
```

Se a saída tiver avisos relevantes, acrescente-os em linhas curtas logo abaixo — eles mudam a interpretação do resultado:
- alterações não commitadas (a simulação só considera commits, então elas não foram avaliadas);
- a branch não tem commits à frente da develop (o merge não traria nada);
- o fetch falhou (a comparação foi feita com uma `origin/develop` possivelmente desatualizada).

Não acrescente nada além disso.

### Com conflitos (`status: CONFLITOS`)

Gere o relatório neste formato:

````markdown
# Relatório de conflitos: <branch_atual> → <alvo>

**Resultado:** ❌ <N> arquivo(s) em conflito
**Branch atual:** <branch> · <X> commits à frente / <Y> atrás de <alvo>

## Arquivos em conflito

### 1. `caminho/do/arquivo`
- **Tipo:** conteúdo | modificação/remoção | renomeação | binário
- **Commits na branch:** <hash mensagem>, ...
- **Commits na develop:** <hash mensagem>, ...
- **Trechos em conflito:**
  - Linha ~<n>: <resumo em uma frase do que cada lado mudou>

```diff
<trecho com os marcadores <<<<<<< ======= >>>>>>> como veio do script>
```

(repita para cada arquivo)

## Como resolver
<1–3 frases práticas, ex.: "atualize a branch com a develop (`git merge origin/develop` ou `git rebase origin/develop`) e resolva os arquivos acima". Mencione avisos, como alterações não commitadas.>

## 🚫 Pontos bloqueantes
- `<arquivo>` — <decisão ou ação necessária, em uma linha>
- ...

## ⚠️ Pontos de atenção
- <risco que não impede o merge, em uma linha>
- ...
````

**Pontos bloqueantes** são o que impede o merge agora: cada conflito vira um item, formulado como a decisão que alguém precisa tomar (ex.: "`application.properties` — definir qual `server.port` vale: 8081 ou 9090"). Se o mesmo arquivo tiver vários trechos que pedem decisões diferentes, pode virar mais de um item.

**Pontos de atenção** são riscos que não travam o merge, mas que a pessoa deveria saber antes de mergear. Exemplos:
- alterações não commitadas, que ficaram fora da simulação;
- a branch está muitos commits atrás da develop;
- o fetch falhou e a comparação pode estar desatualizada;
- efeitos colaterais prováveis da resolução, como uma assinatura de método alterada que pode quebrar chamadas existentes na develop, ou uma remoção de arquivo que descartaria uma correção feita na develop;
- arquivos sensíveis tocados pelos dois lados (configuração, migrations, dependências).

As duas listas são um resumo para leitura rápida: um item por linha, sem repetir os trechos de código. Se não houver nenhum ponto de atenção, escreva "Nenhum".

Para o resumo de cada trecho, leia os dois lados dos marcadores e explique em linguagem simples o que diverge (ex.: "a develop renomeou o método `getUser` para `fetchUser`, enquanto a branch adicionou um parâmetro em `getUser`"). É isso que torna o relatório útil — a pessoa entende o conflito sem abrir o arquivo. Se um arquivo tiver muitos trechos, mostre os mais relevantes e diga quantos foram omitidos.

### Erro (`status: ERRO`)

Explique o motivo em uma frase e o que o usuário pode fazer (ex.: atualizar o git, verificar o nome da branch). Não tente contornar o problema com comandos que alterem o repositório.

## Observações

- Se o script não puder ser executado (ex.: sem bash), rode diretamente `git fetch origin develop` e `git merge-tree --write-tree --name-only --messages HEAD origin/develop`, e use `git show <tree>:<arquivo>` para ver os marcadores de conflito. Mesmo raciocínio: nada que toque o working tree.
- `merge-tree` grava objetos temporários em `.git/objects` (inofensivos, removidos pelo `git gc`). Isso não é alteração de código.
