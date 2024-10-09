package com.ecobank.ecounion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContributionDTO {

    private Long id;
    private String projectName;      // Project name
    private String contributorName;
    private String contributorEmail;  // Contributor's email
    private Double amount;            // Contribution amount
    private Date contributionDate;    // Date of contribution
    private String status;            // Status of the contribution
    private String dueMonth;          // Due month for the contribution
    private String receiptImage;      // Receipt image as binary data
}
