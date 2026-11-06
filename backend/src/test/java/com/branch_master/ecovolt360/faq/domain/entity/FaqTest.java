package com.branch_master.ecovolt360.faq.domain.entity;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class FaqTest {

    @Test
    void shouldPopulateAllFieldsViaAllArgsConstructor() {
        UUID id = UUID.randomUUID();

        Faq faq = new Faq(id, "Titulo", "Resposta", FaqCategory.EQUIPMENT);

        assertEquals(id, faq.getId());
        assertEquals("Titulo", faq.getTitle());
        assertEquals("Resposta", faq.getAnswer());
        assertEquals(FaqCategory.EQUIPMENT, faq.getCategory());
    }

    @Test
    void shouldHaveAllFieldsNullWhenCreatedWithNoArgsConstructor() {
        Faq faq = new Faq();

        assertNull(faq.getId());
        assertNull(faq.getTitle());
        assertNull(faq.getAnswer());
        assertNull(faq.getCategory());
    }

    @Test
    void shouldAllowFieldMutationViaSetters() {
        Faq faq = new Faq();
        UUID id = UUID.randomUUID();

        faq.setId(id);
        faq.setTitle("Novo titulo");
        faq.setAnswer("Nova resposta");
        faq.setCategory(FaqCategory.PAYMENT);

        assertEquals(id, faq.getId());
        assertEquals("Novo titulo", faq.getTitle());
        assertEquals("Nova resposta", faq.getAnswer());
        assertEquals(FaqCategory.PAYMENT, faq.getCategory());
    }
}
