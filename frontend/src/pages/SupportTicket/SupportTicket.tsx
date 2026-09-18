import { Box } from "@chakra-ui/react";
import SupportTicketList from "./SupportTicketList";
import "./SupportTicket.css";

function SupportTicket() {
  return (
    <Box className="support-ticket-page">
      <Box className="support-ticket-page__header">
        <h1 className="support-ticket-page__title">Meus chamados de suporte</h1>
        <span className="support-ticket-page__subtitle">
          1 chamado(s) resolvido(s)
        </span>
      </Box>
      <SupportTicketList />
    </Box>
  );
}

export default SupportTicket;
