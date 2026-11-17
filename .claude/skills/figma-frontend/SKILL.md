---
name: figma-frontend
description: Implementa no frontend (React 19 + TypeScript + Vite + Chakra UI v3, portal EcoVolt 360) uma tela, componente, modal ou ajuste visual a partir de um link do Figma colado no prompt, usando o MCP do Figma para ver o design e o `frontend/AGENTS.md` para seguir a estrutura do projeto. Use sempre que o usuário colar uma URL figma.com (design, make, board) junto com algo para construir, implementar, "deixar igual ao Figma", criar página, migrar estilo ou corrigir layout no frontend, mesmo que ele não diga "skill" nem "Figma MCP". Também serve quando ele disser só "implementa esse link" ou "faz essa tela" com um link do Figma.
---

# Figma → frontend EcoVolt 360

O objetivo é transformar o que está no Figma em código que pareça ter sido escrito por quem mantém este frontend: mesmas pastas, mesmos tokens, mesmos componentes compartilhados, mesmas regras de rota e API. O código que o Figma gera é só referência visual; copiá-lo direto produz CSS absoluto, cores soltas e componentes duplicados, que é exatamente o que este fluxo evita.

```
Figma → frontend:
- [ ] Ler frontend/AGENTS.md
- [ ] Extrair fileKey/nodeId do link
- [ ] Carregar a skill figma:figma-design-to-code
- [ ] Ver o design (sessão, contexto, screenshot)
- [ ] Mapear design → projeto (onde mora, o que reaproveita)
- [ ] Perguntar ao usuário o que não estiver claro (rota, login, regras, interações)
- [ ] Implementar
- [ ] Validar (build + lint) e comparar com o screenshot
```

## 1. Ler o `AGENTS.md` do frontend primeiro

Antes de olhar o Figma, leia `frontend/AGENTS.md` inteiro (caminho a partir da raiz do repositório). Ele é a fonte única de verdade sobre arquitetura, rotas, camada de API, padrão de páginas, formulários, toasts e mocks, e muda com frequência, então leia a versão atual a cada execução em vez de confiar no que você lembra. Se algo nesta skill contradizer o `AGENTS.md`, vale o `AGENTS.md`.

Leia também `frontend/src/index.css` para conhecer os tokens (`--color-*`, `--font-*` etc.). São eles que você vai usar no lugar das cores e fontes literais que o Figma devolve.

## 2. Extrair o link

Pegue a URL do Figma do prompt e extraia:

- `figma.com/design/:fileKey/:nome?node-id=1-2` → `fileKey`, `nodeId = "1:2"` (troque `-` por `:`)
- `figma.com/design/:fileKey/branch/:branchKey/...` → use o `branchKey` como `fileKey`
- `figma.com/make/:makeFileKey/...` → use o `makeFileKey`
- `figma.com/board/...` → é FigJam; use `get_figjam`

Se não houver link no prompt, ou ele não tiver `node-id` e o arquivo for grande, peça ao usuário o link do frame específico (no Figma: botão direito no frame → "Copy link to selection"). Implementar o arquivo inteiro sem saber qual tela ele quer gera trabalho jogado fora.

## 3. Ver o design pelo MCP do Figma

1. Carregue a skill `figma:figma-design-to-code` (é pré-requisito obrigatório do `get_design_context`) e siga as orientações dela junto com estas.
2. `create_session` com o `fileKey` e uma descrição curta da tarefa; reaproveite o `sessionId` nas chamadas seguintes.
3. `get_design_context` com `fileKey` e `nodeId`: estrutura, textos, medidas e o código de referência.
4. `get_screenshot` do mesmo nó: é com ele que você confere o resultado no fim.
5. Se o nó for grande demais ou o contexto vier truncado, use `get_metadata` para listar os filhos e chame `get_design_context` por partes.
6. `get_variable_defs` ajuda a casar as variáveis do Figma com os tokens do `index.css`.
7. Imagens e ícones: baixe com `download_assets` para `frontend/src/assets/` só o que não existir no projeto. Verifique antes o que já está em `src/assets/` e se o ícone existe em uma biblioteca já instalada (veja `frontend/package.json`).

## 4. Mapear o design para o projeto

Antes de escrever código, decida e diga ao usuário em poucas linhas:

- **Onde mora.** Página nova vai em `src/pages/<PageName>/`, modal específico em subpasta da página, componente usado por mais de uma página em `src/components/`, conforme o `AGENTS.md`. Rota nova entra em `paths` (`src/routes/routes.ts`) e em `src/App.tsx`, dentro da área de papel certa (`CUSTOMER` ou `SUPPORT`) se for autenticada.
- **O que já existe.** Procure no código antes de criar: `Header`, `Sidebar`, `PanelBase`, `TicketStatusBadge`, `TicketDetailsModal`, `toaster`, tipos e hooks de `src/api/tickets.ts`, labels de `src/models/ticketStatus.ts`. Um badge de status no Figma é o `TicketStatusBadge`, não um componente novo.
- **De onde vêm os dados.** Se existe endpoint em `src/api/`, use-o. Se não existe, crie um `*.mock.ts` colocalizado, como o `AGENTS.md` descreve, em vez de deixar dados fixos no JSX.
- **Tokens.** Para cada cor/fonte do Figma, use o token equivalente do `index.css`. Se não houver equivalente, adicione um token novo com nome no mesmo padrão e mencione isso no resumo final, em vez de espalhar hex pelo CSS.

### Quando perguntar antes de implementar

O Figma mostra a aparência, mas quase nunca mostra onde a tela se encaixa no fluxo nem o que acontece quando o usuário interage. Chutar essas respostas gera rota no lugar errado, tela acessível a quem não devia ou regra de negócio inventada, coisas caras de desfazer e difíceis de perceber numa revisão. Por isso, se depois de ler o `AGENTS.md`, o código e o Figma você ainda não souber responder algum destes pontos com segurança, **pare e pergunte ao usuário antes de escrever código**:

- **Rota:** qual é o caminho da página (ex.: `/cliente/faturas`) ou se ela é um modal/aba dentro de uma página existente.
- **Posição em relação ao login:** se é pública (antes do login, como `/` e `/cadastro`) ou autenticada (depois do login) e, nesse caso, para qual papel: `CUSTOMER`, `SUPPORT` ou ambos. Isso também define se ela aparece na `Sidebar`.
- **Navegação:** de onde o usuário chega na tela e para onde vai depois de cada ação (salvar, cancelar, voltar).
- **Regras de negócio:** validações de campos, campos obrigatórios, quem pode ver ou editar o quê, o que acontece em caso de erro, limites e formatos.
- **Interações:** o que cada botão, link, filtro ou dropdown faz; se uma ação chama a API (e qual endpoint) ou é só visual por enquanto; confirmações, toasts e estados de carregamento.
- **Dados:** se os dados vêm de um endpoint que já existe, de um endpoint que ainda vai existir (mock) ou são fixos.

Como perguntar:

- Junte todas as dúvidas numa única rodada (use `AskUserQuestion` se estiver disponível), em vez de interromper várias vezes. Cada pergunta deve fazer sentido sozinha e citar a tela ou o elemento do Figma a que se refere.
- Sempre que der, ofereça opções concretas e recomende a que parece mais provável pelo padrão do projeto (ex.: "Rota `/cliente/faturas`, autenticada para `CUSTOMER`, com item na Sidebar (recomendado)").
- Não pergunte o que o `AGENTS.md`, o código ou o próprio Figma já respondem. Perguntas desnecessárias também atrapalham.

Lacunas puramente visuais (hover, foco, estado vazio, texto de placeholder que não aparece no Figma) não precisam de pergunta: siga o padrão de telas parecidas do projeto e liste essas suposições no resumo final.

## 5. Implementar

Siga o padrão de estilo que o `AGENTS.md` define para código novo. Hoje ele é o da `RegistrationPage`, mas confira no arquivo:

- Componentes Chakra (`Box`, `Flex`, `Heading`, `Field`, `Input`, `Button`, `Icon`, com `as` para tags semânticas) estilizados por `className` de um **CSS Module** (`*.module.css` importado como `styles`), sem `style` inline.
- Dimensionamento pelo próprio Chakra (`size`, `boxSize`, `width`, `p`, `rounded`...); cores e tipografia no CSS Module, com tokens.
- Mobile-first, com `@media (min-width: 64rem)` como breakpoint de desktop. O Figma geralmente mostra só desktop; garanta que a versão estreita continue usável.
- Layout com flex/grid, não com as posições absolutas e medidas fixas do código de referência do Figma.
- Textos da interface em português do Brasil, exatamente como no Figma.
- Rotas sempre via `paths`, chamadas de API pela instância de `src/api/api.ts`, feedback ao usuário pelo `toaster`.

Se estiver alterando uma tela existente, mude só o que o design pede e mantenha o comportamento atual (validações, chamadas de API, navegação). Se o `AGENTS.md` ficar desatualizado pelo que você fez (pasta nova, padrão novo, mock novo), atualize-o, já que ele é a documentação que os próximos agentes vão ler.

## 6. Validar

1. Em `frontend/`: `npm run build` (inclui `tsc -b`) e `npm run lint`. Corrija o que quebrar.
2. Compare o resultado com o screenshot do Figma: hierarquia, espaçamentos, cores, textos, estados. Se der para rodar o app (`npm run dev`) e ver a tela, melhor ainda.
3. Termine com um resumo curto:
   - arquivos criados/alterados;
   - componentes reaproveitados e tokens novos (se houver);
   - suposições feitas e diferenças conscientes em relação ao Figma;
   - o que ficou pendente (por exemplo, endpoint que ainda não existe e está mockado).
