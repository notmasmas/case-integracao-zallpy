package com.branch_master.ecovolt360.faq.application.service;

import com.branch_master.ecovolt360.faq.application.dto.FaqDTO;
import com.branch_master.ecovolt360.faq.domain.entity.Faq;
import com.branch_master.ecovolt360.faq.domain.repository.FaqRepository;


import java.util.List;

public class FaqService {

    private final FaqRepository faqRepository;

    public FaqService(FaqRepository faqRepository){
        this.faqRepository = faqRepository;
    }

    public List<FaqDTO> listFaqs(){
        return faqRepository.findAll().stream().map(FaqDTO::new).toList();
    }
}
