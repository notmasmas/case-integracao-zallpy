package com.branch_master.ecovolt360.faq.infrastructure.persistence;

import com.branch_master.ecovolt360.faq.application.exception.FaqUnavailableException;
import com.branch_master.ecovolt360.faq.domain.entity.Faq;
import com.branch_master.ecovolt360.faq.domain.entity.FaqCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.transaction.CannotCreateTransactionException;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void shouldDelegateFindAllToFindAllByOrderByCategoryAscTitleAsc() {
        List<Faq> faqs = List.of(new Faq(UUID.randomUUID(), "Titulo", "Resposta", FaqCategory.OTHER));
        when(springDataFaqRepository.findAllByOrderByCategoryAscTitleAsc()).thenReturn(faqs);

        List<Faq> result = jpaFaqRepository.findAll();

        assertSame(faqs, result);
        verify(springDataFaqRepository).findAllByOrderByCategoryAscTitleAsc();
        verifyNoMoreInteractions(springDataFaqRepository);
    }

    @Test
    void shouldReturnEmptyListWhenSpringDataRepositoryHasNoFaqs() {
        when(springDataFaqRepository.findAllByOrderByCategoryAscTitleAsc()).thenReturn(List.of());

        List<Faq> result = jpaFaqRepository.findAll();

        assertEquals(0, result.size());
    }

    @Test
    void shouldThrowFaqUnavailableWhenDatabaseAccessFails() {
        DataAccessResourceFailureException failure = new DataAccessResourceFailureException("connection refused");
        when(springDataFaqRepository.findAllByOrderByTitleAsc()).thenThrow(failure);

        FaqUnavailableException thrown = assertThrows(FaqUnavailableException.class, () -> jpaFaqRepository.findAll());

        assertSame(failure, thrown.getCause());
        assertEquals(FaqUnavailableException.MESSAGE, thrown.getMessage());
    }

    @Test
    void shouldThrowFaqUnavailableWhenTransactionCannotBeCreated() {
        CannotCreateTransactionException failure = new CannotCreateTransactionException("no connection");
        when(springDataFaqRepository.findAllByOrderByTitleAsc()).thenThrow(failure);

        FaqUnavailableException thrown = assertThrows(FaqUnavailableException.class, () -> jpaFaqRepository.findAll());

        assertSame(failure, thrown.getCause());
    }

    @Test
    void shouldPropagateNonDatabaseExceptions() {
        IllegalStateException failure = new IllegalStateException("bug");
        when(springDataFaqRepository.findAllByOrderByTitleAsc()).thenThrow(failure);

        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> jpaFaqRepository.findAll());

        assertSame(failure, thrown);
    }
}
