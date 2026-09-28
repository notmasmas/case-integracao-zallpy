package com.branch_master.ecovolt360.customer.dto;

import com.branch_master.ecovolt360.user.dto.UserBodyDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record CustomerBodyDTO (

        @NotNull
        @Valid
        UserBodyDTO user) {
}
