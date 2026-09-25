import { BrowserRouter, Routes, Route } from "react-router-dom";
import "./App.css";

import PanelBase from "./pages/PanelBase/PanelBase";
import Login from "./pages/LoginPage/LoginPage";
import RegistrationPage from "./pages/RegistrationPage/RegistrationPage";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/home" element={<PanelBase />} />
        <Route path="/customer-registration" element={<RegistrationPage />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
