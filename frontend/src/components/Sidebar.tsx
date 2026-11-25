import { Avatar } from "@chakra-ui/react";
import { FiLogOut, FiUser } from "react-icons/fi";
import { NavLink } from "react-router-dom";
import "./Sidebar.css";
import { useAuth } from "../context/AuthContext";
import { paths } from "../routes/routes";

export default function Sidebar() {
  const { session, logout } = useAuth();
  const isSupport = session?.role === "SUPPORT";
  const isManager = session?.role === "MANAGER";

  return (
    <nav className="sidebar-wrapper">
      <div className="profile-wrapper">
        {isManager ? (
          <FiUser size={24} aria-hidden="true" />
        ) : (
          <Avatar.Root>
            <Avatar.Fallback name="Foto de perfil" />
            <Avatar.Image src="/assets/Image.png" />
          </Avatar.Root>
        )}
        <p title={session?.name}>{session?.name}</p>
      </div>
      <ul
        className={
          isManager ? "sidebar-links sidebar-links--manager" : "sidebar-links"
        }
      >
        {isManager ? (
          <>
            <li>
              <NavLink to={paths.manager.home} end>
                Painel do Admin
              </NavLink>
            </li>
            <li>
              <NavLink to={paths.manager.tickets}>Chamados de Equipe</NavLink>
            </li>
          </>
        ) : isSupport ? (
          <>
          <li>
            <NavLink to={paths.support.home}>Painel de Suporte</NavLink>
          </li>
          <li>
            <NavLink to={paths.support.tickets}>Chamados</NavLink>
          </li> 
          </>
        ) : (
          <>
            <li>
              <NavLink to={paths.customer.home}>Painel do Cliente</NavLink>
            </li>
            <li>
              <NavLink to={paths.customer.tickets}>Chamados</NavLink>
            </li>
            <li>
              <NavLink to="/faq">FAQ</NavLink>
            </li>
          </>
        )}
      </ul>
      <button type="button" className="sidebar-logout" onClick={() => void logout()}>
        Sair
        <FiLogOut aria-hidden="true" />
      </button>
    </nav>
  );
}
