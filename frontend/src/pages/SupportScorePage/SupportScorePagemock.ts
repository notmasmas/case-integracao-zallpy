import { LuStar } from "react-icons/lu";
import { FaQuestionCircle } from "react-icons/fa";

export type SupportPanelScoreIcon = typeof FaQuestionCircle;

export type SupportPanelScoreMeta = {
  id: string;
  title: string;
  icon: SupportPanelScoreIcon;
  complement: string;
  description: string;
  metricKey: "scoreAverage" | "totalAmount";
};

export const supportPanelScoreMeta: SupportPanelScoreMeta[] = [
  {
    id: "1",
    title: "Média geral",
    icon: LuStar,
    complement: "/5",
    description: "Média dos atendimentos avaliados",
    metricKey: "scoreAverage",
  },
  {
    id: "2",
    title: "Avaliados",
    icon: FaQuestionCircle,
    complement: "chamados",
    description: "Total com nota enviada pelo cliente",
    metricKey: "totalAmount",
  },
];
