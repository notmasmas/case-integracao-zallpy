import "./Header.css";
import { useAuth } from "../context/AuthContext";

export default function Header() {
  const { session } = useAuth();

  if (session?.role === "MANAGER") {
    return (
      <header className="header-wrapper header-wrapper--manager">
        <p>Olá {session.name}, seja bem-vindo(a) à EcoVolt 360! ☀️</p>
      </header>
    );
  }

  return (
    <header className="header-wrapper">
      <p>
        Olá, seja bem-vindo(a) {session?.name}!
        <br />
        Acompanhe seu projeto, consulte suas informações e conte com nosso
        suporte sempre que precisar.
      </p>
    </header>
  );
}
