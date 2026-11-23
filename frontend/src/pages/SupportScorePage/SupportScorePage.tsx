
import { Box, Progress, HStack, Icon } from "@chakra-ui/react";
import {
  supportPanelScore,
  supportPanelScoreProgress,
} from "./SupportScorePagemock";
import "./SupportScorePage.css";
import { LuStar } from "react-icons/lu";
import WithoutScorePage from "../../components/NoScore/WithoutScore";

export type Score = {
  label:string;
  value:number
}
function SupportScorePage() {
  const scoreDistribution: Score[] = [
    { label: "1", value: supportPanelScoreProgress.totalOne },
    { label: "2", value: supportPanelScoreProgress.totalTwo },
    { label: "3", value: supportPanelScoreProgress.totalThree },
    { label: "4",value: supportPanelScoreProgress.totalFour },
    { label: "5",value: supportPanelScoreProgress.totalFive },
  ];

  return (
    /*<Box className="support-score-page">
      <Box className="support-score-cards-content">

      {supportPanelScore.map((item) => (
        <Box key={item.id} className="support-score-cards">
            <Box className="support-score-cards-header">
              <h2>{item.title}</h2>
              <p><item.icon /></p>
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
          <p>Quantidade de avaliações por estrela: {supportPanelScoreProgress.scoreAverage}</p>
        </Box>
          <Box className="support-score-progress-content">
            {scoreDistribution.map((item) => (
              <Progress.Root
                key={item.label}
                value={item.value}
                max={supportPanelScoreProgress.totalAmount}
                maxW="bg"
                display="flex"
                alignItems="center"
                gap="4" 
              >
                <Progress.Label minW="10" mb="0">
                  <HStack gap="1">
                    <span>{item.label}</span>
                    <Icon as={LuStar} color="var(--color-primary)"/>
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
    </Box>*/
    <WithoutScorePage/>
  );
}

export default SupportScorePage;
