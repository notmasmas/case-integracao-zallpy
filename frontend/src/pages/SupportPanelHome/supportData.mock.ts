import { BiHeadphone } from "react-icons/bi";
import { LuStar, LuTriangle, LuUserCheck } from "react-icons/lu";

export type SupportSummaryIcon = typeof BiHeadphone;

export type SupportSummaryCard = {
  id: string;
  title: string;
  value: number;
  unit: string;
  description: string;
  icon: SupportSummaryIcon;
};

export const supportSummaryMock: SupportSummaryCard[] = [
  {
    id: "active-tickets",
    title: "Seus chamados ativos",
    value: 2,
    unit: "sob sua tratativa",
    description: "1 pendente • em andamento",
    icon: BiHeadphone,
  },
  {
    id: "priority-tickets",
    title: "Atenção prioritária",
    value: 2,
    unit: "chamados urgentes",
    description: "Requerem retorno rápido ou vistoria imediata",
    icon: LuTriangle,
  },
  {
    id: "general-queue",
    title: "Na fila geral",
    value: 15,
    unit: "chamados",
    description: "Disponíveis para serem atribuídos",
    icon: LuUserCheck,
  },
  {
    id: "resolved-tickets",
    title: "Resolvidos & Avaliação",
    value: 0,
    unit: "Avaliados",
    description: "Média de satisfação dos clientes atendidos",
    icon: LuStar,
  },
];
