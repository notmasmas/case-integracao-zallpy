import { toaster } from "./toaster";

// O id fixo evita toasts repetidos quando várias requisições recebem 403 juntas.
// O microtask é necessário porque o toaster usa flushSync, proibido dentro de useEffect.
export function showAccessDeniedToast() {
  queueMicrotask(() => {
    toaster.create({
      id: "access-denied",
      title: "Acesso negado",
      description: "Você não tem permissão para acessar este recurso.",
      type: "error",
    });
  });
}
