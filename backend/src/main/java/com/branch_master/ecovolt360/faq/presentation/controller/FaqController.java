package com.branch_master.ecovolt360.faq.presentation.controller;

import com.branch_master.ecovolt360.faq.application.dto.FaqDTO;
import com.branch_master.ecovolt360.faq.application.service.FaqService;
import com.branch_master.ecovolt360.faq.domain.entity.Faq;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/faqs")
public class FaqController {

    private final FaqService faqService;

    public FaqController(FaqService faqService){
        this.faqService = faqService;
    }

    @GetMapping
    public ResponseEntity<List<FaqDTO>> listFaqs() {
        return ResponseEntity.ok(faqService.listFaqs());
    }

}
