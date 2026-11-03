import { Box } from "@chakra-ui/react";
import { supportSummaryMock } from "./supportData.mock";
import { BsArrowRight } from "react-icons/bs";
import TicketStatusBadge from "../../components/TicketStatusBadge";
import { categoryLabel, useSupportTickets } from "../../api/tickets";
import "./SupportPanelHome.css";
import { paths } from "../../routes/routes";
import { useNavigate } from "react-router-dom";

function SupportPanelHome() {
  const { tickets, error, loading } = useSupportTickets();
const navigate = useNavigate();
  return (
    <Box className="support-panel-home">
      <Box className="support-panel-data">
        <Box className="support-panel-data__itens">
          {supportSummaryMock.map((item) => {
            const Icon = item.icon;

            return (
              <Box key={item.id} className="support-panel-home__item">
                <Box className="support-panel-home__item__title">
                  <p>{item.title}</p>
                  <Icon
                    className="support-panel-home__item__icon"
                    aria-hidden="true"
                  />
                </Box>
                <Box className="support-panel-home__item__value-unit">
                  <p className="support-panel-home__item__value">
                    {item.value}
                  </p>
                  <p className="support-panel-home__item__unit">{item.unit}</p>
                </Box>
                <p className="support-panel-home__item__description">
                  {item.description}
                </p>
              </Box>
            );
          })}
        </Box>
      </Box>
      <Box className="support-panel-tickets">
        <Box className="support-panel-tickets__header">
          <p>Sua fila de chamados</p>
          <span>
            Fila pessoal de atendimento sob sua responsabilidade técnica e
            contratual.
          </span>
        </Box>
        <Box className="support-panel-tickets__filters__header">
         
          <Box className="support-panel-tickets__filters__action">
            <button type="button" onClick={() => navigate(paths.support.tickets)}>
              Todos os chamados
            </button>
            <BsArrowRight aria-hidden="true" />
          </Box>
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
    </Box>
  );
}
export default SupportPanelHome;
