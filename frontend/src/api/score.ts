import { useEffect, useState } from "react";
import api from "./api";

export type SupportMetrics = {
  scoreAverage: number | null;
  totalAmount: number;
  totalOne: number;
  totalTwo: number;
  totalThree: number;
  totalFour: number;
  totalFive: number;
};

export async function getSupportMetrics(): Promise<SupportMetrics> {
  const response = await api.get<SupportMetrics>("/support/feedback/metrics");
  return response.data;
}

export function useSupportMetrics() {
  const [metrics, setMetrics] = useState<SupportMetrics | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let active = true;

    getSupportMetrics()
      .then((data) => {
        if (active) {
          setMetrics(data);
          setError(null);
        }
      })
      .catch(() => {
        if (active) {
          setMetrics(null);
          setError("Não foi possível carregar as métricas de avaliação.");
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

  return { metrics, error, loading };
}
