import {
  createContext,
  useCallback,
  useContext,
  useMemo,
  useState,
  type ReactNode,
} from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import api from "../api/api";
import { toaster } from "../components/ui/toaster";
import {
  clearSession,
  loadSession,
  paths,
  saveSession,
  type Session,
} from "../routes/routes";

type AuthContextValue = {
  session: Session | null;
  setSession: (session: Session) => void;
  logout: () => Promise<void>;
};

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const navigate = useNavigate();
  const [session, setSessionState] = useState<Session | null>(() => loadSession());

  const endSession = useCallback(() => {
    clearSession();
    setSessionState(null);
    navigate(paths.login, { replace: true });
  }, [navigate]);

  const logout = useCallback(async () => {
    try {
      await api.post("/logout");
      endSession();
    } catch (error) {
      const unauthorized = axios.isAxiosError(error) && error.response?.status === 401;
      if (unauthorized) {
        endSession();
        return;
      }

      const offline = axios.isAxiosError(error) && !error.response;
      toaster.create({
        title: "Não foi possível sair",
        description: offline
          ? "Não foi possível conectar ao servidor."
          : "Tente novamente.",
        type: "error",
      });
    }
  }, [endSession]);

  const value = useMemo(
    () => ({
      session,
      setSession: (next: Session) => {
        saveSession(next);
        setSessionState(next);
      },
      logout,
    }),
    [session, logout],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth deve ser usado dentro de AuthProvider");
  }
  return context;
}
