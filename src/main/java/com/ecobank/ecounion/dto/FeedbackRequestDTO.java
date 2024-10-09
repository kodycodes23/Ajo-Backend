package com.ecobank.ecounion.dto;

import lombok.Data;

@Data
public class FeedbackRequestDTO {
    private Long userId;
    private String message;
}