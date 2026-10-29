package com.branch_master.ecovolt360.faq.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FaqBodyDTO (
        @NotBlank
        @NotNull
        String content
){
}
