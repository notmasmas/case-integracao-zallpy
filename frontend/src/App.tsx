import { BrowserRouter, Routes, Route } from "react-router-dom";
import "./App.css";

import PanelBase from "./pages/PanelBase/PanelBase";
import Login from "./pages/LoginPage/LoginPage";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/painel-cliente" element={<PanelBase />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
