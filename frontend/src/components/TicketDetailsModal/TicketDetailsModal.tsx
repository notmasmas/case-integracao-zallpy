import { Box } from "@chakra-ui/react";
import { useEffect, useState } from "react";
import "./TicketDetailsModal.css";
import { MdClose } from "react-icons/md";
import TicketStatusBadge from "../TicketStatusBadge";
import { FiChevronLeft, FiChevronRight, FiSend } from "react-icons/fi";
import CategoryDropdown from "./CategoryDropdown";
import StatusDropdown from "./StatusDropdown";
import {
  categoryLabel,
  getTicketMessages,
  sendTicketMessage,
  updateTicketCategory,
  updateTicketStatus,
  type CustomerTicket,
  type TicketCategory,
  type TicketMessage,
} from "../../api/tickets";
import { useAuth } from "../../context/AuthContext";
import UserComment from "./UserComment/UserComment";
import type { TicketStatus } from "../../models/ticketStatus";
import { toaster } from "../ui/toaster";

type TicketDetailsModalProps = {
  ticket: CustomerTicket | null;
  onClose: () => void;
};

function TicketDetailsModal({ ticket, onClose }: TicketDetailsModalProps) {
  const { session } = useAuth();
  const ticketId = ticket?.id;
  const [messages, setMessages] = useState<TicketMessage[]>([]);
  const [messagesLoading, setMessagesLoading] = useState(false);
  const [messagesError, setMessagesError] = useState<string | null>(null);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalMessages, setTotalMessages] = useState(0);
  const [inputError, setInputError] = useState<string | null>(null);
  const [reloadKey, setReloadKey] = useState(0);
  const [activeTicketId, setActiveTicketId] = useState(ticketId);
  const [selectedCategory, setSelectedCategory] = useState<TicketCategory | null>(ticket?.category ?? null);
  const [selectedStatus, setSelectedStatus] = useState<TicketStatus>(ticket?.status ?? "pending");

  if (ticketId !== activeTicketId) {
    setActiveTicketId(ticketId);
    setPage(0);
    setMessages([]);
    setTotalPages(0);
    setTotalMessages(0);
    setMessagesError(null);
    setInputError(null);
    setSelectedCategory(ticket?.category ?? null);
    setSelectedStatus(ticket?.status ?? "pending");
  }

  useEffect(() => {
    if (!ticketId) {
      setMessagesLoading(false);
      return;
    }

    let active = true;
    setMessagesLoading(true);
    setMessagesError(null);

    getTicketMessages(ticketId, page)
      .then((data) => {
        if (active) {
          setMessages(data.messages);
          setTotalPages(data.totalPages);
          setTotalMessages(data.totalMessages);
        }
      })
      .catch(() => {
        if (active) {
          setMessages([]);
          setMessagesError("Não foi possível carregar as respostas.");
        }
      })
      .finally(() => {
        if (active) {
          setMessagesLoading(false);
        }
      });

    return () => {
      active = false;
    };
  }, [ticketId, page, reloadKey]);

  if (!ticket) {
    return null;
  }

  function verifyMessageContentLength(content: string): boolean {

    const buttonElement = document.querySelector<HTMLButtonElement>(
      ".client-ticket-modal__send-button"
    );
    if (buttonElement) {
      buttonElement.disabled = content.length > 150 || content.trim().length === 0;
    }

    if (content.length > 150) {
      setInputError("O conteúdo da mensagem não pode exceder 150 caracteres.");
      return false;
    }
    else if (content.trim().length === 0) {
      setInputError("O conteúdo da mensagem não pode estar vazio.");
      return false; 
    }
    setInputError(null);
    return true;  
  }

  function handleSendMessage() {
    const inputElement = document.querySelector<HTMLInputElement>(
      ".client-ticket-modal__send-input"
    );
    if (!inputElement || !ticketId) {
      return;
    }

    const content = inputElement.value.trim();

    if (!verifyMessageContentLength(content)) {
      return;
    }

    sendTicketMessage(ticketId, content)
      .then(() => {
        inputElement.value = "";
        setInputError(null);
        setPage(0);
        setReloadKey((current) => current + 1);
      })
      .catch(() => {
        setMessagesError("Não foi possível enviar ou atualizar as respostas.");
      });
  }

  async function handleCategoryChange(category: TicketCategory | null) {
    setSelectedCategory(category);
    if (!ticketId || !category) {
      return;
    }

    try {
      await updateTicketCategory(ticketId, category);
    } catch {
      setSelectedCategory(ticket.category);
      toaster.create({
        type: "error",
        title: "Não foi possível atualizar a categoria",
        description: "Tente novamente.",
      });
    }
  }

  async function handleStatusChange(status: TicketStatus) {
    setSelectedStatus(status);
    if (!ticketId) {
      return;
    }

    try {
      await updateTicketStatus(ticketId, status);
    } catch {
      setSelectedStatus(ticket.status);
      toaster.create({
        type: "error",
        title: "Não foi possível atualizar o status",
        description: "Tente novamente.",
      });
    }
  }

  return (
    <Box className="client-ticket-modal">
      <Box className="client-ticket-modal__header">
        <Box className="client-ticket-modal__title">
          <h3>{ticket.title}</h3>
          <button onClick={onClose}>
            <MdClose aria-hidden="true" />
          </button>
        </Box>
      </Box>
      <Box className="client-ticket-modal__main">
        <Box className="client-ticket-modal__informations">
          <Box className="client-ticket-modal__informations__item1">
            <span>Aberto por {session?.name || "Cliente"} • Chamado {ticket.id}</span>
          </Box>
          <Box className="client-ticket-modal__informations__item2">
            <Box className="client-ticket-modal__informations__item2__details">
              <h3>Atendente responsável:</h3>
              <span>{ticket.supportName || "Suporte não atribuido"}</span>
            </Box>
            <Box className="client-ticket-modal__informations__item2__details">
              <h3>Categoria:</h3>
              {session?.role === "SUPPORT" ? (
                <CategoryDropdown selectedCategory={selectedCategory} setSelectedCategory={handleCategoryChange} />
              ) : (
                <span>{selectedCategory ? categoryLabel(selectedCategory) : "Categoria não atribuída"}</span>
              )}
            </Box>
          </Box>
          <Box>
            {session?.role === "SUPPORT" ? (
              <StatusDropdown status={selectedStatus} onChange={handleStatusChange} />
            ) : (
              <TicketStatusBadge status={ticket.status} />
            )}
          </Box>
        </Box>
        <Box className="client-ticket-modal__client-description">
          <h3>Descrição do problema pelo cliente</h3>
          <Box className="client-ticket-modal__client-description__text">
            <p>{ticket.description}</p>
          </Box>
        </Box>
        <Box className="client-ticket-modal__client-comments__section">
          <h3>Respostas ({totalMessages})</h3>
          {messagesLoading && <p>Carregando respostas...</p>}
          {!messagesLoading && messagesError && <p>{messagesError}</p>}
          {!messagesLoading && !messagesError && messages.length === 0 && (
            <p>Nenhuma resposta para este chamado.</p>
          )}
          {!messagesLoading &&
            !messagesError &&
            messages.map((message) => (
              <UserComment key={message.id} message={message} />
            ))}
          {totalPages > 1 && (
            <Box className="client-ticket-modal__pagination">
              <button
                type="button"
                aria-label="Mensagens mais recentes"
                disabled={messagesLoading || page === 0}
                onClick={() => setPage((current) => current - 1)}
              >
                <FiChevronLeft aria-hidden="true" />
              </button>
              <span>
                {page + 1} / {totalPages}
              </span>
              <button
                type="button"
                aria-label="Mensagens mais antigas"
                disabled={messagesLoading || page >= totalPages - 1}
                onClick={() => setPage((current) => current + 1)}
              >
                <FiChevronRight aria-hidden="true" />
              </button>
            </Box>
          )}
        </Box>
      </Box>
      <Box className="client-ticket-modal__send-comment__section">
        <h3>Adicionar resposta no chamado</h3>
        <Box className="client-ticket-modal__send-comment__input-section">
          {
            ticket.status === "resolved" || ticket.status === "closed" ?
            "Este chamado foi encerrado e não aceita novas mensagens."
            : (
             <>
             <input className="client-ticket-modal__send-input" onChange={(e) => verifyMessageContentLength(e.target.value)} />
             <button className="client-ticket-modal__send-button" onClick={handleSendMessage}>
               <FiSend /> Enviar
             </button>
             </>
            )
          }
        </Box>
        {inputError && <p className="client-ticket-modal__send-error">{inputError}</p>}
      </Box>
    </Box>
  );
}
export default TicketDetailsModal;
