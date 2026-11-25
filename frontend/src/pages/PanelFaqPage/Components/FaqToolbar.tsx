import { Box, Flex, IconButton, Input, Menu, Portal } from "@chakra-ui/react";
import { FiFilter, FiSearch } from "react-icons/fi";
import { faqCategoryLabel } from "../../../api/faqs";
import type { FaqCategory } from "../../../api/faqs";
import { isFaqCategoryFilter } from "../faqFilters";
import type { FaqCategoryFilter } from "../faqFilters";
import styles from "../PanelFaqPage.module.css";

type FaqToolbarProps = {
  search: string;
  onSearchChange: (value: string) => void;
  category: FaqCategoryFilter;
  categories: FaqCategory[];
  onCategoryChange: (value: FaqCategoryFilter) => void;
};

export default function FaqToolbar({
  search,
  onSearchChange,
  category,
  categories,
  onCategoryChange,
}: FaqToolbarProps) {
  const filterActive = category !== "all";
  const filterLabel = filterActive
    ? `Filtrar por categoria: ${faqCategoryLabel(category)}`
    : "Filtrar por categoria";

  return (
    <Flex className={styles.toolbar}>
      <Box className={styles.search}>
        <Input
          type="search"
          className={styles.searchInput}
          aria-label="Buscar perguntas"
          placeholder="Buscar"
          value={search}
          onChange={(event) => onSearchChange(event.target.value)}
        />
        <FiSearch className={styles.searchIcon} aria-hidden="true" />
      </Box>

      <Menu.Root>
        <Menu.Trigger asChild>
          <IconButton
            className={styles.filterButton}
            aria-label={filterLabel}
            data-active={filterActive ? "" : undefined}
            width="3.25rem"
            height="2.75rem"
            rounded="1rem"
          >
            <FiFilter aria-hidden="true" />
          </IconButton>
        </Menu.Trigger>
        <Portal>
          <Menu.Positioner>
            <Menu.Content className={styles.menuContent}>
              <Menu.RadioItemGroup
                value={category}
                onValueChange={({ value }) => {
                  if (isFaqCategoryFilter(value)) {
                    onCategoryChange(value);
                  }
                }}
              >
                <Menu.RadioItem value="all" className={styles.menuItem}>
                  <Menu.ItemText>Todas as categorias</Menu.ItemText>
                  <Menu.ItemIndicator />
                </Menu.RadioItem>
                {categories.map((item) => (
                  <Menu.RadioItem
                    key={item}
                    value={item}
                    className={styles.menuItem}
                  >
                    <Menu.ItemText>{faqCategoryLabel(item)}</Menu.ItemText>
                    <Menu.ItemIndicator />
                  </Menu.RadioItem>
                ))}
              </Menu.RadioItemGroup>
            </Menu.Content>
          </Menu.Positioner>
        </Portal>
      </Menu.Root>
    </Flex>
  );
}
