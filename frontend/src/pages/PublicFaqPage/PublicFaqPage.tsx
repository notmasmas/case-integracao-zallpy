import { Box, Button, Flex, Heading, Text } from "@chakra-ui/react";
import { useFaqs } from "../../api/faqs";
import FaqList from "./Components/FaqList";
import HelpCallout from "./Components/HelpCallout";
import styles from "./PublicFaqPage.module.css";

function PublicFaqPage() {
  const { faqs, error, loading, reload } = useFaqs();

  function renderContent() {
    if (loading) {
      return (
        <Text role="status" className={styles.feedback}>
          Carregando perguntas...
        </Text>
      );
    }

    if (error) {
      return (
        <Flex direction="column" className={styles.errorBox}>
          <Text role="alert" className={styles.feedback}>
            {error}
          </Text>
          <Button
            type="button"
            className={styles.retryButton}
            height="2.4375rem"
            px="4"
            rounded="var(--radius-lg)"
            onClick={reload}
          >
            Tentar novamente
          </Button>
        </Flex>
      );
    }

    if (faqs.length === 0) {
      return (
        <Text className={styles.feedback}>
          Nenhuma pergunta cadastrada no momento.
        </Text>
      );
    }

    return <FaqList faqs={faqs} />;
  }

  return (
    <Box className={styles.page}>
      <Flex as="main" direction="column" className={styles.container}>
        <Heading as="h1" className={styles.title}>
          Perguntas frequentes
        </Heading>
        {renderContent()}
        <HelpCallout />
      </Flex>
    </Box>
  );
}

export default PublicFaqPage;
