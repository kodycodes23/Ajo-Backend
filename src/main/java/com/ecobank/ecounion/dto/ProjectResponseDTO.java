package com.ecobank.ecounion.dto;

import lombok.Data;

@Data
public class ProjectResponseDTO {
    private Long projectId;
    private String projectName;
    private String bankName;
    private String accountNumber;

    // Constructor
    public ProjectResponseDTO(Long projectId, String projectName, String bankName, String accountNumber) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
    }
}
