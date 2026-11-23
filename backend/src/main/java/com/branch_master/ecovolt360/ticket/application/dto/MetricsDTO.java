package com.branch_master.ecovolt360.ticket.application.dto;


public record MetricsDTO(
    Double scoreAverage,
    int totalAmount,
    int totalOne,
    int totalTwo,
    int totalThree,
    int totalFour,
    int totalFive
) {
    public MetricsDTO (Double scoreAverage,
                       Long totalAmount,
                       Long totalOne,
                       Long totalTwo,
                       Long totalThree,
                       Long totalFour,
                       Long totalFive) {
        this(scoreAverage,
                totalAmount.intValue(),
                totalOne.intValue(),
                totalTwo.intValue(),
                totalThree.intValue(),
                totalFour.intValue(),
                totalFive.intValue());
    }
}
