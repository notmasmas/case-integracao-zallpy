import { Box } from "@chakra-ui/react";
import { FiSearch } from "react-icons/fi";
import SupportTicketList from "./SupportTicketList";
import "./SupportTicket.css";
import { useState } from "react";

function SupportTicket() {
  const [selectedCategory, setSelectedCategory] = useState<string>("all");
  const [searchFilter, setSearchFilter] = useState<string>("");

  const onChangeCategory = (event: React.ChangeEvent<HTMLSelectElement>) => {
    setSelectedCategory(event.target.value);
  };
  const onChangeSearch = (event: React.ChangeEvent<HTMLInputElement>) => {
    setSearchFilter(event.target.value);
  };

  return (
    <Box className="support-ticket-page">
      <Box className="support-ticket-page__header">
        <h1 className="support-ticket-page__title">Meus chamados de suporte</h1>
        <span className="support-ticket-page__subtitle">
          1 chamado(s) resolvido(s)
        </span>
      </Box>
      <Box className="support-ticket-page__filters">
        <select onChange={onChangeCategory}>
          <option value="all">Todas as categorias</option>
          <option value="SYSTEM_MONITORING">Monitoramento do sistema</option>
          <option value="ENERGY_GENERATION">Geração de energia</option>
          <option value="EQUIPMENT">Equipamento</option>
          <option value="INSTALLATION">Instalação</option>
          <option value="MAINTENANCE">Manutenção</option>
          <option value="SCHEDULING">Agendamento</option>
          <option value="PAYMENT">Pagamento</option>
          <option value="PROJECT">Projeto</option>
          <option value="ACCESS">Acesso</option>
          <option value="OTHER">Outro</option>
        </select>
        <Box className="support-ticket-search">
          <input
            onChange={onChangeSearch}
            type="text"
            placeholder="Pesquisar por título ou status"
          />
          <FiSearch
            className="support-ticket-search__icon"
            aria-hidden="true"
          />
        </Box>
      </Box>

      <SupportTicketList
        ticketCategoryFilter={selectedCategory}
        searchFilter={searchFilter}
      />
    </Box>
  );
}

export default SupportTicket;
