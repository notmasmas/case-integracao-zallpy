package com.branch_master.ecovolt360.faq.infrastructure.persistence;

import com.branch_master.ecovolt360.faq.domain.entity.Faq;
import com.branch_master.ecovolt360.faq.domain.entity.FaqCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaFaqRepositoryTest {

    @Mock
    private SpringDataFaqRepository springDataFaqRepository;

    private JpaFaqRepository jpaFaqRepository;

    @BeforeEach
    void setUp() {
        jpaFaqRepository = new JpaFaqRepository(springDataFaqRepository);
    }

    @Test
    void shouldDelegateFindAllToFindAllByOrderByTitleAsc() {
        List<Faq> faqs = List.of(new Faq(UUID.randomUUID(), "Titulo", "Resposta", FaqCategory.OTHER));
        when(springDataFaqRepository.findAllByOrderByTitleAsc()).thenReturn(faqs);

        List<Faq> result = jpaFaqRepository.findAll();

        assertSame(faqs, result);
        verify(springDataFaqRepository).findAllByOrderByTitleAsc();
        verifyNoMoreInteractions(springDataFaqRepository);
    }

    @Test
    void shouldReturnEmptyListWhenSpringDataRepositoryHasNoFaqs() {
        when(springDataFaqRepository.findAllByOrderByTitleAsc()).thenReturn(List.of());

        List<Faq> result = jpaFaqRepository.findAll();

        assertEquals(0, result.size());
    }
}
