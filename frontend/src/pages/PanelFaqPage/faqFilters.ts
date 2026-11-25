import { faqCategoryLabel } from "../../api/faqs";
import type { Faq, FaqCategory } from "../../api/faqs";

export type FaqCategoryFilter = FaqCategory | "all";

const faqCategoryFilters: readonly string[] = [
  "all",
  "SYSTEM_MONITORING",
  "EQUIPMENT",
  "INSTALLATION",
  "MAINTENANCE",
  "SCHEDULING",
  "PAYMENT",
  "PROJECT",
  "ACCESS",
  "OTHER",
] satisfies readonly FaqCategoryFilter[];

export function isFaqCategoryFilter(value: string): value is FaqCategoryFilter {
  return faqCategoryFilters.includes(value);
}

// Lowercase, accent-free and trimmed, so "  Manutenção " matches "manutencao".
export function normalizeSearchText(value: string): string {
  return value
    .normalize("NFD")
    .replace(/\p{Diacritic}/gu, "")
    .toLocaleLowerCase("pt-BR")
    .trim();
}

// Keeps the API order. Search looks at the question and the answer; both
// criteria apply together, and an empty search or "all" ignores that criterion.
export function filterFaqs(
  faqs: Faq[],
  search: string,
  category: FaqCategoryFilter,
): Faq[] {
  const term = normalizeSearchText(search);

  return faqs.filter((faq) => {
    if (category !== "all" && faq.category !== category) {
      return false;
    }
    if (!term) {
      return true;
    }
    return (
      normalizeSearchText(faq.title).includes(term) ||
      normalizeSearchText(faq.answer).includes(term)
    );
  });
}

// Distinct categories present in the list, sorted by their pt-BR label.
export function categoriesIn(faqs: Faq[]): FaqCategory[] {
  const unique = [...new Set(faqs.map((faq) => faq.category))];
  return unique.sort((a, b) =>
    faqCategoryLabel(a).localeCompare(faqCategoryLabel(b), "pt-BR"),
  );
}
