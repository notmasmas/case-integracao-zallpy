export const ticketCategory = {
  system_monitoring: "SYSTEM_MONITORING",
  energy_generation: "ENERGY_GENERATION",
  equipment: "EQUIPMENT",
  installation: "INSTALLATION",
  maintenance: "MAINTENANCE",
  scheduling: "SCHEDULING",
  payment: "PAYMENT",
  project: "PROJECT",
  access: "ACCESS",
  other: "OTHER",
} as const;

export type TicketCategory =
  (typeof ticketCategory)[keyof typeof ticketCategory];

export type TicketStatus =
  | "under_review"
  | "in_progress"
  | "waiting_customer"
  | "resolved"
  | "closed";

export type SupportTicket = {
  id: string;
  createdAT: Date;
  title: string;
  category: TicketCategory;
  status: TicketStatus;
};

export const supportTicketMock1: SupportTicket = {
  id: "ID-2026-0001",
  createdAT: new Date("2026-09-10T09:00:00"),
  title: "Monitoramento do sistema indisponível",
  category: ticketCategory.system_monitoring,
  status: "under_review",
};

export const supportTicketMock2: SupportTicket = {
  id: "ID-2026-0002",
  createdAT: new Date("2026-09-12T14:30:00"),
  title: "Dúvida sobre a geração de energia",
  category: ticketCategory.energy_generation,
  status: "waiting_customer",
};
export const supportTicketMock3: SupportTicket = {
  id: "ID-2026-0003",
  createdAT: new Date("2026-09-15T11:15:00"),
  title: "Problema com o equipamento",
  category: ticketCategory.equipment,
  status: "in_progress",
};

export const supportTicketMock4: SupportTicket = {
  id: "ID-2026-0004",
  createdAT: new Date("2026-09-18T16:45:00"),
  title: "Solicitação de manutenção",
  category: ticketCategory.maintenance,
  status: "resolved",
};

export const supportTicketMock5: SupportTicket = {
  id: "ID-2026-0005",
  createdAT: new Date("2026-09-20T10:30:00"),
  title: "Agendamento de instalação",
  category: ticketCategory.installation,
  status: "closed",
};

export const supportTicketMock6: SupportTicket = {
  id: "ID-2026-0006",
  createdAT: new Date("2026-09-22T13:00:00"),
  title: "Dúvida sobre pagamento",
  category: ticketCategory.payment,
  status: "under_review",
};

export const supportTicketsMock: SupportTicket[] = [
  supportTicketMock1,
  supportTicketMock2,
  supportTicketMock3,
  supportTicketMock4,
  supportTicketMock5,
  supportTicketMock6,
];
