import { useEffect } from "react";
import { Box } from "@chakra-ui/react";
import { MdClose } from "react-icons/md";
import type { CustomerProject } from "../../api/projects";
import ProjectStatusBadge from "../../components/ProjectStatusBadge";
import "./CustomerProjectsModal.css";

function formatInstallationDate(value: string | null | undefined): string {
  if (!value) {
    return "Data não definida";
  }

  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return "Data não definida";
  }

  return new Intl.DateTimeFormat("pt-BR", { dateStyle: "short" }).format(date);
}

function projectAddress(project: CustomerProject): string {
  const address = project.address;
  if (!address) {
    return "Endereço não informado";
  }

  const street = [address.street, address.number].filter(Boolean).join(", ");
  const cityState = [address.city, address.state].filter(Boolean).join(" - ");
  return [street, cityState].filter(Boolean).join(", ") || "Endereço não informado";
}

type CustomerProjectsModalProps = {
  isOpen: boolean;
  projects: CustomerProject[];
  loading: boolean;
  error: string | null;
  selectedProjectId: string | null;
  onSelect: (projectId: string) => void;
  onClose: () => void;
};

function CustomerProjectsModal({
  isOpen,
  projects,
  loading,
  error,
  selectedProjectId,
  onSelect,
  onClose,
}: CustomerProjectsModalProps) {
  useEffect(() => {
    if (!isOpen) {
      return;
    }

    document.body.classList.add("customer-projects-modal-open");

    const handleKeyDown = (event: KeyboardEvent) => {
      if (event.key === "Escape") {
        onClose();
      }
    };

    window.addEventListener("keydown", handleKeyDown);
    return () => {
      document.body.classList.remove("customer-projects-modal-open");
      window.removeEventListener("keydown", handleKeyDown);
    };
  }, [isOpen, onClose]);

  if (!isOpen) {
    return null;
  }

  return (
    <Box
      className="customer-projects-modal__backdrop"
      onClick={onClose}
    >
      <Box
        className="customer-projects-modal"
        role="dialog"
        aria-modal="true"
        aria-labelledby="customer-projects-modal-title"
        onClick={(event) => event.stopPropagation()}
      >
        <Box className="customer-projects-modal__header">
          <Box>
            <h2 id="customer-projects-modal-title">Projetos e Instalações solares</h2>
            <p>Acompanhe os dados de cada usina, consulte a previsão de instalação e selecione a usina ativa para o portal.</p>
          </Box>
          <button
            className="customer-projects-modal__close"
            type="button"
            aria-label="Fechar lista de projetos"
            onClick={onClose}
            autoFocus
          >
            <MdClose aria-hidden="true" />
          </button>
        </Box>
        {!error && loading ? (
          <p className="customer-projects-modal__message" role="status">
            Carregando projetos...
          </p>
        ) : null}
        {!error && !loading && projects.length === 0 ? (
          <p className="customer-projects-modal__message">
            Nenhum projeto vinculado ao cliente.
          </p>
        ) : null}
        {!error && !loading && projects.length > 0 ? (
          <Box as="ul" className="customer-projects-modal__list">
            {projects.map((project) => (
              <li key={project.id}>
                <button
                  className="customer-projects-modal__project"
                  type="button"
                  aria-current={selectedProjectId === project.id ? "true" : undefined}
                  onClick={() => onSelect(project.id)}
                >
                  <span className="customer-projects-modal__project-heading">
                    <span className="customer-projects-modal__project-name">
                      {project.name}
                    </span>
                    <span className="customer-projects-modal__project-id">
                      | {project.id}
                    </span>
                  </span>
                  <ProjectStatusBadge status={project.status} />
                  <span className="customer-projects-modal__project-details">
                    <span className="customer-projects-modal__project-location">
                      Localização: <strong>{projectAddress(project)}</strong>
                    </span>
                    <span className="customer-projects-modal__project-installation">
                      <span>Previsão de Instalação:</span>
                      <strong>{formatInstallationDate(project.installationDate)}</strong>
                    </span>
                  </span>
                </button>
              </li>
            ))}
          </Box>
        ) : null}
      </Box>
    </Box>
  );
}

export default CustomerProjectsModal;