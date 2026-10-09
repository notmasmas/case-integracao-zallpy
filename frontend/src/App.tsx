import { BrowserRouter, Routes, Route } from "react-router-dom";
import "./App.css";

import PanelBase from "./pages/PanelBase/PanelBase";
import Login from "./pages/LoginPage/LoginPage";
import { AuthProvider } from "./context/AuthContext";
import { paths } from "./routes/routes";
import RegistrationPage from "./pages/RegistrationPage/RegistrationPage";
import RequireRole from "./routes/RequireRole";
import SupportTicket from "./pages/SupportTicket/SupportTicket";
import SupportPanelHome from "./pages/SupportPanelHome/SupportPanelHome";

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path={paths.login} element={<Login />} />
          <Route path={paths.registration} element={<RegistrationPage />} />
          <Route element={<RequireRole role="CUSTOMER" />}>
            <Route element={<PanelBase />}>
              <Route path={paths.customer.tickets} element={<SupportTicket />} />
            </Route>
          </Route>
          <Route element={<RequireRole role="SUPPORT" />}>
            <Route element={<PanelBase />}>
              <Route path={paths.support.home} element={<SupportPanelHome />} />
            </Route>
          </Route>
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
