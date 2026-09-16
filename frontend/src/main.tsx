import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { ChakraProvider, defaultSystem } from "@chakra-ui/react";
import "./index.css";
import App from "./App.tsx";

createRoot(document.getElementById("root")!).render(
  <ChakraProvider value={defaultSystem}>
    <StrictMode>
      <App />
    </StrictMode>
  </ChakraProvider>,
);
