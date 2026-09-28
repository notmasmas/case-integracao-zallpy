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

**Routing**: `src/App.tsx` defines top-level routes with `react-router-dom` (`BrowserRouter`/`Routes`/`Route`). Currently `/` (`LoginPage`), `/painel-cliente` (`PanelBase`) and `/cadastro` (`RegistrationPage`) are wired up. `Sidebar` also links to routes such as `/tickets` and `/faq` that are not registered yet — check `App.tsx` before assuming a page is reachable.

**Chakra setup**: `src/main.tsx` wraps the app in `ChakraProvider` using `defaultSystem` (no custom theme extension yet). Components mix Chakra primitives (`Box`, `Flex`, `Field`, `Input`, `Button`, `Avatar`, `Checkbox`, etc.) with hand-written BEM-style CSS classes and a colocated `.css` file per component/page (imported directly). `RegistrationPage` has moved to **CSS Modules** (`*.module.css`, imported as `styles`): its TSX uses Chakra components (`Box`, `Flex`, `Heading`, `Image`, `Field`, `Input`, `Button`, `Icon`, with `as` for semantic tags) styled via `className`, with no inline `style`. Sizing of Chakra components comes from Chakra itself (recipe props like `size`, plus sizing props such as `boxSize`, `width`, `minW`, `p`, `rounded`, e.g. the step dots and the "Cadastrar" button), while colors and typography stay in the CSS Module. Chakra recipes live in CSS cascade layers, so unlayered CSS Module rules override them. Styles are written mobile-first with `@media (min-width: 64rem)` as the main desktop breakpoint. Use this pattern when migrating other pages.

**Page structure convention**: each page lives under `src/pages/<PageName>/` with its own `.tsx` and `.css`. Multi-part pages further nest a `Components/` folder (e.g. `RegistrationPage/Components/RegistrationForms.tsx`) or split subviews into sibling files (e.g. `SupportTicket/SupportTicketList.tsx`). Shared chrome (`Header`, `Sidebar`) lives in `src/components/` and is composed into layout pages like `PanelBase` (`src/pages/PanelBase/PanelBase.tsx`), which renders `Sidebar` + `Header` + a page-specific body (e.g. `SupportPanelHome`).

**Forms**: `LoginPageForm` (Chakra `Field.Root`/`Input` markup) has a minimal `handleSubmit` that only shows an error toast when email or password is empty. `RegistrationForms` is a `chakra.form` (`noValidate`) with **controlled** Chakra `Field`/`Input`/`Button` styled via CSS Modules, and a two-step wizard (personal info → address). Both steps stay mounted (the inactive one uses `hidden`).
- Validation is hand-rolled (no form library) in `Components/registrationValidation.ts`: pure validators, `validateStep`, and every user-facing message in `validationMessages`, so change texts there. Fields are checked on blur and re-checked on change once they show an error. Advancing with "→" or the dots requires step 1 to be valid, and submit validates both steps, jumping back to step 1 if it has errors. Errors render through `Field.Root invalid` + `Field.ErrorText`, colored with the `--color-warning-*` tokens.
- Password rule: 8–25 characters with at least one letter and one number.
- CEP lookup is in `Components/viaCep.ts` (ViaCEP, `AbortController` cancels stale requests). Once 8 digits are typed it fills Estado/Cidade/Bairro/Rua with whatever is returned, and those fields stay editable. "CEP não encontrado" blocks submit, while a failed lookup doesn't, since the user can fill the address manually.
- `handleSubmit` builds a nested `RegistrationFormData` and `console.log`s it (no backend call yet), then shows a success toast and navigates to the login (`/`).

**Toasts**: `src/components/ui/toaster.tsx` exports both the Chakra `toaster` instance and the `<Toaster />` component, styled with the project's success/warning tokens (it renders title + description only, no close button). `<Toaster />` is rendered once in `src/main.tsx`, outside the router, so toasts survive route changes. Trigger them with `toaster.create({ type: "success" | "error", title, description })`; `LoginPageForm` and `RegistrationForms` both use it.

**Mock data pattern**: domain data that doesn't yet come from an API is defined in colocated `*.mock.ts` files (e.g. `src/pages/SupportTicket/supportTicket.mock.ts`), which export both TypeScript types (e.g. `SupportTicket`, `TicketCategory`, `TicketStatus`) and mock instances/arrays consumed directly by page components. Enum-like label maps (`categoryLabels`, `statusLabels`) for display are defined alongside the consuming component, keyed off those mock types.

There is no state management library, API client, or auth layer yet — everything is static/local component state.
