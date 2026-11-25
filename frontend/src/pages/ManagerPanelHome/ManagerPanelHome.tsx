import { Flex, Heading } from "@chakra-ui/react";
import styles from "./ManagerPanelHome.module.css";

// Página provisória da área de gestão: será substituída pela listagem da US08.
function ManagerPanelHome() {
  return (
    <Flex as="section" className={styles.page}>
      <Heading as="h1" className={styles.title}>
        Esta página ainda está em construção! :)
      </Heading>
    </Flex>
  );
}

export default ManagerPanelHome;
