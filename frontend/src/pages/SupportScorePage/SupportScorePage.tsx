
import { Box, Progress } from "@chakra-ui/react";
import {
  supportPanelScore,
  supportPanelScoreProgress,
} from "./SupportScorePagemock";
import "./SupportScorePage.css";

function SupportScorePage() {
  const scoreDistribution = [
    { label: "Nota 1", value: supportPanelScoreProgress.totalOne },
    { label: "Nota 2", value: supportPanelScoreProgress.totalTwo },
    { label: "Nota 3", value: supportPanelScoreProgress.totalThree },
    { label: "Nota 4", value: supportPanelScoreProgress.totalFour },
    { label: "Nota 5", value: supportPanelScoreProgress.totalFive },
  ];

  return (
    <Box className="support-score-page">
      <Box className="support-score-cards-content">

      {supportPanelScore.map((item) => (
        <Box key={item.id} className="support-score-cards">
            <Box className="support-score-cards-header">
              <h2>{item.title}</h2>
              <item.icon />
            </Box>
             <Box className="support-score-cards-value">
                <p>{item.value}</p>
                <span>{item.complement}</span>
             </Box>
              <Box className="support-score-cards-description">
              <p>{item.description}</p>
            </Box>
        </Box>
      ))}
      </Box>

      <Box className="support-score-progress">
        <Box className="support-score-progress-header">
        <h2>Distribuição de notas</h2>
          <p>Média geral: {supportPanelScoreProgress.scoreAverage}</p>
          <p>Total de avaliações: {supportPanelScoreProgress.totalAmount}</p>
        </Box>

        {scoreDistribution.map((item) => (
          <Progress.Root
            key={item.label}
            value={item.value}
            max={supportPanelScoreProgress.totalAmount}
            maxW="sm"
          >
            <Progress.Label>{item.label}</Progress.Label>

            <Progress.Track flex="1">
              <Progress.Range />
            </Progress.Track>

            <Progress.ValueText />
          </Progress.Root>
        ))}
      </Box>
    </Box>
  );
}

export default SupportScorePage;
