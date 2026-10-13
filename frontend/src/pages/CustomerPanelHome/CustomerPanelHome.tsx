import { Box } from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";
import { BsArrowRight } from "react-icons/bs";
import TicketStatusBadge from "../../components/TicketStatusBadge";
import { categoryLabel, useCustomerTickets } from "../../api/tickets";
import { paths } from "../../routes/routes";
import { customerSummaryMock } from "./customerPanelData.mock";
import "../SupportPanelHome/SupportPanelHome.css";
import "./CustomerPanelHome.css";

function CustomerPanelHome() {
  const navigate = useNavigate();
  const { tickets, error, loading } = useCustomerTickets();

  return (
    <Box className="support-panel-home">
      <Box className="customer-panel-summary">
        {/* <p className="customer-panel-summary__projects">
          Ver todos os projetos (1)
          <LuChevronDown
            className="customer-panel-summary__projects-icon"
            aria-hidden="true"
          />
        </p>
        */}
        <Box className="customer-panel-summary__cards">
          {customerSummaryMock.map((item) => {
            const Icon = item.icon;

            return (
              <Box key={item.id} className="customer-panel-summary__card">
                <Box className="customer-panel-summary__title">
                  <p>{item.title}</p>
                  <Icon
                    className="customer-panel-summary__icon"
                    aria-hidden="true"
                  />
                </Box>
                <Box className="customer-panel-summary__value-unit">
                  <p className="customer-panel-summary__value">{item.value}</p>
                  {item.unit ? (
                    <p className="customer-panel-summary__unit">{item.unit}</p>
                  ) : null}
                </Box>
                <p className="customer-panel-summary__description">
                  {item.description}
                </p>
              </Box>
            );
          })}
        </Box>
      </Box>
      <Box className="support-panel-tickets">
        <Box className="support-panel-tickets__header">
          <p>Seus chamados</p>
          <span>Acompanhe os atendimentos do seu projeto.</span>
        </Box>
        <Box className="support-panel-tickets__filters__header">
          <Box />
          <Box className="support-panel-tickets__filters__action">
            <button type="button" onClick={() => navigate(paths.tickets)}>
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

export default CustomerPanelHome;
