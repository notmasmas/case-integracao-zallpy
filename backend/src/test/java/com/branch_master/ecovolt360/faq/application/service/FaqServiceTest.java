package com.branch_master.ecovolt360.faq.application.service;

import com.branch_master.ecovolt360.faq.application.dto.FaqDTO;
import com.branch_master.ecovolt360.faq.domain.entity.Faq;
import com.branch_master.ecovolt360.faq.domain.entity.FaqCategory;
import com.branch_master.ecovolt360.faq.domain.repository.FaqRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FaqServiceTest {

    @Mock
    private FaqRepository faqRepository;

    private FaqService faqService;

    @BeforeEach
    void setUp() {
        faqService = new FaqService(faqRepository);
    }

    @Test
    void shouldReturnEmptyListWhenRepositoryHasNoFaqs() {
        when(faqRepository.findAll()).thenReturn(List.of());

        List<FaqDTO> result = faqService.listFaqs();

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldMapRepositoryFaqsToFaqDTOsInOrder() {
        Faq first = new Faq(UUID.randomUUID(), "Como funciona o monitoramento?", "Resposta 1", FaqCategory.SYSTEM_MONITORING);
        Faq second = new Faq(UUID.randomUUID(), "Como agendar manutencao?", "Resposta 2", FaqCategory.MAINTENANCE);
        when(faqRepository.findAll()).thenReturn(List.of(first, second));

        List<FaqDTO> result = faqService.listFaqs();

        assertEquals(2, result.size());
        assertEquals(first.getId(), result.get(0).id());
        assertEquals(first.getTitle(), result.get(0).title());
        assertEquals(first.getAnswer(), result.get(0).answer());
        assertEquals(first.getCategory(), result.get(0).category());
        assertEquals(second.getId(), result.get(1).id());
    }

    @Test
    void shouldDelegateToRepositoryFindAll() {
        when(faqRepository.findAll()).thenReturn(List.of());

        faqService.listFaqs();

        verify(faqRepository).findAll();
    }
}
