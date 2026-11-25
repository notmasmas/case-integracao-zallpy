import { useCallback, useEffect, useState } from "react";
import axios from "axios";
import api from "./api";
import { categoryLabels } from "./tickets";

// Mirrors backend FaqCategory.java.
export type FaqCategory =
  | "SYSTEM_MONITORING"
  | "EQUIPMENT"
  | "INSTALLATION"
  | "MAINTENANCE"
  | "SCHEDULING"
  | "PAYMENT"
  | "PROJECT"
  | "ACCESS"
  | "OTHER";

// Mirrors backend FaqDTO.
export type Faq = {
  id: string;
  title: string;
  answer: string;
  category: FaqCategory;
};

// FAQ and ticket categories share the same backend enum, so the ticket
// labels are reused. Unknown values (outside the type at runtime) fall back.
export function faqCategoryLabel(category: FaqCategory): string {
  const labels: Partial<Record<string, string>> = categoryLabels;
  return labels[category] ?? "Outro";
}

const offlineMessage ="Não foi possível conectar ao servidor.";
const genericFaqErrorMessage =
  "Não foi possível carregar as perguntas frequentes. Tente novamente mais tarde.";

export async function listFaqs(): Promise<Faq[]> {
  const response = await api.get<Faq[]>("/faqs");
  return response.data;
}

export function faqErrorMessage(error: unknown): string {
  if (!axios.isAxiosError(error)) {
    return genericFaqErrorMessage;
  }

  if (!error.response) {
    return offlineMessage;
  }

  const data: unknown = error.response.data;
  if (
    error.response.status === 503 &&
    typeof data === "object" &&
    data !== null &&
    "code" in data &&
    data.code === "FAQ_UNAVAILABLE" &&
    "message" in data &&
    typeof data.message === "string" &&
    data.message.trim() !== ""
  ) {
    return data.message;
  }

  return genericFaqErrorMessage;
}

export function useFaqs() {
  const [faqs, setFaqs] = useState<Faq[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [attempt, setAttempt] = useState(0);

  useEffect(() => {
    let active = true;
    setLoading(true);
    setError(null);

    listFaqs()
      .then((data) => {
        if (active) {
          setFaqs(data);
          setError(null);
        }
      })
      .catch((caught: unknown) => {
        if (active) {
          setFaqs([]);
          setError(faqErrorMessage(caught));
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
  }, [attempt]);

  const reload = useCallback(() => setAttempt((current) => current + 1), []);

  return { faqs, error, loading, reload };
}
