import { LuStar, LuCalendar} from "react-icons/lu";
import { FaQuestionCircle } from "react-icons/fa";
export type SupportPanelScoreIcon = typeof FaQuestionCircle;

export type SupportPanelScoreProgress = {
    id: string;
    scoreAverage: number;
    totalAmount: number;
    totalOne: number;
    totalTwo: number;
    totalThree: number;
    totalFour: number;
    totalFive: number;
}
export type SupportPanelScore = {
  id: string;
  title: string;
  icon: SupportPanelScoreIcon;
  value: number;
  complement: string;
  description: string;
}
export const supportPanelScore : SupportPanelScore[] = [
  {
    id: "1",
    title: "Média geral",
    icon: LuStar,
    value: 4.5,
    complement: "/5",
    description: "Média dos atendimentos avaliados",
  },
  {
    id: "2",
    title: "Avaliados",
    icon: FaQuestionCircle,
    value: 12,
    complement: "chamados",
    description: "Total com nota enviada pelo cliente",
  },
  {
    id: "3",
    title:"Aguardando",
    icon: FaQuestionCircle,
    value: 3,
    complement: "chamados",
    description: "Resolvidos sem avaliação do cliente",
  },
  {
    id: "4",
    title: "Mês atual",    
    icon: LuCalendar,
    value: 0,
    complement: "avaliados",
    description: "Média de satisfação dos clientes atendidos",
  },
]

export const supportPanelScoreProgress : SupportPanelScoreProgress = 
    {
        id: "1",
        "scoreAverage": 15,
        "totalAmount": 3,
        "totalOne": 1,
        "totalTwo": 0,
        "totalThree": 0,
        "totalFour": 1,
        "totalFive": 3
    }
