import { Box, Button, Input } from "@chakra-ui/react";
import { useState } from "react";
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

function formatTicketDate(date: Date) {
  return new Intl.DateTimeFormat("pt-BR", {
    dateStyle: "short",
    timeStyle: "short",
  }).format(date);
}

function SupportTicketList() {
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedCategory, setSelectedCategory] = useState("all");

  const filteredTickets = supportTicketsMock.filter((ticket) => {
    const normalizedSearchTerm = searchTerm.trim().toLocaleLowerCase("pt-BR");
    const searchableText = [
      ticket.id,
      ticket.title,
      categoryLabels[ticket.category],
      statusLabels[ticket.status],
    ]
      .join(" ")
      .toLocaleLowerCase("pt-BR");

    const matchesSearch = searchableText.includes(normalizedSearchTerm);
    const matchesCategory =
      selectedCategory === "all" || ticket.category === selectedCategory;

    return matchesSearch && matchesCategory;
  });

  return (
    <Box className="support-ticket-list-wrapper">
      <Box className="support-ticket-list__filters" as="form" role="search">
        <Box className="support-ticket-search">
          <Input
            aria-label="Buscar chamados"
            className="support-ticket-search__input"
            onChange={(event) => setSearchTerm(event.target.value)}
            placeholder="Buscar"
            type="search"
            value={searchTerm}
          />
          <span className="support-ticket-search__icon" aria-hidden="true" />
        </Box>

        <Box className="support-ticket-category-filter">
          <label className="visually-hidden" htmlFor="ticket-category">
            Filtrar por categoria
          </label>
          <select
            className="support-ticket-category-filter__select"
            id="ticket-category"
            onChange={(event) => setSelectedCategory(event.target.value)}
            value={selectedCategory}
          >
            <option value="all">Categorias</option>
            {Object.entries(categoryLabels).map(([value, label]) => (
              <option key={value} value={value}>
                {label}
              </option>
            ))}
          </select>
        </Box>

        <Button className="support-ticket-open-button" type="button">
          <span aria-hidden="true">+</span>
          Abrir chamado
        </Button>
      </Box>

      <Box
        className="support-ticket-list"
        role="table"
        aria-label="Chamados de suporte"
      >
        <Box className="support-ticket-list__header" role="row">
          <span role="columnheader">CÓD/DATA</span>
          <span role="columnheader">TÍTULO DO CHAMADO</span>
          <span role="columnheader">CATEGORIA</span>
          <span role="columnheader">STATUS</span>
          <span role="columnheader" aria-label="Ações" />
        </Box>

        <Box className="support-ticket-list__body">
          {filteredTickets.map((ticket) => (
            <Box className="support-ticket-item" key={ticket.id} role="row">
              <Box className="support-ticket-item__code" role="cell">
                <strong>{ticket.id}</strong>
                <time dateTime={ticket.createdAT.toISOString()}>
                  {formatTicketDate(ticket.createdAT)}
                </time>
              </Box>
              <Box className="support-ticket-item__title" role="cell">
                {ticket.title}
              </Box>
              <Box className="support-ticket-item__category" role="cell">
                {categoryLabels[ticket.category]}
              </Box>
              <Box className="support-ticket-item__status" role="cell">
                <span
                  className={`support-ticket-status support-ticket-status--${ticket.status}`}
                >
                  {statusLabels[ticket.status]}
                </span>
              </Box>
              <Box className="support-ticket-item__action" role="cell">
                <Button
                  className="support-ticket-details-button"
                  variant="plain"
                >
                  Ver detalhes
                </Button>
              </Box>
            </Box>
          ))}
        </Box>
      </Box>
    </Box>
  );
}

export default SupportTicketList;
