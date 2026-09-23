import { Box } from "@chakra-ui/react";
import { supportSummaryMock } from "./supportData.mock";
import {
  currentMockUser,
  supportPanelTicketsMock,
} from "./supportTickets.mock";
import { BsArrowRight } from "react-icons/bs";
import "./SupportPanelHome.css";
import { useState } from "react";

type TicketFilter = "mine" | "third_party";

function SupportPanelHome() {
  const [ticketFilter, setTicketFilter] = useState<TicketFilter>("mine");
  const visibleTickets = supportPanelTicketsMock.filter((ticket) => {
    if (ticketFilter === "mine") {
      return ticket.ownerId === currentMockUser.id;
    }

    return ticket.ownerType === "third_party";
  });

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
          <Box className="support-panel-tickets__filters">
            <Box
              className={`support-panel-filter ${
                ticketFilter === "mine" ? "active" : ""
              }`}
            >
              <button onClick={() => setTicketFilter("mine")}>
                Meus chamados
              </button>
            </Box>
            <Box
              className={`support-panel-filter ${
                ticketFilter === "third_party" ? "active" : ""
              }`}
            >
              <button onClick={() => setTicketFilter("third_party")}>
                Terceiros (
                {
                  supportPanelTicketsMock.filter(
                    (ticket) => ticket.ownerType === "third_party",
                  ).length
                }
                )
              </button>
            </Box>
          </Box>
          <Box className="support-panel-tickets__filters__action">
            <button>Todos os chamados</button>
            <BsArrowRight aria-hidden="true" />
          </Box>
        </Box>
        {visibleTickets.map((ticket) => (
          <Box className="support-panel-tickets__list" key={ticket.id}>
            <Box className="support-panel-tickets__list__right">
              <Box className="support-panel-tickets__list__data">
                <p className="support-panel-ticket-id">{ticket.id}</p>
                <p className="support-panel-ticket-category">
                  {ticket.category}
                </p>
                <p className="support-panel-ticket-status">
                  {ticket.statusLabel}
                </p>
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
                  <span>Atribuído:</span> {ticket.responsible}
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
