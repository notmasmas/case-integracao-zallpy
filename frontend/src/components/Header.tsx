import "./Header.css";
import type { RequestContext } from "../models/request";

type HeaderProps = {
  requestContext: RequestContext;
};

export default function Header({ requestContext }: HeaderProps) {
  return (
    <header className="header-wrapper">
      <p>
        Olá, seja bem-vindo à {requestContext.requestName}!
        <br />
        Acompanhe seu projeto, consulte suas informações e conte com nosso
        suporte sempre que precisar.
      </p>
    </header>
  );
}
