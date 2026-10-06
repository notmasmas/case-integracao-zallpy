import { Box } from "@chakra-ui/react";
import { toaster } from "../../../components/ui/toaster";
import { useState } from "react";
import "./SupportTicketModal.css";
import { MdClose } from "react-icons/md";
import { BsSendArrowUp } from "react-icons/bs";
import type { RequestContext } from "../../../models/request";

type SupportTicketModalProps = {
  isOpen: boolean;
  onClose: () => void;
  requestContext: RequestContext;
};
type SupportTicketFormData = {
  projectId: string;
  subject: string;
  body: string;
};

function SupportTicketModal({
  isOpen,
  onClose,
  requestContext,
}: SupportTicketModalProps) {
  const [subject, setSubject] = useState<string>("");
  const [body, setBody] = useState<string>("");

  if (!isOpen) {
    return null;
  }

  const submitTicket = (formData: SupportTicketFormData): void => {
    if (!formData.body.trim() && !formData.subject.trim()) {
      toaster.create({
        title: "Preencha os campos obrigatórios",
        description: "Preencha o título e a descrição do chamado.",
        type: "error",
      });
      return;
    }
    if (!formData.subject.trim()) {
      toaster.create({
        title: "Título obrigatório",
        description: "Preencha o título do chamado.",
        type: "error",
      });
      return;
    }

    if (!formData.body.trim()) {
      toaster.create({
        title: "Descrição obrigatória",
        description: "Preencha a descrição do chamado.",
        type: "error",
      });
      return;
    }

    toaster.create({
      title: "Chamado enviado",
      description: "Seu chamado foi enviado com sucesso.",
      type: "success",
    });

    onClose();
    setSubject("");
    setBody("");
  };
  return (
    <Box className="support-ticket-modal">
      <Box className="support-ticket-modal__header">
        <Box className="support-ticket-modal__title">
          <h3>Novo chamado de suporte</h3>
          <button onClick={onClose}>
            <MdClose aria-hidden="true" />
          </button>
        </Box>
        <span className="support-ticket-modal__subtitle">
          Seu chamado será atribuído para a equipe especializada, acompanhe seu
          chamado em ver detalhes.
        </span>
      </Box>
      <Box className="support-ticket-modal__informations">
        <Box className="support-ticket-modal__informations__item1">
          <span> Cliente solicitante:</span>
          <p title={requestContext.requestName}>{requestContext.requestName}</p>
        </Box>
        <Box className="support-ticket-modal__informations__item2">
          <span> ID do projeto:</span>
          <p>SOLAR-7842-RS</p>
        </Box>
      </Box>
      <Box className="support-ticket-modal__form_body">
        <form
          className="support-ticket-modal__form"
          onSubmit={(event) => {
            event.preventDefault();
            submitTicket({
              projectId: "SOLAR-7842-RS",
              subject,
              body,
            });
          }}
        >
          <Box className="support-ticket-title">
            <label htmlFor="category">Título *</label>
            <input
              aria-valuemax={20}
              id="subject"
              type="text"
              maxLength={20}
              value={subject}
              onChange={(event) => setSubject(event.target.value)}
            />
            <span className="support-ticket-character-count">
              {subject.length}/20
            </span>
          </Box>
          <Box className="support-ticket-description">
            <label htmlFor="description">Descrição *</label>
            <textarea
              aria-valuemax={150}
              id="body"
              maxLength={150}
              value={body}
              onChange={(event) => setBody(event.target.value)}
            />
            <span className="support-ticket-character-count">
              {body.length}/150
            </span>
          </Box>
          <Box className="support-ticket-modal__form__button">
            <button type="submit" disabled={!subject.trim() || !body.trim()}>
              <BsSendArrowUp aria-hidden="true" />
              Registrar chamado
            </button>
          </Box>
        </form>
      </Box>
    </Box>
  );
}
export default SupportTicketModal;
