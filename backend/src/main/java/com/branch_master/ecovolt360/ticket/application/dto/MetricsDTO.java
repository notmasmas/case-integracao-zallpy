package com.branch_master.ecovolt360.ticket.application.dto;


public record MetricsDTO(
    Double scoreAverage,
    int totalAmount,
    int totalOne,
    int totalTwo,
    int totalThree,
    int totalFour,
    int totalFive,
    int pendingAmount,
    int totalTickets
) {
    public MetricsDTO (Double scoreAverage,
                       Long totalAmount,
                       Long totalOne,
                       Long totalTwo,
                       Long totalThree,
                       Long totalFour,
                       Long totalFive,
                       Long pendingAmount,
                       Long totalTickets) {
        this(scoreAverage,
                totalAmount != null ? totalAmount.intValue() : 0,
                totalOne != null ? totalOne.intValue() : 0,
                totalTwo != null ? totalTwo.intValue() : 0,
                totalThree != null ? totalThree.intValue() : 0,
                totalFour != null ? totalFour.intValue() : 0,
                totalFive != null ? totalFive.intValue() : 0,
                pendingAmount != null ? pendingAmount.intValue() : 0,
                totalTickets != null ? totalTickets.intValue() : 0);
    }
}