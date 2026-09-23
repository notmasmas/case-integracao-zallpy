import { Flex, Image } from "@chakra-ui/react";
import Logo from "../../assets/logo.svg";
import "../../index.css";
import "./loginPage.css";
import LoginPageForm from "./LoginPageForm/LoginPageForm";

function LoginPage() {
  return (
    <div className="login-page">
      <Flex
        className="login-page__layout"
        justify="space-between"
        align="center"
        direction={{ base: "column", md: "row" }}
        minHeight="calc(100vh - 4rem)"
        width="100%"
        gap={{ base: "2rem", md: "1rem" }}
      >
        <Flex
          className="login-page__brand"
          justify="center"
          align="flex-start"
          direction="column"
          gap="2rem"
        >
          <Flex width="100%">
            <Image className="login-page__logo" src={Logo} alt="Logo" />
          </Flex>
          <Flex width="80%" justify="center" align="center">
            <p className="login-page__slogan">
              Onde há Sol, <br /> há energia
            </p>
          </Flex>
        </Flex>

        <Flex className="login-page__form" justify="center" align="center">
          <LoginPageForm />
        </Flex>
      </Flex>
    </div>
  );
}

export default LoginPage;
