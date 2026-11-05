package com.branch_master.ecovolt360.faq.infrastructure.persistence;

import com.branch_master.ecovolt360.faq.domain.entity.Faq;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataFaqRepository extends JpaRepository<Faq,UUID> {
    List<Faq> findByFaqIdOrderByTitleAsc(UUID faqId);
}
