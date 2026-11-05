import { useEffect, useState } from "react";
import api from "./api";

export type CustomerProject = {
  id: string;
  name: string;
  status: string | null;
  installationDate?: string | null;
  address?: {
    cep: string;
    state: string;
    city: string;
    neighborhood: string;
    street: string;
    number: string;
    complement: string;
  } | null;
  createdAt: string;
  updatedAt?: string | null;
};

const projectStatusLabels: Record<string, string> = {
  AWAITING_INSTALLATION: "Aguardando instalação",
  AWAITING_APPROVAL: "Aguardando aprovação",
  INSTALLATION_SCHEDULE: "Instalação agendada",
  INSTALLATION_IN_PROGRESS: "Instalação em andamento",
  IN_PROGRESS: "Em andamento",
  INSTALLED: "Instalado",
  COMPLETED: "Concluído",
  CANCELED: "Cancelado",
  CANCELLED: "Cancelado",
};

export function projectStatusLabel(status: string | null): string {
  if (!status?.trim()) {
    return "Status não informado";
  }

  return (
    projectStatusLabels[status] ??
    status.toLocaleLowerCase("pt-BR").replaceAll("_", " ")
  );
}

export async function listCustomerProjects(): Promise<CustomerProject[]> {
  const response = await api.get<CustomerProject[]>("/projects");
  return response.data;
}

export function useCustomerProjects() {
  const [projects, setProjects] = useState<CustomerProject[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let active = true;

    listCustomerProjects()
      .then((data) => {
        if (active) {
          setProjects(data);
          setError(null);
        }
      })
      .catch(() => {
        if (active) {
          setProjects([]);
          setError("Não foi possível carregar os projetos.");
        }
      })
      .finally(() => {
        if (active) {
          setLoading(false);
        }
      });

    return () => {
      active = false;
    };
  }, []);

  return { projects, error, loading };
}