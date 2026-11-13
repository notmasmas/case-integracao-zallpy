package com.branch_master.ecovolt360.ticket.application.dto;


public record MetricsDTO(
    Double scoreAverage,
    Long totalAmount,
    Long totalOne,
    Long totalTwo,
    Long totalThree,
    Long totalFour,
    Long totalFive
) {
}
