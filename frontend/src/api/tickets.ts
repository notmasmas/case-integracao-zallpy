import { useEffect, useState } from "react";
import api from "./api";
import {
  ticketStatusLabels,
  type TicketStatus,
} from "../models/ticketStatus";

export const ticketCategories = [
  "SYSTEM_MONITORING",
  "EQUIPMENT",
  "INSTALLATION",
  "MAINTENANCE",
  "SCHEDULING",
  "PAYMENT",
  "PROJECT",
  "ACCESS",
  "OTHER",
] as const;

export type TicketCategory = (typeof ticketCategories)[number];

export const categoryLabels: Record<TicketCategory, string> = {
  SYSTEM_MONITORING: "Monitoramento do sistema",
  EQUIPMENT: "Equipamento",
  INSTALLATION: "Instalação",
  MAINTENANCE: "Manutenção",
  SCHEDULING: "Agendamento",
  PAYMENT: "Pagamento",
  PROJECT: "Projeto",
  ACCESS: "Acesso",
  OTHER: "Outro",
};

export const statusByApiValue: Record<string, TicketStatus> = {
  PENDING: "pending",
  UNDER_REVIEW: "under_review",
  IN_PROGRESS: "in_progress",
  WAITING_CUSTOMER: "waiting_customer",
  RESOLVED: "resolved",
  CLOSED: "closed",
};

export type CustomerTicket = {
  id: string;
  title: string;
  description: string;
  category: TicketCategory | null;
  status: TicketStatus;
  createdAt: string;
  supportName: string;
  evaluate: number | null;
  evaluateComment: string | null;
  isEvaluated: boolean;
};

type TicketSummaryResponse = {
  id: string;
  title: string;
  description: string;
  category: string | null;
  status: string;
  createdAt: string;
  supportName: string;
  evaluate: number | null;
  evaluateComment: string | null;
  isEvaluated: boolean;
};

export type TicketMessage = {
  id: string;
  senderUserId: string;
  role: string;
  content: string;
  createdAt: string;
};

export type TicketMessagesPage = {
  messages: TicketMessage[];
  page: number;
  totalPages: number;
  totalMessages: number;
};



export function categoryLabel(category: TicketCategory | null): string {
  if (!category || !categoryLabels[category]) {
    return "Sem categoria";
  }
  return categoryLabels[category];
}

function isTicketCategory(value: string | null): value is TicketCategory {
  return (
    value !== null &&
    (ticketCategories as readonly string[]).includes(value)
  );
}

function formatTicketDate(value: string): string {
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return value;
  }
  return new Intl.DateTimeFormat("pt-BR", {
    dateStyle: "short",
    timeStyle: "short",
  }).format(date);
}

function toCustomerTicket(ticket: TicketSummaryResponse): CustomerTicket {
  return {
    id: ticket.id,
    title: ticket.title,
    description: ticket.description,
    category: isTicketCategory(ticket.category) ? ticket.category : null,
    status: statusByApiValue[ticket.status] ?? "pending",
    createdAt: formatTicketDate(ticket.createdAt),
    supportName: ticket.supportName?.trim() ? ticket.supportName : "Não atribuído",
    evaluate: ticket.evaluate,
    evaluateComment: ticket.evaluateComment,
    isEvaluated: ticket.isEvaluated,
  };
}

export async function listTickets(): Promise<CustomerTicket[]> {
  const response = await api.get<TicketSummaryResponse[]>("support/tickets/all");
  return response.data.map(toCustomerTicket);
}

export async function listCustomerTickets(): Promise<CustomerTicket[]> {
  const response = await api.get<TicketSummaryResponse[]>("/tickets");
  return response.data.map(toCustomerTicket);
}

export async function listSupportTickets(): Promise<CustomerTicket[]> {
  const response = await api.get<TicketSummaryResponse[]>("/support/tickets");
  return response.data.map(toCustomerTicket);
}


export async function evaluateTicket(
  ticketId: string,
  evaluation: number,
  comment: string | null,
): Promise<void> {
  await api.patch(`/tickets/${ticketId}/evaluation`, {
    evaluation,
    comment,
  });
}

export async function updateTicketStatus(ticketId: string, status: TicketStatus): Promise<void> {
  const statusByApiKey: Record<TicketStatus, string> = {
    pending: "PENDING",
    under_review: "UNDER_REVIEW",
    in_progress: "IN_PROGRESS",
    waiting_customer: "WAITING_CUSTOMER",
    resolved: "RESOLVED",
    closed: "CLOSED",
  };

  const apiStatus = statusByApiKey[status];
  await api.patch(`/support/tickets/${encodeURIComponent(ticketId)}/status`, { status: apiStatus });
}

export async function updateTicketCategory(ticketId: string, category: TicketCategory): Promise <void> {
    await api.patch(`/tickets/${encodeURIComponent(ticketId)}/category`, category);
}

export async function getTicketMessages(
  ticketId: string,
  page: number,
): Promise<TicketMessagesPage> {
  const response = await api.get<TicketMessagesPage>(
    `/tickets/${encodeURIComponent(ticketId)}/messages`,
    { params: { page } },
  );
  return response.data;
}

export async function sendTicketMessage(ticketId: string, content: string): Promise<void> {
  const url = `/tickets/${encodeURIComponent(ticketId)}/messages`;
  await api.post(url, { content });
}



export function useCustomerTickets() {
  const [tickets, setTickets] = useState<CustomerTicket[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let active = true;

    listCustomerTickets()
      .then((data) => {
        if (!active) {
          return;
        }
        setTickets(data);
        setError(null);
      })
      .catch(() => {
        if (!active) {
          return;
        }
        setTickets([]);
        setError("Não foi possível carregar os chamados.");
      })
      .finally(() => {
        if (active) {
          setLoading(false);
        }
      });

    return () => {
      active = false;
    };
  }, []);

  const markTicketEvaluated = (
    ticketId: string,
    evaluation: number,
    comment: string | null,
  ) => {
    setTickets((current) =>
      current.map((ticket) =>
        ticket.id === ticketId
          ? {
              ...ticket,
              evaluate: evaluation,
              evaluateComment: comment,
              isEvaluated: true,
            }
          : ticket,
      ),
    );
  };

  return { tickets, error, loading, markTicketEvaluated };
}

export function useAllTickets() {
  const [tickets, setTickets] = useState<CustomerTicket[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let active = true;

    listTickets()
      .then((data) => {
        if (!active) {
          return;
        }
        setTickets(data);
        setError(null);
      })
      .catch(() => {
        if (!active) {
          return;
        }
        setTickets([]);
        setError("Não foi possível carregar os chamados.");
      })
      .finally(() => {
        if (active) {
          setLoading(false);
        }
      });

    return () => {
      active = false;
    };
  }, []);

  return { tickets, error, loading };
}

export function useSupportTickets() {
  const [tickets, setTickets] = useState<CustomerTicket[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let active = true;

    listSupportTickets()
      .then((data) => {
        if (!active) {
          return;
        }
        setTickets(data);
        setError(null);
      })
      .catch(() => {
        if (!active) {
          return;
        }
        setTickets([]);
        setError("Não foi possível carregar os chamados.");
      })
      .finally(() => {
        if (active) {
          setLoading(false);
        }
      });

    return () => {
      active = false;
    };
  }, []);

  return { tickets, error, loading };
}

export function ticketMatchesSearch(ticket: CustomerTicket, search: string): boolean {
  const normalizedSearch = search.trim().toLowerCase();
  if (normalizedSearch === "") {
    return true;
  }

  return (
    ticket.title.toLowerCase().includes(normalizedSearch) ||
    ticket.status.toLowerCase().includes(normalizedSearch) ||
    ticketStatusLabels[ticket.status].toLowerCase().includes(normalizedSearch)
  );
}
