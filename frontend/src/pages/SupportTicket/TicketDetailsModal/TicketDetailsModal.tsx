import { Box } from "@chakra-ui/react";
import { useEffect, useRef, useState, type UIEvent } from "react";
import "./TicketDetailsModal.css";
import { MdClose } from "react-icons/md";
import TicketStatusBadge from "../../../components/TicketStatusBadge";
import { FiSend } from "react-icons/fi";
import {
  categoryLabel,
  getTicketMessages,
  sendTicketMessage,
  updateTicketStatus,
  type CustomerTicket,
  type TicketMessage,
} from "../../../api/tickets";
import { useAuth } from "../../../context/AuthContext";
import UserComment from "./UserComment/UserComment";

type TicketDetailsModalProps = {
  ticket: CustomerTicket | null;
  onClose: () => void;
};

function TicketDetailsModal({ ticket, onClose }: TicketDetailsModalProps) {
  const { session } = useAuth();
  const ticketId = ticket?.id;
  const [messages, setMessages] = useState<TicketMessage[]>([]);
  const [messagesLoading, setMessagesLoading] = useState(false);
  const [loadingMore, setLoadingMore] = useState(false);
  const [messagesError, setMessagesError] = useState<string | null>(null);
  const [loadMoreError, setLoadMoreError] = useState(false);
  const [nextPage, setNextPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [inputError, setInputError] = useState<string | null>(null);
  const loadingPageRef = useRef(false);

  useEffect(() => {
    if (!ticketId) {
      setMessages([]);
      setMessagesLoading(false);
      setLoadingMore(false);
      setMessagesError(null);
      setInputError(null);
      setLoadMoreError(false);
      setNextPage(0);
      setTotalPages(0);
      return;
    }

    let active = true;
    loadingPageRef.current = false;
    setMessages([]);
    setMessagesLoading(true);
    setLoadingMore(false);
    setMessagesError(null);
    setInputError(null);
    setLoadMoreError(false);
    setNextPage(0);
    setTotalPages(0);

    getTicketMessages(ticketId, 0)
      .then((data) => {
        if (active) {
          setMessages(data.messages);
          setNextPage(data.page + 1);
          setTotalPages(data.totalPages);
        }
      })
      .catch(() => {
        if (active) {
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
  }, [ticketId]);

  if (!ticket) {
    return null;
  }

  async function loadNextPage() {
    if (
      !ticketId ||
      messagesLoading ||
      loadingPageRef.current ||
      nextPage >= totalPages
    ) {
      return;
    }

    loadingPageRef.current = true;
    setLoadingMore(true);
    setLoadMoreError(false);

    try {
      const data = await getTicketMessages(ticketId, nextPage);
      setMessages((currentMessages) => [...currentMessages, ...data.messages]);
      setNextPage(data.page + 1);
      setTotalPages(data.totalPages);
    } catch {
      setLoadMoreError(true);
    } finally {
      loadingPageRef.current = false;
      setLoadingMore(false);
    }
  }

  function handleMessagesScroll(event: UIEvent<HTMLDivElement>) {
    const container = event.currentTarget;
    const distanceFromBottom =
      container.scrollHeight - container.scrollTop - container.clientHeight;

    if (distanceFromBottom <= 48) {
      void loadNextPage();
    }
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
      .then(async () => {
        inputElement.value = "";
        const firstPage = await getTicketMessages(ticketId, 0);
        setMessages(firstPage.messages);
        setNextPage(firstPage.page + 1);
        setTotalPages(firstPage.totalPages);
        setMessagesError(null);
        setLoadMoreError(false);

        if (ticket.status === "waiting_customer") {
          await updateTicketStatus(ticketId, "in_progress");
          ticket.status = "in_progress";
        }
      })
      .catch(() => {
        setMessagesError("Não foi possível enviar ou atualizar as respostas.");
      });
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
      <Box className="client-ticket-modal__main" onScroll={handleMessagesScroll}>
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
              <span>{categoryLabel(ticket.category) || "Categoria não atribuida"}</span>
            </Box>
          </Box>
          <Box>
            <TicketStatusBadge status={ticket.status} />
          </Box>
        </Box>
        <Box className="client-ticket-modal__client-description">
          <h3>Descrição do problema pelo cliente</h3>
          <Box className="client-ticket-modal__client-description__text">
            <p>{ticket.description}</p>
          </Box>
        </Box>
        <Box className="client-ticket-modal__client-comments__section">
          <h3>Respostas ({messages.length})</h3>
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
          {loadingMore && <p>Carregando mais respostas...</p>}
          {loadMoreError && (
            <button onClick={() => void loadNextPage()}>
              Tentar carregar mais respostas
            </button>
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
