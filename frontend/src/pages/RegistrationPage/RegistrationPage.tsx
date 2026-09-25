import { Box, Flex, Heading, Image } from "@chakra-ui/react";
import Logo from "../../assets/logo.svg";
import RegistrationForms from "./Components/RegistrationForms";
import styles from "./RegistrationPage.module.css";

function RegistrationPage() {
  return (
    <Box className={styles.page}>
      <Flex className={styles.layout}>
        <Flex as="section" className={styles.brand}>
          <Image className={styles.logo} src={Logo} alt="Logo EcoVolt 360" />
          <Heading as="h1" className={styles.slogan}>
            Onde há Sol, <br /> há energia
          </Heading>
        </Flex>

        <Flex as="section" className={styles.form}>
          <RegistrationForms />
        </Flex>
      </Flex>
    </Box>
  );
}

export default RegistrationPage;
