package com.ecobank.ecounion.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ReportAnalyticsDTO {

    private String projectName;
    private Double totalContributions;
    private Date projectedGoalAchievementDate;
    private Double cooperativeAccountBalance;
    private String memberName;
    private Double memberContribution;
}
