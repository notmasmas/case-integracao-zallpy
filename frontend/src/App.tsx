import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import "./App.css";

import PanelBase from "./pages/PanelBase/PanelBase";
import Login from "./pages/LoginPage/LoginPage";
import { AuthProvider, useAuth } from "./context/AuthContext";
import { paths } from "./routes/routes";
import RegistrationPage from "./pages/RegistrationPage/RegistrationPage";
import SupportPanelHome from "./pages/SupportPanelHome/SupportPanelHome";
import CustomerPanelHome from "./pages/CustomerPanelHome/CustomerPanelHome";
import SupportTicket from "./pages/SupportTicket/SupportTicket";

function PanelHome() {
  const { session } = useAuth();

  if (session?.role === "SUPPORT") {
    return <SupportPanelHome />;
  }

  return <CustomerPanelHome />;
}

function TicketsRoute() {
  const { session } = useAuth();

  if (!session || session.role !== "CUSTOMER") {
    return <Navigate to={paths.panel} replace />;
  }

  return <SupportTicket requestContext={{ requestName: session.name }} />;
}

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path={paths.login} element={<Login />} />
          <Route element={<PanelBase />}>
            <Route path={paths.panel} element={<PanelHome />} />
            <Route path={paths.tickets} element={<TicketsRoute />} />
          </Route>
          <Route path={paths.registration} element={<RegistrationPage />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
