import { Outlet } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import PublicFaqPage from "../pages/PublicFaqPage/PublicFaqPage";

// /faq is public. Anonymous visitors get the public page; a CUSTOMER or
// SUPPORT session goes to the nested index route: the panel FAQ
// (RequireSession → PanelBase → PanelFaqPage), the same page for both roles.
export default function FaqRoute() {
  const { session } = useAuth();

  if (session?.role === "CUSTOMER" || session?.role === "SUPPORT") {
    return <Outlet />;
  }

  return <PublicFaqPage />;
}
