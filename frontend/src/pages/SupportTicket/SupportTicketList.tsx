import { Box, Button } from "@chakra-ui/react";
import {
  categoryLabel,
  ticketMatchesSearch,
  type CustomerTicket,
} from "../../api/tickets";
import TicketStatusBadge from "../../components/TicketStatusBadge";
import "./SupportTicket.css";

type SupportTicketListProps = {
  tickets: CustomerTicket[];
  ticketCategoryFilter: string;
  searchFilter: string;
  error: string | null;
  loading: boolean;
  setSelectedTicket: (ticket: CustomerTicket) => void;
};

function SupportTicketList({
  tickets,
  ticketCategoryFilter,
  searchFilter,
  error,
  loading,
  setSelectedTicket
}: SupportTicketListProps) {
  const filteredTickets = tickets.filter((ticket) => {
    const matchesCategory =
      ticketCategoryFilter === "all" ||
      ticket.category === ticketCategoryFilter;

    return matchesCategory && ticketMatchesSearch(ticket, searchFilter);
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
        {error ? <p>{error}</p> : null}
        {!error && loading ? <p>Carregando chamados...</p> : null}
        {!error && !loading && filteredTickets.length === 0 ? (
          <p>Nenhum chamado encontrado.</p>
        ) : null}
        {!error &&
          filteredTickets.map((ticket) => (
            <Box className="support-ticket-item" key={ticket.id}>
              <Box>
                <Box>
                  {ticket.id}
                  <Box color="var(--color-text-gray-secondary)">
                    {ticket.createdAt}
                  </Box>
                </Box>
              </Box>
              <Box>{ticket.title}</Box>
              <Box>{categoryLabel(ticket.category)}</Box>
              <Box>
                <TicketStatusBadge status={ticket.status} />
              </Box>
              <Box>
                <Button className="support-ticket-item__button" onClick={() => setSelectedTicket(ticket)}>
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
