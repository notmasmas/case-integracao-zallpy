import { Box, Button } from "@chakra-ui/react";
import {
  supportTicketsMock,
  type SupportTicket as SupportTicketData,
} from "./supportTicket.mock";
import "./SupportTicket.css";

const categoryLabels: Record<SupportTicketData["category"], string> = {
  SYSTEM_MONITORING: "Monitoramento do sistema",
  ENERGY_GENERATION: "Geração de energia",
  EQUIPMENT: "Equipamento",
  INSTALLATION: "Instalação",
  MAINTENANCE: "Manutenção",
  SCHEDULING: "Agendamento",
  PAYMENT: "Pagamento",
  PROJECT: "Projeto",
  ACCESS: "Acesso",
  OTHER: "Outro",
};

const statusLabels: Record<SupportTicketData["status"], string> = {
  under_review: "Em análise",
  in_progress: "Em andamento",
  waiting_customer: "Aguardando cliente",
  resolved: "Resolvido",
  closed: "Encerrado",
};

// o backend ja pode retornar a data e o id formatado para o front end apenas renderizar
function formatTicketDate(date: Date) {
  return new Intl.DateTimeFormat("pt-BR", {
    dateStyle: "short",
    timeStyle: "short",
  }).format(date);
}

const formatId = (id: string) => {
  return `ID-${new Date().getFullYear()}-00${id}`;
};

type SupportTicketListProps = {
  ticketCategoryFilter: string;
  searchFilter: string;
};

function SupportTicketList({
  ticketCategoryFilter,
  searchFilter,
}: SupportTicketListProps) {
  const filteredTickets = supportTicketsMock.filter((ticket) => {
    const normalizedSearch = searchFilter.trim().toLowerCase();

    const matchesCategory =
      ticketCategoryFilter === "all" ||
      ticket.category === ticketCategoryFilter;

    const matchesSearch =
      normalizedSearch === "" ||
      ticket.title.toLowerCase().includes(normalizedSearch) ||
      ticket.status.toLowerCase().includes(normalizedSearch);

    return matchesCategory && matchesSearch;
  });

  return (
    <Box className="support-ticket-list">
      <Box className="support-ticket-list__header">
        <Box> Código </Box>
        <Box> Título </Box>
        <Box> Categoria </Box>
        <Box> Status </Box>
        <Box> Ações </Box>
      </Box>
      <Box className="support-ticket-list__body">
        {filteredTickets.map((ticket) => (
          <Box className="support-ticket-item">
            <Box key={formatId(ticket.id)}>
              <Box>
                {formatId(ticket.id)}
                <Box color="var(--color-text-gray-secondary)">
                  {formatTicketDate(ticket.createdAT)}
                </Box>
              </Box>
            </Box>
            <Box>{ticket.title}</Box>
            <Box>{categoryLabels[ticket.category]}</Box>
            <Box>{statusLabels[ticket.status]}</Box>
            <Box>
              <Button className="support-ticket-item__button">
                Ver detalhes
              </Button>
            </Box>
          </Box>
        ))}
      </Box>
    </Box>
  );
}

export default SupportTicketList;
