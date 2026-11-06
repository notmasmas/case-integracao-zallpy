package com.branch_master.ecovolt360.faq.application.dto;

import com.branch_master.ecovolt360.faq.domain.entity.Faq;
import com.branch_master.ecovolt360.faq.domain.entity.FaqCategory;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FaqDTOTest {

    @Test
    void shouldCopyAllFieldsFromEntity() {
        UUID id = UUID.randomUUID();
        Faq entity = new Faq(id, "Como funciona o monitoramento?", "Resposta detalhada", FaqCategory.SYSTEM_MONITORING);

        FaqDTO dto = new FaqDTO(entity);

        assertEquals(id, dto.id());
        assertEquals("Como funciona o monitoramento?", dto.title());
        assertEquals("Resposta detalhada", dto.answer());
        assertEquals(FaqCategory.SYSTEM_MONITORING, dto.category());
    }
}
