import { Box, Button } from "@chakra-ui/react";
import { useState } from "react";
import { FaRegStar, FaStar } from "react-icons/fa";
import {
  categoryLabel,
  ticketMatchesSearch,
  type CustomerTicket,
} from "../../api/tickets";
import TicketStatusBadge from "../../components/TicketStatusBadge";
import SupportTicketEvaluationModal from "./SupportTicketEvaluationModal/SupportTicketEvaluationModal";
import "./SupportTicket.css";

const STAR_COUNT = 5;

type SupportTicketListProps = {
  tickets: CustomerTicket[];
  ticketCategoryFilter: string;
  searchFilter: string;
  error: string | null;
  loading: boolean;
  setSelectedTicket: (ticket: CustomerTicket) => void;
  onTicketEvaluated: (
    ticketId: string,
    evaluation: number,
    comment: string | null,
  ) => void;
};

type PendingEvaluation = {
  ticketId: string;
  rating: number;
};

function canRateTicket(ticket: CustomerTicket): boolean {
  return (
    (ticket.status === "resolved" || ticket.status === "closed") &&
    !ticket.isEvaluated
  );
}

type TicketStarsProps = {
  ticket: CustomerTicket;
  reviewOpen: boolean;
  onToggleReview: () => void;
  onSelect: (rating: number) => void;
};

function TicketStars({
  ticket,
  reviewOpen,
  onToggleReview,
  onSelect,
}: TicketStarsProps) {
  const [hoverRating, setHoverRating] = useState(0);
  const canRate = canRateTicket(ticket);
  const savedRating = ticket.isEvaluated ? (ticket.evaluate ?? 0) : 0;
  const previewRating = canRate ? hoverRating : savedRating;
  const savedComment = ticket.evaluateComment?.trim();

  const onStarClick = (value: number) => {
    if (canRate) {
      onSelect(value);
      return;
    }
    if (ticket.isEvaluated) {
      onToggleReview();
    }
  };

  return (
    <Box className="ticket-stars">
      <span className="ticket-stars__label">Avaliar</span>
      <Box
        className="ticket-stars__row"
        role="group"
        aria-label={
          ticket.isEvaluated
            ? `Avaliado com ${savedRating} de 5 estrelas`
            : canRate
              ? "Avaliar chamado"
              : "Avaliação disponível quando o chamado estiver resolvido"
        }
      >
        {Array.from({ length: STAR_COUNT }, (_, index) => {
          const value = index + 1;
          const filled = value <= previewRating;
          const Icon = filled ? FaStar : FaRegStar;
          const className = [
            "ticket-stars__button",
            filled ? "ticket-stars__button--filled" : "",
            ticket.isEvaluated ? "ticket-stars__button--saved" : "",
          ]
            .filter(Boolean)
            .join(" ");

          return (
            <button
              key={value}
              type="button"
              className={className}
              aria-label={
                ticket.isEvaluated
                  ? "Ver avaliação enviada"
                  : `Avaliar com ${value} ${value === 1 ? "estrela" : "estrelas"}`
              }
              aria-expanded={ticket.isEvaluated ? reviewOpen : undefined}
              disabled={!canRate && !ticket.isEvaluated}
              onMouseEnter={() => {
                if (canRate) {
                  setHoverRating(value);
                }
              }}
              onMouseLeave={() => setHoverRating(0)}
              onFocus={() => {
                if (canRate) {
                  setHoverRating(value);
                }
              }}
              onBlur={() => setHoverRating(0)}
              onClick={() => onStarClick(value)}
            >
              <Icon aria-hidden="true" />
            </button>
          );
        })}
      </Box>
      {ticket.isEvaluated && reviewOpen ? (
        <Box className="ticket-stars__review">
          <Box className="ticket-stars__review-score">
            {Array.from({ length: STAR_COUNT }, (_, index) => {
              const value = index + 1;
              const Icon = value <= savedRating ? FaStar : FaRegStar;
              return (
                <Icon
                  key={value}
                  className={
                    value <= savedRating
                      ? "ticket-stars__review-icon ticket-stars__review-icon--filled"
                      : "ticket-stars__review-icon"
                  }
                  aria-hidden="true"
                />
              );
            })}
            <span>
              {savedRating}/5
            </span>
          </Box>
          <p>{savedComment ? savedComment : "Sem comentário"}</p>
        </Box>
      ) : null}
    </Box>
  );
}

function SupportTicketList({
  tickets,
  ticketCategoryFilter,
  searchFilter,
  error,
  loading,
  setSelectedTicket,
  onTicketEvaluated,
}: SupportTicketListProps) {
  const [pendingEvaluation, setPendingEvaluation] =
    useState<PendingEvaluation | null>(null);
  const [openReviewId, setOpenReviewId] = useState<string | null>(null);
  const filteredTickets = tickets.filter((ticket) => {
    const matchesCategory =
      ticketCategoryFilter === "all" ||
      ticket.category === ticketCategoryFilter;

    return matchesCategory && ticketMatchesSearch(ticket, searchFilter);
  });

  return (
    <Box className="support-ticket-list">
      <Box className="support-ticket-list__header">
        <Box> Código </Box>
        <Box> Título </Box>
        <Box> Categoria </Box>
        <Box> Status </Box>
        <Box> Ações </Box>
      </Box>
      <Box className="support-ticket-list__body">
        {error ? <p>{error}</p> : null}
        {!error && loading ? <p>Carregando chamados...</p> : null}
        {!error && !loading && filteredTickets.length === 0 ? (
          <p>Nenhum chamado encontrado.</p>
        ) : null}
        {!error &&
          filteredTickets.map((ticket) => (
            <Box
              className={
                openReviewId === ticket.id
                  ? "support-ticket-item support-ticket-item--review-open"
                  : "support-ticket-item"
              }
              key={ticket.id}
            >
              <Box>
                <Box>
                  {ticket.id}
                  <Box color="var(--color-text-gray-secondary)">
                    {ticket.createdAt}
                  </Box>
                </Box>
              </Box>
              <Box>{ticket.title}</Box>
              <Box>{categoryLabel(ticket.category)}</Box>
              <Box>
                <TicketStatusBadge status={ticket.status} />
              </Box>
              <Box className="support-ticket-item__actions">
                <TicketStars
                  ticket={ticket}
                  reviewOpen={openReviewId === ticket.id}
                  onToggleReview={() =>
                    setOpenReviewId((current) =>
                      current === ticket.id ? null : ticket.id,
                    )
                  }
                  onSelect={(rating) =>
                    setPendingEvaluation({ ticketId: ticket.id, rating })
                  }
                />
                <Button className="support-ticket-item__button" onClick={() => setSelectedTicket(ticket)}>
                  Ver detalhes
                </Button>
              </Box>
            </Box>
          ))}
      </Box>
      <SupportTicketEvaluationModal
        ticketId={pendingEvaluation?.ticketId ?? null}
        initialRating={pendingEvaluation?.rating ?? 1}
        onClose={() => setPendingEvaluation(null)}
        onSubmitted={onTicketEvaluated}
      />
    </Box>
  );
}

export default SupportTicketList;
