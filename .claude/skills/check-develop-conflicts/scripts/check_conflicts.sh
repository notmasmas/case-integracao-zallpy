#!/usr/bin/env bash
# Compara a branch atual com a branch base (padrão: develop) e simula o merge
# com `git merge-tree --write-tree` para apontar conflitos.
#
# É somente leitura: não faz checkout, merge, rebase nem altera o working tree.
# O único efeito colateral é o `git fetch` da branch base (desligue com --no-fetch).
#
# Uso: check_conflicts.sh [branch-base] [--no-fetch]
# Requer git >= 2.38.

set -uo pipefail

BASE_BRANCH="develop"
FETCH=1
MAX_LOG=30
MAX_CONFLICT_LINES=200

for arg in "$@"; do
  case "$arg" in
    --no-fetch) FETCH=0 ;;
    -h|--help)
      sed -n '2,10p' "$0"
      exit 0
      ;;
    *) BASE_BRANCH="$arg" ;;
  esac
done

section() { printf '\n== %s ==\n' "$1"; }

if ! git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
  echo "ERRO: o diretório atual não é um repositório git."
  exit 2
fi

# merge-tree --write-tree só existe a partir do git 2.38.
git_version=$(git version | sed -E 's/^git version ([0-9]+)\.([0-9]+).*/\1 \2/')
read -r git_major git_minor <<<"$git_version"
if [ "$git_major" -lt 2 ] || { [ "$git_major" -eq 2 ] && [ "$git_minor" -lt 38 ]; }; then
  echo "ERRO: é preciso git >= 2.38 (encontrado: $(git version))."
  exit 2
fi

CURRENT=$(git symbolic-ref --quiet --short HEAD || true)
if [ -z "$CURRENT" ]; then
  CURRENT="(HEAD destacado em $(git rev-parse --short HEAD))"
fi

# Atualiza a referência remota da base, se houver um remote "origin".
FETCH_NOTE=""
if [ "$FETCH" -eq 1 ] && git remote | grep -qx origin; then
  if ! git fetch --quiet origin "$BASE_BRANCH" 2>/dev/null; then
    FETCH_NOTE="AVISO: não foi possível atualizar origin/$BASE_BRANCH (sem rede ou branch inexistente no remoto); usando a última cópia local."
  fi
elif [ "$FETCH" -eq 0 ]; then
  FETCH_NOTE="AVISO: fetch desligado (--no-fetch); a base pode estar desatualizada."
fi

# Prefere a versão remota da base, que é o que vai para o merge/PR.
if git rev-parse --verify --quiet "refs/remotes/origin/$BASE_BRANCH" >/dev/null; then
  BASE_REF="origin/$BASE_BRANCH"
elif git rev-parse --verify --quiet "refs/heads/$BASE_BRANCH" >/dev/null; then
  BASE_REF="$BASE_BRANCH"
else
  echo "ERRO: a branch base '$BASE_BRANCH' não existe localmente nem em origin."
  exit 2
fi

if [ "$(git rev-parse HEAD)" = "$(git rev-parse "$BASE_REF")" ]; then
  echo "A branch atual ($CURRENT) está no mesmo commit que $BASE_REF. Nada a comparar."
  exit 0
fi

MERGE_BASE=$(git merge-base HEAD "$BASE_REF" || true)
if [ -z "$MERGE_BASE" ]; then
  echo "ERRO: $CURRENT e $BASE_REF não têm histórico em comum."
  exit 2
fi

read -r BEHIND AHEAD <<<"$(git rev-list --left-right --count "$BASE_REF...HEAD")"

section "RESUMO"
echo "Branch atual: $CURRENT"
echo "Base: $BASE_REF"
echo "Ponto de divergência (merge-base): $(git log -1 --format='%h %ad %s' --date=short "$MERGE_BASE")"
echo "Commits só na sua branch (à frente): $AHEAD"
echo "Commits novos na base (atrás): $BEHIND"
[ -n "$FETCH_NOTE" ] && echo "$FETCH_NOTE"

# A branch local de mesmo nome pode estar diferente da remota.
if [ "$BASE_REF" = "origin/$BASE_BRANCH" ] &&
  git rev-parse --verify --quiet "refs/heads/$BASE_BRANCH" >/dev/null &&
  [ "$(git rev-parse "$BASE_BRANCH")" != "$(git rev-parse "$BASE_REF")" ]; then
  echo "AVISO: a branch local '$BASE_BRANCH' está diferente de $BASE_REF; a comparação usa $BASE_REF."
fi

DIRTY=$(git status --porcelain)
if [ -n "$DIRTY" ]; then
  section "ALTERAÇÕES NÃO COMMITADAS (fora da simulação)"
  echo "$DIRTY"
fi

section "COMMITS SÓ NA SUA BRANCH (até $MAX_LOG)"
git log --format='%h %ad %an: %s' --date=short -n "$MAX_LOG" "$BASE_REF..HEAD"

section "COMMITS NOVOS NA BASE DESDE A DIVERGÊNCIA (até $MAX_LOG)"
git log --format='%h %ad %an: %s' --date=short -n "$MAX_LOG" "HEAD..$BASE_REF"

section "ARQUIVOS ALTERADOS NA SUA BRANCH"
git diff --stat=120 "$BASE_REF...HEAD"

section "ARQUIVOS QUE A SUA BRANCH REMOVE"
# Remoções passam despercebidas no --stat e muitas vezes vêm de um merge mal
# resolvido; se a branch entrar na base, esses arquivos somem de lá.
DELETED=$(git diff --diff-filter=D --name-only "$BASE_REF...HEAD")
if [ -n "$DELETED" ]; then echo "$DELETED"; else echo "(nenhum)"; fi

section "ARQUIVOS ALTERADOS NA BASE"
git diff --name-status "HEAD...$BASE_REF"

section "ARQUIVOS ALTERADOS DOS DOIS LADOS"
BOTH=$(comm -12 \
  <(git diff --name-only "$BASE_REF...HEAD" | sort) \
  <(git diff --name-only "HEAD...$BASE_REF" | sort))
if [ -n "$BOTH" ]; then echo "$BOTH"; else echo "(nenhum)"; fi

section "SIMULAÇÃO DE MERGE ($BASE_REF -> $CURRENT)"
# HEAD primeiro: nos marcadores, "HEAD" é a sua branch e "$BASE_REF" é a base.
MERGE_OUT=$(git merge-tree --write-tree --name-only --messages HEAD "$BASE_REF")
MERGE_STATUS=$?

if [ "$MERGE_STATUS" -eq 0 ]; then
  echo "SEM CONFLITOS: o merge de $BASE_REF na sua branch seria automático."
  exit 0
elif [ "$MERGE_STATUS" -ne 1 ]; then
  echo "ERRO: git merge-tree falhou (código $MERGE_STATUS)."
  echo "$MERGE_OUT"
  exit 2
fi

# Saída do merge-tree: 1ª linha = tree resultante; depois os arquivos em
# conflito; uma linha em branco; depois as mensagens informativas.
RESULT_TREE=$(printf '%s\n' "$MERGE_OUT" | sed -n '1p')
CONFLICT_FILES=$(printf '%s\n' "$MERGE_OUT" | sed -n '2,/^$/p' | sed '/^$/d')
MESSAGES=$(printf '%s\n' "$MERGE_OUT" | sed -n '/^$/,$p' | sed '1d')

echo "CONFLITOS ENCONTRADOS em $(printf '%s\n' "$CONFLICT_FILES" | grep -c .) arquivo(s):"
printf '%s\n' "$CONFLICT_FILES"

section "MENSAGENS DO GIT"
printf '%s\n' "$MESSAGES"

section "TRECHOS EM CONFLITO"
while IFS= read -r file; do
  [ -z "$file" ] && continue
  echo "--- $file"
  if ! git cat-file -e "$RESULT_TREE:$file" 2>/dev/null; then
    echo "(o arquivo não existe no resultado do merge; veja as MENSAGENS DO GIT, ex.: modify/delete ou rename)"
    continue
  fi
  CONTENT=$(git cat-file -p "$RESULT_TREE:$file")
  if ! printf '%s' "$CONTENT" | grep -qI .; then
    echo "(arquivo binário; conflito precisa ser resolvido escolhendo uma das versões)"
    continue
  fi
  # Imprime cada bloco <<<<<<< ... >>>>>>> com número de linha e 3 linhas de contexto.
  printf '%s\n' "$CONTENT" | awk -v max="$MAX_CONFLICT_LINES" '
    { line[NR] = $0 }
    /^<<<<<<< / { start[++n] = NR }
    /^>>>>>>> / { stop[n] = NR }
    END {
      if (n == 0) { print "(sem marcadores de texto; veja as MENSAGENS DO GIT)"; exit }
      printed = 0
      for (i = 1; i <= n; i++) {
        from = start[i] - 3; if (from < 1) from = 1
        to = stop[i] + 3; if (to > NR) to = NR
        printf "@@ linhas %d-%d\n", from, to
        for (j = from; j <= to; j++) {
          if (printed >= max) { print "... (trecho truncado)"; exit }
          printf "%5d | %s\n", j, line[j]
          printed++
        }
      }
    }'
done <<<"$CONFLICT_FILES"

exit 1
