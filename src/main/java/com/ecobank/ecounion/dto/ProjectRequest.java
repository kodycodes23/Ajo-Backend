package com.ecobank.ecounion.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ProjectRequest {
    private String projectName;
    private String description;
    private Double goalAmount;
    private Date startDate;
    private Date endDate;
    private Long adminId;
    private String accountNumber;
    private String bankName;
}
