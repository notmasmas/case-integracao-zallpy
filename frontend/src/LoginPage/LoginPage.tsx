import { Flex, Image } from "@chakra-ui/react";
import Logo from "../assets/logo.svg";
import "../index.css";
import LoginPageForm from "./LoginPageForm/LoginPageForm";

function LoginPage() {
  return (
    <div>
      <Flex
        justify="space-between"
        align="center"
        direction="row"
        height="100%"
        width="100%"
      >
        <Flex
          width="55%"
          justify="center"
          align="left"
          direction="column"
          gap="2rem"
        >
          <Flex width="100%" height="100%">
            <Image src={Logo} alt="Logo" width="520px" height="350px" />
          </Flex>
          <Flex width="80%" justify="center" align="center">
            <p
              style={{
                fontSize: "3.75rem",
                fontFamily: "var(--font-family-instrument-serif)",
                fontWeight: "400",
                color: "var(--color-text-primary)",
              }}
            >
              Onde há Sol, <br /> há energia
            </p>
          </Flex>
        </Flex>

        <Flex width="45%" justify="flex-start" align="center">
          <LoginPageForm />
        </Flex>
      </Flex>
    </div>
  );
}

export default LoginPage;
