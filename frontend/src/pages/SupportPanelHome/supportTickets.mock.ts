export type SupportPanelTicketStatus =
  | "in_progress"
  | "waiting_customer"
  | "resolved";

export type SupportPanelTicketOwnerType = "current_user" | "third_party";

export const currentMockUser = {
  id: "support-user-01",
  name: "João da Silva",
};

export type SupportPanelTicket = {
  id: string;
  category: string;
  status: SupportPanelTicketStatus;
  statusLabel: string;
  title: string;
  description: string;
  responsible: string;
  createdAt: string;
  ownerType: SupportPanelTicketOwnerType;
  ownerId: string;
};

export const supportPanelTicketsMock: SupportPanelTicket[] = [
  {
    id: "ID-2026-0015",
    category: "Painel & Inversor",
    status: "in_progress",
    statusLabel: "EM ANDAMENTO",
    title: "Inversor piscando luz vermelha",
    description:
      "Após a tempestade com raios ontem à noite, a caixinha do painel parou de atualizar.",
    responsible: "João da Silva",
    createdAt: "10/06/2024 10:53",
    ownerType: "current_user",
    ownerId: currentMockUser.id,
  },
  {
    id: "ID-2026-0014",
    category: "Monitoramento",
    status: "waiting_customer",
    statusLabel: "AGUARDANDO CLIENTE",
    title: "Dados de geração desatualizados",
    description:
      "O painel de monitoramento não apresenta os dados de geração desde ontem.",
    responsible: "Mariana Costa",
    createdAt: "09/06/2024 15:20",
    ownerType: "third_party",
    ownerId: "client-user-02",
  },
  {
    id: "ID-2026-0012",
    category: "Equipamento",
    status: "resolved",
    statusLabel: "RESOLVIDO",
    title: "Alerta de comunicação do inversor",
    description:
      "A comunicação foi restabelecida após a atualização da configuração do equipamento.",
    responsible: "Carlos Mendes",
    createdAt: "07/06/2024 09:14",
    ownerType: "third_party",
    ownerId: "client-user-03",
  },
];
