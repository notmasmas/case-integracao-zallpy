export type TicketStatus =
  | "pending"
  | "under_review"
  | "in_progress"
  | "waiting_customer"
  | "resolved"
  | "closed";

export const ticketStatusLabels: Record<TicketStatus, string> = {
  pending: "Pendente",
  under_review: "Em análise",
  in_progress: "Em andamento",
  waiting_customer: "Aguardando cliente",
  resolved: "Resolvido",
  closed: "Encerrado",
};
