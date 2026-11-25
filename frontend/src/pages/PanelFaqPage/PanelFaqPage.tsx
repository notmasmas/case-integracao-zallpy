import { useState } from "react";
import { Box, Button, Flex, Heading, Text, VisuallyHidden } from "@chakra-ui/react";
import { useFaqs } from "../../api/faqs";
import FaqAccordion from "./Components/FaqAccordion";
import FaqToolbar from "./Components/FaqToolbar";
import { categoriesIn, filterFaqs } from "./faqFilters";
import type { FaqCategoryFilter } from "./faqFilters";
import styles from "./PanelFaqPage.module.css";

function PanelFaqPage() {
  const { faqs, error, loading, reload } = useFaqs();
  const [search, setSearch] = useState("");
  const [category, setCategory] = useState<FaqCategoryFilter>("all");

  function clearFilters() {
    setSearch("");
    setCategory("all");
  }

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
        <Flex direction="column" className={styles.feedbackBox}>
          <Text role="alert" className={styles.feedback}>
            {error}
          </Text>
          <Button
            type="button"
            className={styles.actionButton}
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
          Nenhuma pergunta disponível no momento.
        </Text>
      );
    }

    const visible = filterFaqs(faqs, search, category);

    return (
      <>
        <FaqToolbar
          search={search}
          onSearchChange={setSearch}
          category={category}
          categories={categoriesIn(faqs)}
          onCategoryChange={setCategory}
        />
        {visible.length === 0 ? (
          <Flex direction="column" className={styles.feedbackBox}>
            <Text role="status" className={styles.feedback}>
              Nenhuma pergunta encontrada para essa busca.
            </Text>
            <Button
              type="button"
              className={styles.actionButton}
              height="2.4375rem"
              px="4"
              rounded="var(--radius-lg)"
              onClick={clearFilters}
            >
              Limpar filtros
            </Button>
          </Flex>
        ) : (
          <FaqAccordion faqs={visible} />
        )}
      </>
    );
  }

  return (
    <Box className={styles.page}>
      <Flex direction="column" className={styles.column}>
        <VisuallyHidden asChild>
          <Heading as="h1">Perguntas frequentes</Heading>
        </VisuallyHidden>
        {renderContent()}
      </Flex>
    </Box>
  );
}

export default PanelFaqPage;
