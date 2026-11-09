package com.branch_master.ecovolt360.faq.infrastructure.persistence.config;

import com.branch_master.ecovolt360.faq.application.service.FaqService;
import com.branch_master.ecovolt360.faq.domain.entity.Faq;
import com.branch_master.ecovolt360.faq.domain.repository.FaqRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FaqConfiguration {

    @Bean
    public FaqService faqService(FaqRepository faqRepository) {

        return new FaqService(faqRepository);
    }
}
