import { Avatar } from "@chakra-ui/react";
import { NavLink } from "react-router-dom";
import "./Sidebar.css";
import { useAuth } from "../context/AuthContext";
import { homePath } from "../routes/routes";

export default function Sidebar() {
  const { session } = useAuth();
  const destination = session ? homePath(session.role) : "/";
  const label = session?.role === "SUPPORT" ? "Fila de chamados" : "Chamados";

  return (
    <nav className="sidebar-wrapper">
      <div className="profile-wrapper">
        <Avatar.Root>
          <Avatar.Fallback name="Foto de perfil" />
          <Avatar.Image src="/assets/Image.png" />
        </Avatar.Root>
        <p title={session?.name}>{session?.name}</p>
      </div>
      <ul className="sidebar-links">
        <li>
          <NavLink to={destination}>{label}</NavLink>
        </li>
      </ul>
    </nav>
  );
}
