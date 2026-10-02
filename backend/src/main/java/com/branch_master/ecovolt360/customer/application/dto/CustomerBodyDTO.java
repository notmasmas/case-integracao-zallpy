package com.branch_master.ecovolt360.customer.application.dto;

import com.branch_master.ecovolt360.user.application.dto.UserBodyDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record CustomerBodyDTO (

        @NotNull
        @Valid
        UserBodyDTO user) {
}
