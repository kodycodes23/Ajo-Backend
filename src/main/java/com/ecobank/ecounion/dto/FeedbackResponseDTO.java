package com.ecobank.ecounion.dto;

import lombok.Builder;
import lombok.Data;
import java.util.Date;

@Data
@Builder
public class FeedbackResponseDTO {
    private Long id;
    private String message;
    private String userName;  // Optional: Include user details
    private String projectName;  // Optional: Include project details
    private Date feedbackDate;   // Add feedback date here
}
