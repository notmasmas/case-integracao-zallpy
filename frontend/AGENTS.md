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

**Chakra setup**: `src/main.tsx` wraps the app in `ChakraProvider` using `defaultSystem` (no custom theme extension yet). Components mix Chakra primitives (`Box`, `Flex`, `Field`, `Input`, `Button`, `Avatar`, `Checkbox`, etc.) with hand-written BEM-style CSS classes and a colocated `.css` file per component/page (imported directly). `RegistrationPage` has moved to **CSS Modules** (`*.module.css`, imported as `styles`): its TSX uses Chakra components (`Box`, `Flex`, `Heading`, `Image`, `Field`, `Input`, `Button`, `Icon`, with `as` for semantic tags) styled via `className`, with no inline `style`. Sizing of Chakra components comes from Chakra itself (recipe props like `size`, plus sizing props such as `boxSize`, `width`, `minW`, `p`, `rounded`, e.g. the step dots and the "Cadastrar" button), while colors and typography stay in the CSS Module. Chakra recipes live in CSS cascade layers, so unlayered CSS Module rules override them. Styles are written mobile-first with `@media (min-width: 64rem)` as the main desktop breakpoint. Use this pattern when migrating other pages.

**Page structure convention**: each page lives under `src/pages/<PageName>/` with its own `.tsx` and `.css`. Multi-part pages further nest a `Components/` folder (e.g. `RegistrationPage/Components/RegistrationForms.tsx`) or split subviews into sibling files (e.g. `SupportTicket/SupportTicketList.tsx`). Shared chrome (`Header`, `Sidebar`) lives in `src/components/` and is composed into layout pages like `ClientPanel` (`src/pages/ClientPanel/ClientPanel.tsx`), which renders `Sidebar` + `Header` + a page-specific `<main>` body.

**Forms**: `LoginPageForm` is presentational only (Chakra `Field.Root`/`Input` markup, no state, validation, or submit handler). `RegistrationForms` is a `chakra.form` with uncontrolled Chakra `Field`/`Input`/`Button` styled via CSS Modules; local `useState` steps through a two-step wizard (personal info → address) via dot navigation and a "→" button (`type="button"`). Both steps stay mounted (the inactive one uses `hidden`) so every input is still in the form on submit. The "Cadastrar" button is `type="submit"`, and `handleSubmit` reads `FormData` into a typed `RegistrationFormData` object. There is no validation or backend call yet.

**Mock data pattern**: domain data that doesn't yet come from an API is defined in colocated `*.mock.ts` files (e.g. `src/pages/SupportTicket/supportTicket.mock.ts`), which export both TypeScript types (e.g. `SupportTicket`, `TicketCategory`, `TicketStatus`) and mock instances/arrays consumed directly by page components. Enum-like label maps (`categoryLabels`, `statusLabels`) for display are defined alongside the consuming component, keyed off those mock types.

There is no state management library, API client, or auth layer yet — everything is static/local component state.
