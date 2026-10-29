package com.branch_master.ecovolt360.faq.application.dto;

import com.branch_master.ecovolt360.faq.domain.entity.Faq;
import java.util.UUID;

public record FaqDTO(
        UUID id,
        String title,
        String answer,
        String category

) {
    public FaqDTO(Faq entity) {
        this(
                entity.getId(),
                entity.getTitle(),
                entity.getAnswer(),
                entity.getCategory()
        );
    }
}
