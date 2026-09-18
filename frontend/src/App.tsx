import { BrowserRouter, Routes, Route } from "react-router-dom";
import "./App.css";

import ClientPanel from "./pages/ClientPanel";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/painel-cliente" element={<ClientPanel />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
