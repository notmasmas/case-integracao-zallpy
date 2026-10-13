import { LuCheck, LuLeaf, LuSun, LuTrendingUp } from "react-icons/lu";

export type CustomerSummaryIcon = typeof LuTrendingUp;

export type CustomerSummaryCard = {
  id: string;
  title: string;
  value: string;
  unit: string;
  description: string;
  icon: CustomerSummaryIcon;
};

export const customerSummaryMock: CustomerSummaryCard[] = [
  {
    id: "accumulated-energy",
    title: "Energia Acumulada",
    value: "R$20.000,00",
    unit: "",
    description: "+ R$ 1.178,00 economizados neste mês",
    icon: LuTrendingUp,
  },
  {
    id: "environmental-impact",
    title: "Impacto ambiental",
    value: "8.930",
    unit: "kg CO₂",
    description: "Equivale a 476 árvores salvas",
    icon: LuLeaf,
  },
  {
    id: "generated-energy",
    title: "Energia gerada",
    value: "48.3",
    unit: "kWh",
    description: "Gerados hoje",
    icon: LuSun,
  },
  {
    id: "tickets",
    title: "Chamados",
    value: "2",
    unit: "resolvidos",
    description: "Deixe seu feedback",
    icon: LuCheck,
  },
];
