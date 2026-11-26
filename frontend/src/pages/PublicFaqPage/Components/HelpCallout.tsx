import { Button, Flex, Heading, Text } from "@chakra-ui/react";
import { Link as RouterLink } from "react-router-dom";
import { paths } from "../../../routes/routes";
import styles from "../PublicFaqPage.module.css";

export default function HelpCallout() {
  return (
    <Flex as="section" className={styles.help} aria-labelledby="faq-help-title">
      <Flex direction="column" className={styles.helpText}>
        <Heading as="h2" id="faq-help-title" className={styles.helpTitle}>
          Ainda precisa de ajuda?
        </Heading>
        <Text className={styles.helpSubtitle}>
          Entre em contato com nosso suporte.
        </Text>
      </Flex>

      <Button
        asChild
        className={styles.loginButton}
        minW="13.75rem"
        height="2.4375rem"
        px="4"
        rounded="var(--radius-lg)"
      >
        <RouterLink to={paths.login}>Login</RouterLink>
      </Button>
    </Flex>
  );
}
