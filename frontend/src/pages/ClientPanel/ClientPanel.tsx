import Sidebar from "../../components/Sidebar";
import Header from "../../components/Header";

import "./ClientPanel.css";
import SupportTicket from "../SupportTicket/SupportTicket";

export default function ClientPanel() {
  return (
    <div className="template-wrapper">
      <Sidebar />
      <Header />

      <main className="main-content">
        <SupportTicket />
      </main>
    </div>
  );
}
