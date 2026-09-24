import { Box, Portal, Select, createListCollection } from "@chakra-ui/react";
import { FiSearch } from "react-icons/fi";
import SupportTicketList from "./SupportTicketList";
import SupportTicketModal from "./SupportTicketModal/SupportTicketModal";
import { supportTicketsMock } from "./supportTicket.mock";
import type { RequestContext } from "../../models/request";
import "./SupportTicket.css";
import { useState } from "react";

const categoryCollection = createListCollection({
  items: [
    { label: "Todas as categorias", value: "all" },
    { label: "Monitoramento do sistema", value: "SYSTEM_MONITORING" },
    { label: "Geração de energia", value: "ENERGY_GENERATION" },
    { label: "Equipamento", value: "EQUIPMENT" },
    { label: "Instalação", value: "INSTALLATION" },
    { label: "Manutenção", value: "MAINTENANCE" },
    { label: "Agendamento", value: "SCHEDULING" },
    { label: "Pagamento", value: "PAYMENT" },
    { label: "Projeto", value: "PROJECT" },
    { label: "Acesso", value: "ACCESS" },
    { label: "Outro", value: "OTHER" },
  ],
});

type SupportTicketProps = {
  requestContext: RequestContext;
};

function SupportTicket({ requestContext }: SupportTicketProps) {
  const [selectedCategory, setSelectedCategory] = useState<string>("all");
  const [searchFilter, setSearchFilter] = useState<string>("");
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const ticketResolvedCount = supportTicketsMock.filter(
    (ticket) => ticket.status === "resolved",
  ).length;

  const onChangeCategory = (details: Select.ValueChangeDetails) => {
    setSelectedCategory(details.value[0] ?? "all");
  };
  const onChangeSearch = (event: React.ChangeEvent<HTMLInputElement>) => {
    setSearchFilter(event.target.value);
  };
  const onCloseModal = (): void => {
    setIsModalOpen(false);
  };
  const onOpenModal = (): void => {
    setIsModalOpen(true);
  };

  return (
    <Box className="support-ticket-page">
      <Box className="support-ticket-page__header">
        <h1 className="support-ticket-page__title">Meus chamados de suporte</h1>
        <span className="support-ticket-page__subtitle">
          {ticketResolvedCount} chamado(s) resolvido(s)
        </span>
      </Box>
      <Box className="support-ticket-page__filters">
        <Box className="support-ticket-page__filters__left">
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
          <Select.Root
            collection={categoryCollection}
            className="support-ticket-category"
            value={[selectedCategory]}
            onValueChange={onChangeCategory}
          >
            <Select.HiddenSelect />
            <Select.Control className="support-ticket-category__control">
              <Select.Trigger>
                <Select.ValueText />
              </Select.Trigger>
              <Select.IndicatorGroup>
                <Select.Indicator />
              </Select.IndicatorGroup>
            </Select.Control>
            <Portal>
              <Select.Positioner>
                <Select.Content className="support-ticket-category__content">
                  {categoryCollection.items.map((category) => (
                    <Select.Item item={category} key={category.value}>
                      {category.label}
                      <Select.ItemIndicator />
                    </Select.Item>
                  ))}
                </Select.Content>
              </Select.Positioner>
            </Portal>
          </Select.Root>
        </Box>
        <Box className="support-ticket-page__filters__action">
          <button className="new-ticket-button" onClick={onOpenModal}>
            + Abrir chamado
          </button>
        </Box>
      </Box>
      <SupportTicketModal
        isOpen={isModalOpen}
        onClose={onCloseModal}
        requestContext={requestContext}
      />

      <SupportTicketList
        ticketCategoryFilter={selectedCategory}
        searchFilter={searchFilter}
      />
    </Box>
  );
}

export default SupportTicket;
