package com.ecobank.ecounion.dto;

import java.util.Date;
import java.util.List;

public class ProjectContributionDTO {

    private Long id;
    private double amount;
    private Date contributionDate;

    public ProjectContributionDTO(Long id, double amount, Date contributionDate) {
        this.id = id;
        this.amount = amount;
        this.contributionDate = contributionDate;
    }


}
