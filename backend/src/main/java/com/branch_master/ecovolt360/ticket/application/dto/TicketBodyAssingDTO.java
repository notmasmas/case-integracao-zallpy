package com.branch_master.ecovolt360.ticket.application.dto;

import com.branch_master.ecovolt360.ticket.domain.entity.TicketCategory;

import jakarta.validation.constraints.NotNull;

public record TicketBodyAssingDTO(
    @NotNull TicketCategory category

) {} 
