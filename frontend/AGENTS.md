# AGENTS.md

This file provides guidance to AI coding agents (e.g. Claude Code) when working with code in this repository.

## Commands

All commands run from `frontend/`:

- `npm run dev` — start the Vite dev server
- `npm run build` — type-check (`tsc -b`) then production build via Vite
- `npm run lint` — run oxlint (config in `.oxlintrc.json`)
- `npm run preview` — preview the production build

There is no test runner configured in this project.

## Architecture

This is a client-facing portal for "EcoVolt 360" (a solar-energy company), built with React 19 + TypeScript + Vite + Chakra UI v3. All UI copy is in Brazilian Portuguese.

**Routing**: `src/App.tsx` defines top-level routes with `react-router-dom` (`BrowserRouter`/`Routes`/`Route`). Currently only `/` (`LoginPage`) and `/cadastro` (`RegistrationPage`) are wired up. `Sidebar` already links to `/painel-cliente`, `/tickets`, and `/faq`, but those routes are not yet registered in `App.tsx` — check `App.tsx` before assuming a page is reachable.

**Chakra setup**: `src/main.tsx` wraps the app in `ChakraProvider` using `defaultSystem` (no custom theme extension yet). Components mix Chakra primitives (`Box`, `Flex`, `Field`, `Input`, `Button`, `Avatar`, `Checkbox`, etc.) with hand-written BEM-style CSS classes and a colocated `.css` file per component/page (imported directly). `RegistrationPage` has moved to **CSS Modules** (`*.module.css`, imported as `styles`): its page TSX uses only plain semantic HTML with no inline styling or Chakra style props, and styles are written mobile-first with `@media (min-width: 64rem)` as the main desktop breakpoint. Use this pattern when migrating other pages.

**Page structure convention**: each page lives under `src/pages/<PageName>/` with its own `.tsx` and `.css`. Multi-part pages further nest a `Components/` folder (e.g. `RegistrationPage/Components/RegistrationForms.tsx`) or split subviews into sibling files (e.g. `SupportTicket/SupportTicketList.tsx`). Shared chrome (`Header`, `Sidebar`) lives in `src/components/` and is composed into layout pages like `ClientPanel` (`src/pages/ClientPanel/ClientPanel.tsx`), which renders `Sidebar` + `Header` + a page-specific `<main>` body.

**Forms**: forms (`LoginPageForm`, `RegistrationForms`) are currently presentational only — Chakra `Field.Root`/`Input` markup with no state wiring, validation, or submit handlers yet, aside from `RegistrationForms` which uses local `useState` to step through a two-step wizard (personal info → address) via dot navigation.

**Mock data pattern**: domain data that doesn't yet come from an API is defined in colocated `*.mock.ts` files (e.g. `src/pages/SupportTicket/supportTicket.mock.ts`), which export both TypeScript types (e.g. `SupportTicket`, `TicketCategory`, `TicketStatus`) and mock instances/arrays consumed directly by page components. Enum-like label maps (`categoryLabels`, `statusLabels`) for display are defined alongside the consuming component, keyed off those mock types.

There is no state management library, API client, or auth layer yet — everything is static/local component state.
