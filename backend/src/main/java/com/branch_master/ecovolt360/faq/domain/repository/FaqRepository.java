package com.branch_master.ecovolt360.faq.domain.repository;

import com.branch_master.ecovolt360.faq.domain.entity.Faq;
import com.branch_master.ecovolt360.faq.domain.entity.FaqCategory;

import java.util.List;
import java.util.UUID;

public interface FaqRepository {
    List<Faq> findAll();
}
