import { useEffect, useRef, useState } from "react";
import { Navigate, Outlet } from "react-router-dom";
import axios from "axios";
import api from "../api/api";
import { useAuth } from "../context/AuthContext";
import { clearSession, expiredSessionLoginPath, type UserRole } from "./routes";
import "./RequireSession.css";

type SessionStatus = "checking" | "ready" | "offline";

function isUserRole(value: string): value is UserRole {
  return value === "CUSTOMER" || value === "SUPPORT";
}

export default function RequireSession() {
  const { session, setSession } = useAuth();
  const [status, setStatus] = useState<SessionStatus>("checking");
  const [attempt, setAttempt] = useState(0);
  const sessionRef = useRef(session);
  const setSessionRef = useRef(setSession);
  sessionRef.current = session;
  setSessionRef.current = setSession;

  const accessToken = session?.accessToken.trim() ?? "";

  useEffect(() => {
    if (!accessToken) {
      return;
    }

    let active = true;
    setStatus("checking");

    api
      .get<{ role: string }>("/session")
      .then((response) => {
        if (!active) {
          return;
        }
        const role = response.data.role;
        const current = sessionRef.current;
        if (!current || !isUserRole(role)) {
          return;
        }
        if (role !== current.role) {
          setSessionRef.current({ ...current, role });
        }
        setStatus("ready");
      })
      .catch((error: unknown) => {
        if (!active) {
          return;
        }
        if (axios.isAxiosError(error) && !error.response) {
          setStatus("offline");
        }
      });

    return () => {
      active = false;
    };
  }, [accessToken, attempt]);

  if (!session || !accessToken) {
    clearSession();
    return <Navigate to={expiredSessionLoginPath()} replace />;
  }

  if (status === "offline") {
    return (
      <div className="session-gate">
        <p>Não foi possível conectar ao servidor.</p>
        <button type="button" onClick={() => setAttempt((value) => value + 1)}>
          Tentar novamente
        </button>
      </div>
    );
  }

  if (status !== "ready") {
    return (
      <div className="session-gate">
        <p>Carregando...</p>
      </div>
    );
  }

  return <Outlet />;
}
