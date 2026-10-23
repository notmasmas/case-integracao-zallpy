import { Box } from "@chakra-ui/react";
import TicketStatusBadge from "../../components/TicketStatusBadge";
import { categoryLabel, useAllTickets} from "../../api/tickets";

function TicketsQueue() {
  const { tickets, error, loading } = useAllTickets();
  return (
    <Box className="support-panel-tickets">
      <Box className="support-panel-tickets__header">
        <p>Sua fila de chamados</p>
        <span>
          Fila pessoal de atendimento sob sua responsabilidade técnica e
          contratual.
        </span>
      </Box>
      {error ? <p>{error}</p> : null}
      {!error && loading ? <p>Carregando chamados...</p> : null}
      {!error && !loading && tickets.length === 0 ? (
        <p>Nenhum chamado encontrado.</p>
      ) : null}
      {!error &&
        tickets.map((ticket) => (
          <Box className="support-panel-tickets__list" key={ticket.id}>
              <Box className="support-panel-tickets__list__right">
                <Box className="support-panel-tickets__list__data">
                  <p className="support-panel-ticket-id">{ticket.id}</p>
                  <p className="support-panel-ticket-category">
                    {categoryLabel(ticket.category)}
                  </p>
                  <TicketStatusBadge status={ticket.status} />
                </Box>
                <Box className="support-panel-tickets__list__details">
                  <Box className="support-panel-tickets__list__title">
                    <p>{ticket.title}</p>
                  </Box>
                  <Box className="support-panel-tickets__list__description">
                    <p>{ticket.description}</p>
                  </Box>
                </Box>
              </Box>
              <Box className="support-panel-tickets__list__left">
                <Box className="support-panel-tickets__list__responsible">
                  <p>
                    <span>Atribuído:</span> {ticket.supportName}
                  </p>
                  <Box className="support-panel-tickets__list__date">
                    <p>{ticket.createdAt}</p>
                  </Box>
                </Box>
              </Box>
            </Box>
        ))}
    </Box>
  );
}

export default TicketsQueue;
