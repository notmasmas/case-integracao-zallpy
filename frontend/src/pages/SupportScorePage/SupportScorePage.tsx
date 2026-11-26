import { Box, Progress, HStack, Icon } from "@chakra-ui/react";
import {
  supportScoreCardsMeta,
  type SupportMetricsKey,
} from "./supportScoreCardsMeta";
import "./SupportScorePage.css";
import { LuStar } from "react-icons/lu";
import WithoutScorePage from "./NoScore/WithoutScore";
import { useSupportMetrics } from "../../api/score";
import type { SupportMetrics } from "../../api/score";

export type Score = {
  label: string;
  value: number;
};

function formatScoreAverage(scoreAverage: number | null): string {
  if (scoreAverage === null) {
    return "—";
  }
  return scoreAverage.toFixed(1);
}

function cardValue(
  metrics: SupportMetrics,
  metricKey: SupportMetricsKey,
): string | number {
  if (metricKey === "scoreAverage") {
    return formatScoreAverage(metrics.scoreAverage);
  }
  return metrics[metricKey];
}

function SupportScorePage() {
  const { metrics, error, loading } = useSupportMetrics();
  const hasEvaluations = metrics != null && metrics.totalAmount > 0;

  if (loading) {
    return (
      <Box className="support-score-page">
        <p>Carregando…</p>
      </Box>
    );
  }

  if (error || !metrics) {
    return (
      <Box className="support-score-page">
        <p>{error ?? "Não foi possível carregar as métricas de avaliação."}</p>
      </Box>
    );
  }

  const scoreDistribution: Score[] = [
    { label: "1", value: metrics.totalOne },
    { label: "2", value: metrics.totalTwo },
    { label: "3", value: metrics.totalThree },
    { label: "4", value: metrics.totalFour },
    { label: "5", value: metrics.totalFive },
  ];

  return (
    <Box className="support-score-page">
      <Box className="support-score-cards-content">
        {supportScoreCardsMeta.map((item) => (
          <Box key={item.id} className="support-score-cards">
            <Box className="support-score-cards-header">
              <h2>{item.title}</h2>
              <p>
                <item.icon />
              </p>
            </Box>
            <Box className="support-score-cards-value">
              <p>{cardValue(metrics, item.metricKey)}</p>
              <span>{item.complement}</span>
            </Box>
            <Box className="support-score-cards-description">
              <p>{item.description}</p>
            </Box>
          </Box>
        ))}
      </Box>

      {hasEvaluations ? (
        <Box className="support-score-progress">
          <Box className="support-score-progress-header">
            <h2>Distribuição de notas</h2>
            <p>Quantidade de avaliações por estrela: {metrics.totalAmount}</p>
          </Box>
          <Box className="support-score-progress-content">
            {scoreDistribution.map((item) => (
              <Progress.Root
                key={item.label}
                value={item.value}
                max={metrics.totalAmount}
                maxW="bg"
                display="flex"
                alignItems="center"
                gap="4"
              >
                <Progress.Label minW="10" mb="0">
                  <HStack gap="1">
                    <span>{item.label}</span>
                    <Icon as={LuStar} color="var(--color-primary)" />
                  </HStack>
                </Progress.Label>

                <Progress.Track flex="1">
                  <Progress.Range background="var(--color-primary)" />
                </Progress.Track>

                <Progress.ValueText minW="12" textAlign="right" />
              </Progress.Root>
            ))}
          </Box>
        </Box>
      ) : (
        <WithoutScorePage />
      )}
    </Box>
  );
}

export default SupportScorePage;