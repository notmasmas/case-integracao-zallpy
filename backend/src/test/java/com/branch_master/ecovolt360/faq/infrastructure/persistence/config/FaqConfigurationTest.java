package com.branch_master.ecovolt360.faq.infrastructure.persistence.config;

import com.branch_master.ecovolt360.faq.application.service.FaqService;
import com.branch_master.ecovolt360.faq.domain.repository.FaqRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class FaqConfigurationTest {

    @Test
    void shouldBuildFaqServiceBeanWrappingGivenRepository() {
        FaqRepository faqRepository = Mockito.mock(FaqRepository.class);
        FaqConfiguration configuration = new FaqConfiguration();

        FaqService faqService = configuration.faqService(faqRepository);

        assertNotNull(faqService);
    }

    @Test
    void shouldDelegateToTheRepositoryPassedIn() {
        FaqRepository faqRepository = Mockito.mock(FaqRepository.class);
        FaqConfiguration configuration = new FaqConfiguration();

        FaqService faqService = configuration.faqService(faqRepository);
        faqService.listFaqs();

        Mockito.verify(faqRepository).findAll();
    }
}
