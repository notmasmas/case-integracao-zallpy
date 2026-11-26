import { LuStar } from "react-icons/lu";
import { FaQuestionCircle } from "react-icons/fa";
import { HiOutlineTicket } from "react-icons/hi";

export type SupportPanelScoreIcon = typeof FaQuestionCircle;

export type SupportMetricsKey =
  | "scoreAverage"
  | "totalAmount"
  | "pendingAmount"
  | "totalTickets";

export type SupportPanelScoreMeta = {
  id: string;
  title: string;
  icon: SupportPanelScoreIcon;
  complement: string;
  description: string;
  metricKey: SupportMetricsKey;
};

export const supportScoreCardsMeta: SupportPanelScoreMeta[] = [
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
  {
    id: "3",
    title: "Aguardando",
    icon: FaQuestionCircle,
    complement: "chamados",
    description: "Resolvidos ou fechados sem avaliação do cliente",
    metricKey: "pendingAmount",
  },
  {
    id: "4",
    title: "Total",
    icon: HiOutlineTicket,
    complement: "chamados",
    description: "Número total de chamados atribuídos a você",
    metricKey: "totalTickets",
  },
];