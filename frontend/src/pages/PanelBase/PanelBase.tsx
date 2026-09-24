import Sidebar from "../../components/Sidebar";
import Header from "../../components/Header";

import "./PanelBase.css";
import SupportTicket from "../SupportTicket/SupportTicket";
import SupportPanelHome from "../SupportPanelHome/SupportPanelHome";
import type { RequestContext } from "../../models/request";

const requestContext: RequestContext = {
  requestName: "Dummy",
};

export default function PanelBase() {
  return (
    <div className="template-wrapper">
      <Sidebar requestContext={requestContext} />
      <Header requestContext={requestContext} />

      <main className="main-content">
        {/* <SupportTicket requestContext={requestContext} /> */}
        <SupportPanelHome />
      </main>
    </div>
  );
}
