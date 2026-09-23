import Sidebar from "../../components/Sidebar";
import Header from "../../components/Header";

import "./ClientPanel.css";
import SupportTicket from "../SupportTicket/SupportTicket";
import type { RequestContext } from "../../models/request";

const requestContext: RequestContext = {
  requestName: "Dummy",
};

export default function ClientPanel() {
  return (
    <div className="template-wrapper">
      <Sidebar requestContext={requestContext} />
      <Header requestContext={requestContext} />

      <main className="main-content">
        <SupportTicket requestContext={requestContext} />
      </main>
    </div>
  );
}
