# Ponto de entrada para agentes de IA

Este arquivo é o ponto de entrada para qualquer agente de IA (Claude Code, Codex, Cursor, Copilot, Gemini etc.) que trabalhe neste repositório.

**Leia `AGENTS.md` antes de fazer qualquer alteração ou tarefa.** Ele é a fonte única com as instruções do projeto: comandos, arquitetura, convenções de páginas, formulários e dados mock.

## Regras

- Não duplique instruções aqui: toda orientação nova vai em `AGENTS.md`.
- Se o seu agente procura outro arquivo (por exemplo `.cursorrules` ou `.github/copilot-instructions.md`), faça esse arquivo apontar para `AGENTS.md` em vez de copiar o conteúdo.
- Em caso de conflito entre este arquivo e `AGENTS.md`, vale `AGENTS.md`.

<!-- A linha abaixo faz o Claude Code carregar AGENTS.md automaticamente. -->
@AGENTS.md
