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
  return (
    <Box className="support-ticket-list">
      <Box className="support-ticket-list__header">
        <Box> Código </Box>
        <Box> Título </Box>
        <Box> Categoria </Box>
        <Box> Status </Box>
        <Box> Ações </Box>
      </Box>
    </Box>
  );
}

export default SupportTicketList;
