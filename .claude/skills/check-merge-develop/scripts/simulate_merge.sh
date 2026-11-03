#!/usr/bin/env bash
# Simula o merge da branch atual com a develop SEM alterar working tree, index ou refs locais.
# Usa `git merge-tree --write-tree` (git >= 2.38), que calcula o merge apenas no banco de objetos.
#
# Uso: simulate_merge.sh [branch-alvo]   (padrão: develop)
# Saída: blocos de texto estruturados para o Claude montar o relatório.
# Exit codes: 0 = sem conflitos, 1 = conflitos, 2 = erro / não foi possível simular.

set -u
TARGET_NAME="${1:-develop}"
MAX_LINES_PER_FILE=150

err() { echo "=== RESULTADO ==="; echo "status: ERRO"; echo "motivo: $*"; exit 2; }

git rev-parse --is-inside-work-tree >/dev/null 2>&1 || err "não é um repositório git."

# merge-tree --write-tree exige git >= 2.38
ver=$(git --version | sed -E 's/[^0-9]*([0-9]+)\.([0-9]+).*/\1 \2/')
read -r vmaj vmin <<<"$ver"
if [ "$vmaj" -lt 2 ] || { [ "$vmaj" -eq 2 ] && [ "$vmin" -lt 38 ]; }; then
  err "git $vmaj.$vmin é antigo demais; é necessário git >= 2.38 para simular o merge sem tocar no working tree."
fi

CURRENT=$(git symbolic-ref --short -q HEAD || echo "(detached HEAD em $(git rev-parse --short HEAD))")

# Atualiza só a referência remota (não altera código, index nem branches locais).
FETCH_STATUS="ok"
if git remote get-url origin >/dev/null 2>&1; then
  git fetch --quiet origin "$TARGET_NAME" 2>/dev/null || FETCH_STATUS="falhou (usando referência já existente)"
else
  FETCH_STATUS="sem remote origin"
fi

if git rev-parse --verify -q "refs/remotes/origin/$TARGET_NAME" >/dev/null; then
  TARGET="origin/$TARGET_NAME"
elif git rev-parse --verify -q "refs/heads/$TARGET_NAME" >/dev/null; then
  TARGET="$TARGET_NAME"
else
  err "branch '$TARGET_NAME' não encontrada (nem local nem em origin)."
fi

HEAD_SHA=$(git rev-parse HEAD)
TARGET_SHA=$(git rev-parse "$TARGET")
BASE_SHA=$(git merge-base HEAD "$TARGET" 2>/dev/null || echo "")
AHEAD=$(git rev-list --count "$TARGET..HEAD")
BEHIND=$(git rev-list --count "HEAD..$TARGET")

echo "=== CONTEXTO ==="
echo "branch_atual: $CURRENT"
echo "alvo: $TARGET ($(git rev-parse --short "$TARGET"))"
echo "fetch: $FETCH_STATUS"
echo "merge_base: ${BASE_SHA:0:7}"
echo "commits_a_frente_do_alvo: $AHEAD"
echo "commits_atras_do_alvo: $BEHIND"

DIRTY=$(git status --porcelain --untracked-files=no)
if [ -n "$DIRTY" ]; then
  echo "aviso_alteracoes_nao_commitadas: sim (a simulação considera apenas commits; estas alterações NÃO foram incluídas)"
  echo "$DIRTY" | sed 's/^/  /'
else
  echo "aviso_alteracoes_nao_commitadas: nao"
fi

[ -z "$BASE_SHA" ] && echo "aviso: as branches não têm histórico em comum."

if [ "$HEAD_SHA" = "$TARGET_SHA" ]; then
  echo "=== RESULTADO ==="
  echo "status: SEM_CONFLITOS"
  echo "observacao: a branch atual aponta para o mesmo commit que $TARGET."
  exit 0
fi

[ "$AHEAD" -eq 0 ] && echo "observacao: a branch atual não tem commits novos em relação a $TARGET (o merge não traria nada)."

echo "=== ARQUIVOS ALTERADOS NA BRANCH (desde o merge-base) ==="
[ -n "$BASE_SHA" ] && git diff --name-status "$BASE_SHA" HEAD

OUT=$(git merge-tree --write-tree --name-only --messages HEAD "$TARGET" 2>&1)
RC=$?

if [ $RC -gt 1 ]; then
  err "git merge-tree falhou: $OUT"
fi

TREE=$(echo "$OUT" | sed -n '1p')
# Linhas 2..(primeira linha vazia) = arquivos em conflito; depois vêm as mensagens.
FILES=$(echo "$OUT" | sed -n '2,${/^$/q;p}')
MSGS=$(echo "$OUT" | sed -n '/^$/,$p' | sed '/^$/d')

echo "=== RESULTADO ==="
if [ $RC -eq 0 ]; then
  echo "status: SEM_CONFLITOS"
  exit 0
fi

echo "status: CONFLITOS"
echo "total_arquivos_em_conflito: $(echo "$FILES" | sed '/^$/d' | wc -l | tr -d ' ')"
echo "=== MENSAGENS DO GIT ==="
echo "$MSGS" | grep -E '^CONFLICT' || echo "$MSGS"

echo "=== DETALHE POR ARQUIVO ==="
echo "$FILES" | sed '/^$/d' | while IFS= read -r f; do
  echo "--- arquivo: $f"
  echo "commits_na_branch_que_tocaram: $(git log --oneline "$BASE_SHA..HEAD" -- "$f" 2>/dev/null | head -5 | tr '\n' ';')"
  echo "commits_no_alvo_que_tocaram: $(git log --oneline "$BASE_SHA..$TARGET" -- "$f" 2>/dev/null | head -5 | tr '\n' ';')"
  if git cat-file -e "$TREE:$f" 2>/dev/null; then
    # O tree resultante contém o arquivo com marcadores de conflito; mostramos só os trechos conflitantes.
    git show "$TREE:$f" | awk -v max="$MAX_LINES_PER_FILE" '
      /^<<<<<<< / { inside=1; n++; print "  [trecho " n ", linha " NR "]" }
      inside { if (printed < max) { print "  " NR ": " $0; printed++ } }
      /^>>>>>>> / { inside=0 }
      END { if (n == 0) print "  (sem marcadores de conflito — provavelmente conflito de modificação/remoção, renomeação ou binário)";
            if (printed >= max) print "  (saída truncada em " max " linhas)" }'
  else
    echo "  (arquivo não existe no resultado do merge — conflito de remoção/renomeação)"
  fi
done

exit 1
