import { Box } from "@chakra-ui/react";
import axios from "axios";
import { useEffect, useState } from "react";
import { FaRegStar, FaStar } from "react-icons/fa";
import { MdClose } from "react-icons/md";
import { evaluateTicket } from "../../../api/tickets";
import { toaster } from "../../../components/ui/toaster";
import "./SupportTicketEvaluationModal.css";

const STAR_COUNT = 5;
const COMMENT_LIMIT = 150;

type SupportTicketEvaluationModalProps = {
  ticketId: string | null;
  initialRating: number;
  onClose: () => void;
  onSubmitted: (
    ticketId: string,
    evaluation: number,
    comment: string | null,
  ) => void;
};

function evaluationErrorMessage(error: unknown): string {
  if (axios.isAxiosError(error)) {
    const message = error.response?.data?.message;
    if (typeof message === "string" && message.trim()) {
      return message;
    }
  }
  return "Não foi possível enviar a avaliação.";
}

function SupportTicketEvaluationModal({
  ticketId,
  initialRating,
  onClose,
  onSubmitted,
}: SupportTicketEvaluationModalProps) {
  const [rating, setRating] = useState(initialRating);
  const [hoverRating, setHoverRating] = useState(0);
  const [comment, setComment] = useState("");
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    setRating(initialRating);
    setHoverRating(0);
    setComment("");
  }, [ticketId, initialRating]);

  useEffect(() => {
    if (!ticketId || submitting) {
      return;
    }

    const onKeyDown = (event: KeyboardEvent) => {
      if (event.key === "Escape") {
        onClose();
      }
    };

    window.addEventListener("keydown", onKeyDown);
    return () => window.removeEventListener("keydown", onKeyDown);
  }, [ticketId, submitting, onClose]);

  if (!ticketId) {
    return null;
  }

  const shownRating = hoverRating || rating;

  const submitEvaluation = async () => {
    if (submitting) {
      return;
    }

    setSubmitting(true);
    const trimmedComment = comment.trim();

    try {
      await evaluateTicket(
        ticketId,
        rating,
        trimmedComment === "" ? null : trimmedComment,
      );
      onSubmitted(
        ticketId,
        rating,
        trimmedComment === "" ? null : trimmedComment,
      );
      toaster.create({
        type: "success",
        title: "Avaliação enviada",
        description: "Sua nota e seu comentário foram registrados.",
      });
      onClose();
    } catch (error) {
      toaster.create({
        type: "error",
        title: "Não foi possível avaliar",
        description: evaluationErrorMessage(error),
      });
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Box
      className="ticket-evaluation-modal__backdrop"
      onClick={() => {
        if (!submitting) {
          onClose();
        }
      }}
    >
      <Box
        className="ticket-evaluation-modal"
        role="dialog"
        aria-modal="true"
        aria-labelledby="ticket-evaluation-title"
        onClick={(event) => event.stopPropagation()}
      >
        <Box className="ticket-evaluation-modal__header">
          <Box className="ticket-evaluation-modal__title">
            <h3 id="ticket-evaluation-title">Avaliar chamado</h3>
            <button type="button" onClick={onClose} disabled={submitting}>
              <MdClose aria-hidden="true" />
            </button>
          </Box>
          <span className="ticket-evaluation-modal__subtitle">
            Escolha de 1 a 5 estrelas e, se quiser, deixe um comentário.
          </span>
        </Box>
        <form
          className="ticket-evaluation-modal__form"
          onSubmit={(event) => {
            event.preventDefault();
            void submitEvaluation();
          }}
        >
          <Box
            className="ticket-stars ticket-evaluation-modal__stars"
            role="group"
            aria-label="Nota da avaliação"
          >
            {Array.from({ length: STAR_COUNT }, (_, index) => {
              const value = index + 1;
              const filled = value <= shownRating;
              const Icon = filled ? FaStar : FaRegStar;

              return (
                <button
                  key={value}
                  type="button"
                  className={
                    filled
                      ? "ticket-stars__button ticket-stars__button--filled"
                      : "ticket-stars__button"
                  }
                  aria-label={`Avaliar com ${value} ${value === 1 ? "estrela" : "estrelas"}`}
                  aria-pressed={value === rating}
                  disabled={submitting}
                  onMouseEnter={() => setHoverRating(value)}
                  onMouseLeave={() => setHoverRating(0)}
                  onFocus={() => setHoverRating(value)}
                  onBlur={() => setHoverRating(0)}
                  onClick={() => setRating(value)}
                >
                  <Icon aria-hidden="true" />
                </button>
              );
            })}
          </Box>
          <Box className="ticket-evaluation-comment">
            <label htmlFor="ticket-evaluation-comment">Comentário</label>
            <textarea
              id="ticket-evaluation-comment"
              maxLength={COMMENT_LIMIT}
              value={comment}
              disabled={submitting}
              onChange={(event) => setComment(event.target.value)}
            />
            <span className="ticket-evaluation-comment__count">
              {comment.length}/{COMMENT_LIMIT}
            </span>
          </Box>
          <Box className="ticket-evaluation-modal__actions">
            <button type="submit" disabled={submitting}>
              {submitting ? "Enviando..." : "Concluir"}
            </button>
          </Box>
        </form>
      </Box>
    </Box>
  );
}

export default SupportTicketEvaluationModal;
