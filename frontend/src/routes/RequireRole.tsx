import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { homePath, paths, type UserRole } from "./routes";

type RequireRoleProps = {
  role: UserRole;
};

export default function RequireRole({ role }: RequireRoleProps) {
  const { session } = useAuth();

  if (!session) {
    return <Navigate to={paths.login} replace />;
  }

  if (session.role !== role) {
    return <Navigate to={homePath(session.role)} replace />;
  }

  return <Outlet />;
}
