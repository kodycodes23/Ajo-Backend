package com.ecobank.ecounion.dto;

import lombok.Data;

import java.util.List;

@Data
public class AddMemberRequest {

        private List<String> memberEmails;
        private int intervalInDays;



}
