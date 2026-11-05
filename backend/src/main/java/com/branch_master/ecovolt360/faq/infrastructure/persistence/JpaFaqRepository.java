package com.branch_master.ecovolt360.faq.infrastructure.persistence;

import com.branch_master.ecovolt360.faq.domain.entity.Faq;
import com.branch_master.ecovolt360.faq.domain.repository.FaqRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class JpaFaqRepository implements FaqRepository {

    private final SpringDataFaqRepository repository;

    public JpaFaqRepository(SpringDataFaqRepository repository) {this.repository = repository;}

    @Override
    public List<Faq> findByFaqId(UUID faqId){
        return repository.findByFaqIdOrderByTitleAsc(faqId);
    }
}
