import { useEffect } from "react";
import { Navigate, Outlet } from "react-router-dom";
import { showAccessDeniedToast } from "../components/ui/accessDeniedToast";
import { useAuth } from "../context/AuthContext";
import { homePath, paths, type UserRole } from "./routes";

type RequireRoleProps = {
  role: UserRole;
};

export default function RequireRole({ role }: RequireRoleProps) {
  const { session } = useAuth();
  const denied = session !== null && session.role !== role;

  useEffect(() => {
    if (denied) {
      showAccessDeniedToast();
    }
  }, [denied]);

  if (!session) {
    return <Navigate to={paths.login} replace />;
  }

  if (denied) {
    return <Navigate to={homePath(session.role)} replace />;
  }

  return <Outlet />;
}
