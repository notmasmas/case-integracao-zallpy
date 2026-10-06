---
name: check-develop-conflicts
description: Compara a branch em que a pessoa está trabalhando com a develop (ou outra branch base) e aponta conflitos de merge sem alterar nada no repositório — simula o merge com git merge-tree, lista commits à frente/atrás, arquivos alterados dos dois lados e explica cada conflito com sugestão de resolução. Use sempre que alguém perguntar se a branch conflita com a develop, se dá para abrir PR/fazer merge, o que mudou na develop desde que criou a branch, se a branch está desatualizada, ou pedir para "checar conflitos", "comparar com a develop", "ver diferenças com a develop", "vai dar conflito?", "preciso atualizar minha branch?" — mesmo que não diga a palavra "conflito". Também serve para outra base (main, master, qa) quando o usuário nomeá-la.
---

# Verificar diferenças e conflitos com a develop

O objetivo é responder, antes de qualquer merge ou PR: **"minha branch vai conflitar com a develop, e onde?"** — e dar à pessoa o contexto para resolver.

## Regra de segurança

Esta verificação é **somente leitura**. O script usa `git merge-tree --write-tree`, que simula o merge em memória: não faz checkout, não mexe no working tree nem cria commits. Não rode `git merge`, `rebase`, `checkout`, `reset`, `stash` ou `push` por conta própria — a pessoa pode ter trabalho em andamento. Se ela quiser resolver os conflitos depois do relatório, pergunte antes e siga o que ela escolher.

## Passo 1 — Rodar o script

A partir de qualquer pasta do repositório:

```bash
bash "$(git rev-parse --show-toplevel)/.claude/skills/check-develop-conflicts/scripts/check_conflicts.sh" [branch-base] [--no-fetch]
```

- `branch-base` é `develop` por padrão. Passe outra (ex.: `master`, `qa`) se o usuário pedir.
- O script faz `git fetch origin <base>` para comparar com a versão mais recente do remoto, que é o que vai valer no PR. Use `--no-fetch` só se o usuário pedir ou se estiver sem rede.
- Código de saída: `0` = sem conflitos (ou nada a comparar), `1` = há conflitos, `2` = erro (a mensagem explica: repo inválido, base inexistente, git antigo). Em caso de erro, relate a mensagem e sugira a correção em vez de tentar contornar.

A saída vem em seções (`== RESUMO ==`, `== COMMITS ... ==`, `== ARQUIVOS ALTERADOS DOS DOIS LADOS ==`, `== SIMULAÇÃO DE MERGE ==`, `== TRECHOS EM CONFLITO ==`). Nos trechos, o lado `HEAD` é a branch da pessoa e o outro lado (ex.: `origin/develop`) é a base.

## Passo 2 — Entender cada conflito

Para cada arquivo em conflito, o trecho com marcadores mostra *o quê* conflita; para explicar *por quê*, olhe o histórico dos dois lados:

```bash
git log --oneline <merge-base>..HEAD -- <arquivo>          # o que a pessoa mudou
git log --oneline <merge-base>..origin/develop -- <arquivo> # o que entrou na develop
git diff <merge-base> origin/develop -- <arquivo>           # a mudança da develop
```

(O merge-base aparece no RESUMO.) Com isso, descreva em linguagem simples a intenção de cada lado e sugira a resolução mais provável — manter um lado, combinar os dois, ou conversar com o autor da outra mudança quando a intenção for incompatível. Não invente: se não der para inferir a intenção, diga isso.

Tipos de conflito que aparecem nas `MENSAGENS DO GIT` e o que significam:
- **content**: os dois lados editaram as mesmas linhas.
- **modify/delete**: um lado editou um arquivo que o outro apagou — decidir se o arquivo deve continuar existindo.
- **rename/rename, rename/delete, add/add**: mudanças de estrutura concorrentes — explique o que cada lado fez com o arquivo.
- **binário**: não há como combinar; é preciso escolher uma das versões.

## Passo 3 — Olhar além dos conflitos textuais

Arquivos em `ARQUIVOS ALTERADOS DOS DOIS LADOS` que **não** conflitaram ainda podem quebrar juntos (ex.: a develop renomeou uma função que sua branch passou a usar em outro arquivo). Liste-os como "revisar" e, se algum parecer arriscado pelo diff, explique o motivo. Não precisa abrir todos — foque nos que mexem em código compartilhado (tipos, componentes, rotas, configs, package.json/lockfiles).

Olhe também `ARQUIVOS QUE A SUA BRANCH REMOVE`. Quando o PR entrar, esses arquivos somem da base — e remoções que não têm relação com o tema da branch (ex.: uma branch de tela de cadastro apagando o painel de suporte) quase sempre são acidentes de um merge/rebase mal resolvido. Destaque-as mesmo sem conflitos e procure o commit responsável com `git log -m --diff-filter=D --oneline <base>..HEAD -- <arquivo>`. O `-m` é essencial: quando a perda vem de um merge da base na branch, o arquivo nunca existiu do lado da branch — ele só "some" na comparação do commit de merge com o pai que veio da base (a saída mostra `(from <pai>)`). Pergunte se a remoção foi intencional; não restaure nada sem confirmação.

Se houver `ALTERAÇÕES NÃO COMMITADAS`, avise que elas **não** entraram na simulação — o resultado vale para o que está commitado.

## Passo 4 — Relatório

Responda no idioma da conversa (normalmente português), neste formato:

```markdown
## `<branch>` × `<base>`

**Resumo:** X commits à frente, Y atrás. <Sem conflitos ✅ | ⚠️ N arquivo(s) em conflito>.

### Conflitos
<por arquivo: caminho (tipo) — o que sua branch fez, o que a develop fez, sugestão de resolução>

### Arquivos que sua branch remove
<só se houver; aponte os que parecem acidentais e o commit responsável; omita se vazia>

### Alterados dos dois lados, sem conflito (revisar)
<lista curta com observação quando houver risco; omita a seção se vazia>

### O que entrou na develop desde que você criou a branch
<commits relevantes, resumidos; agrupe se forem muitos>

### Próximos passos
<ex.: "atualize sua branch com `git merge origin/develop` (ou rebase, se o time usar) e resolva os conflitos acima"; rode build/lint depois do merge>
```

Se não houver conflitos, mantenha o relatório curto: resumo, eventuais arquivos para revisar, e o próximo passo. Se a branch estiver só atrás (0 à frente), diga que não há nada dela para mesclar ainda.
