import { Outlet } from "react-router-dom";
import Sidebar from "../../components/Sidebar";
import Header from "../../components/Header";
import "./PanelBase.css";

export default function PanelBase() {
  return (
    <div className="template-wrapper">
      <Sidebar />
      <Header />

      <main className="main-content">
        <Outlet />
      </main>
    </div>
  );
}
