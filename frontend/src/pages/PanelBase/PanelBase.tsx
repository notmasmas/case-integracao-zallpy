import { Outlet } from "react-router-dom";
import Sidebar from "../../components/Sidebar";
import Header from "../../components/Header";
import "./PanelBase.css";
import SupportPanelHome from "../SupportPanelHome/SupportPanelHome";
import SupportTicket from "../SupportTicket/SupportTicket";
import { useAuth } from "../../context/AuthContext";
import { paths } from "../../routes/routes";

export default function PanelBase() {
  return (
    <div className="template-wrapper">
      <Sidebar />
      <Header />

      <main className="main-content">
        {session.role === "SUPPORT" ? (
          <SupportPanelHome />
        ) : (
          <SupportTicket requestContext={requestContext} />
        )}
      </main>
    </div>
  );
}
