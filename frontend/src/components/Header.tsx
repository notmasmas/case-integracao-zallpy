import "./Header.css";
import { useAuth } from "../context/AuthContext";

export default function Header() {
  const { session } = useAuth();

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
