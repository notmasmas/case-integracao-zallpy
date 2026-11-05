package com.branch_master.ecovolt360.faq.application.dto;

import com.branch_master.ecovolt360.faq.domain.entity.FaqCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FaqBodyDTO (
        @NotNull
        @NotBlank
        FaqCategory category,

        @NotBlank
        @NotNull
        String title,

        @NotBlank
        @NotNull
        String answer
){
}
