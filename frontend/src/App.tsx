import { BrowserRouter, Routes, Route } from "react-router-dom";
import "./App.css";

import PanelBase from "./pages/PanelBase/PanelBase";
import Login from "./pages/LoginPage/LoginPage";
import { AuthProvider } from "./context/AuthContext";
import { paths } from "./routes/routes";

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path={paths.login} element={<Login />} />
          <Route path={paths.panel} element={<PanelBase />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
