import { BrowserRouter, Routes, Route } from "react-router-dom";
import "./App.css";

import ClientPanel from "./pages/ClientPanel/ClientPanel";
import Login from "./pages/LoginPage/LoginPage";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/painel-cliente" element={<ClientPanel />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
