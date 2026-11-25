import { Accordion, Heading, Text } from "@chakra-ui/react";
import { FiChevronDown } from "react-icons/fi";
import { faqCategoryLabel } from "../../../api/faqs";
import type { Faq } from "../../../api/faqs";
import styles from "../PanelFaqPage.module.css";

type FaqAccordionProps = {
  faqs: Faq[];
};

// Several questions can be open at once; the items keep the API order.
export default function FaqAccordion({ faqs }: FaqAccordionProps) {
  return (
    <Accordion.Root multiple className={styles.accordion}>
      {faqs.map((faq) => (
        <Accordion.Item key={faq.id} value={faq.id} className={styles.card}>
          <Heading as="h2" className={styles.cardHeading}>
            <Accordion.ItemTrigger className={styles.trigger}>
              <span className={styles.triggerText}>
                <span className={styles.category}>
                  {faqCategoryLabel(faq.category)}
                </span>
                <span className={styles.question}>{faq.title}</span>
              </span>
              <Accordion.ItemIndicator className={styles.chevron}>
                <FiChevronDown aria-hidden="true" />
              </Accordion.ItemIndicator>
            </Accordion.ItemTrigger>
          </Heading>
          <Accordion.ItemContent>
            <Accordion.ItemBody className={styles.body}>
              <Text className={styles.answer}>{faq.answer}</Text>
            </Accordion.ItemBody>
          </Accordion.ItemContent>
        </Accordion.Item>
      ))}
    </Accordion.Root>
  );
}
