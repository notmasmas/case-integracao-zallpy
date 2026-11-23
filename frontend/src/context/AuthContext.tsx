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
  sessionEndedLoginPath,
  type Session,
} from "../routes/routes";

type AuthContextValue = {
  session: Session | null;
  endedLoginPath: string | null;
  setSession: (session: Session) => void;
  logout: () => Promise<void>;
  endSessionByInactivity: () => void;
};

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const navigate = useNavigate();
  const [session, setSessionState] = useState<Session | null>(() => loadSession());
  const [endedLoginPath, setEndedLoginPath] = useState<string | null>(null);

  // endedLoginPath is set in the same tick as the session, so RequireSession,
  // which may render before the route changes, redirects to the same target.
  const endSession = useCallback(
    (target: string = paths.login) => {
      clearSession();
      setEndedLoginPath(target);
      setSessionState(null);
      navigate(target, { replace: true });
    },
    [navigate],
  );

  const logout = useCallback(async () => {
    try {
      await api.post("/logout");
      endSession();
      toaster.create({
        title: "Logout realizado com sucesso",
        type: "success",
      });
    } catch (error) {
      const unauthorized = axios.isAxiosError(error) && error.response?.status === 401;
      if (unauthorized) {
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

  const endSessionByInactivity = useCallback(() => {
    // Read before clearSession(): the request interceptor runs later and
    // would find no stored token.
    const token = loadSession()?.accessToken;
    if (token) {
      void api
        .post("/logout", undefined, {
          skipSessionRedirect: true,
          headers: { Authorization: `Bearer ${token}` },
        })
        .catch(() => {
          // Best effort: the backend already rejects this token for
          // inactivity, and the next login issues a new one.
        });
    }
    endSession(sessionEndedLoginPath("inatividade"));
  }, [endSession]);

  const value = useMemo(
    () => ({
      session,
      endedLoginPath,
      setSession: (next: Session) => {
        saveSession(next);
        setEndedLoginPath(null);
        setSessionState(next);
      },
      logout,
      endSessionByInactivity,
    }),
    [session, endedLoginPath, logout, endSessionByInactivity],
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
