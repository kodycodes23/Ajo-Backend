package com.ecobank.ecounion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long id;
    private String adminName;
    private String recipientName;
    private String projectName;
    private Double amount;
    private String status;
    private Date paymentDate;
    private String receiptImage;  // Optionally, if you want to return the image in base64 or URL
}

