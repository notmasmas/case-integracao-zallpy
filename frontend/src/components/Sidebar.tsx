import { Avatar } from "@chakra-ui/react";
import { NavLink } from "react-router-dom";
import "./Sidebar.css";
import type { RequestContext } from "../models/request";

type SidebarProps = {
  requestContext: RequestContext;
};

export default function Sidebar({ requestContext }: SidebarProps) {
  return (
    <nav className="sidebar-wrapper">
      <div className="profile-wrapper">
        <Avatar.Root>
          <Avatar.Fallback name="Foto de perfil" />
          <Avatar.Image src="/assets/Image.png" />
        </Avatar.Root>
        <p title={requestContext.requestName}>{requestContext.requestName}</p>
      </div>
      <ul className="sidebar-links">
        <li>
          <NavLink to="/home">Painel do Cliente</NavLink>
        </li>
        <li>
          <NavLink to="/tickets">Chamados</NavLink>
        </li>
        <li>
          <NavLink to="/faq">FAQ</NavLink>
        </li>
      </ul>
    </nav>
  );
}
