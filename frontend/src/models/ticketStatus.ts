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

export type Ticket = {
  id: string;
  title: string;
  description: string;
  status: TicketStatus;
  createdAt: string;
  updatedAt: string;
};

export type TicketDetails = {
  id: string;
  ticket: Ticket;
  project: Project;
  owner: User;
  assignee: User | null;
  category: Category | null;
  status: TicketStatus;
  createdAt: string;
  updatedAt: string;
};

export type Project = {
  id: string;
  name: string;
  description: string;
  createdAt: string;
  updatedAt: string;
};

export type Category = {
  id: string;
  name: string;
  description: string;
  createdAt: string;
  updatedAt: string;
};

export type User = {
  id: string;
  name: string;
  email: string;
  createdAt: string;
  updatedAt: string;
};