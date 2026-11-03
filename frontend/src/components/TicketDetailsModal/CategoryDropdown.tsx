import { NativeSelect } from "@chakra-ui/react";
import type { ChangeEvent } from "react";
import {
    categoryLabels,
    ticketCategories,
    type TicketCategory,
} from "../../api/tickets";

type CategoryDropdownProps = {
  selectedCategory: TicketCategory | null;
  setSelectedCategory: (category: TicketCategory | null) => void;
};

export default function CategoryDropdown({ selectedCategory, setSelectedCategory }: CategoryDropdownProps) {

    const handleChange = (event: ChangeEvent<HTMLSelectElement>) => {
        const value = event.target.value;
        const category = ticketCategories.find((candidate) => candidate === value) ?? null;
        setSelectedCategory(category);
    };

    return (
        <NativeSelect.Root className="ticket-category-dropdown">
            <NativeSelect.Field
                aria-label="Categoria do chamado"
                className="ticket-category-dropdown__field"
                value={selectedCategory ?? ""}
                onChange={handleChange}
            >
                <option value="">Selecione Categoria</option>
                {ticketCategories.map((category) => (
                    <option key={category} value={category}>
                        {categoryLabels[category]}
                    </option>
                ))}
            </NativeSelect.Field>
            <NativeSelect.Indicator />
        </NativeSelect.Root>
    );
}