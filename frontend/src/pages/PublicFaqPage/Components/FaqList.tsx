import { Box, Text } from "@chakra-ui/react";
import type { Faq } from "../../../api/faqs";
import styles from "../PublicFaqPage.module.css";

type FaqListProps = {
  faqs: Faq[];
};

export default function FaqList({ faqs }: FaqListProps) {
  return (
    <Box as="dl" className={styles.list}>
      {faqs.map((faq) => (
        <Box key={faq.id} className={styles.item}>
          <Text as="dt" className={styles.question}>
            {faq.title}
          </Text>
          <Text as="dd" className={styles.answer}>
            {faq.answer}
          </Text>
        </Box>
      ))}
    </Box>
  );
}
