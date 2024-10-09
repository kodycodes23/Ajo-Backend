package com.ecobank.ecounion.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateProjectDTO {

    @NotNull(message = "Project name is required")
    private String projectName;

    @NotNull(message = "Description is required")
    private String description;

    @NotNull(message = "Goal amount is required")
    private Double goalAmount;

    @NotNull(message = "Bank name is required")
    private String bankName;

    @NotNull(message = "Account number is required")
    private String accountNumber;


}
