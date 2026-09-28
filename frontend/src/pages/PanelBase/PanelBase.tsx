import { Navigate } from "react-router-dom";
import Sidebar from "../../components/Sidebar";
import Header from "../../components/Header";

import "./PanelBase.css";
import SupportTicket from "../SupportTicket/SupportTicket";
import SupportPanelHome from "../SupportPanelHome/SupportPanelHome";
import { useAuth } from "../../context/AuthContext";
import { paths } from "../../routes/routes";

export default function PanelBase() {
  const { session } = useAuth();

  if (!session) {
    return <Navigate to={paths.login} replace />;
  }

  const requestContext = {
    requestName: session.name,
  };

  return (
    <div className="template-wrapper">
      <Sidebar requestContext={requestContext} />
      <Header requestContext={requestContext} />

      <main className="main-content">
        {session.role === "CUSTOMER" ? (
          <SupportTicket requestContext={requestContext} />
        ) : (
          <SupportPanelHome />
        )}
      </main>
    </div>
  );
}
