package com.ecobank.ecounion.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProjectMembersDTO {
    private Long Id;
    private String memberEmail;
    private String memberName;
    private double outstandingDues;
    private String role;
    private double interestAccrued;



}


