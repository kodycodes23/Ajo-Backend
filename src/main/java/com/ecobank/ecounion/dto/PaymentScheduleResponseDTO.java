package com.ecobank.ecounion.dto;

import lombok.Data;

import java.util.Date;

@Data
public class PaymentScheduleResponseDTO {
    private Long userId;
    private Long projectId;
    private String memberName;
    private String memberEmail;
    private int paymentOrder;
    private Date paymentDate;

    // Constructors
    public PaymentScheduleResponseDTO(Long userId, Long projectId, String memberName, String memberEmail, int paymentOrder, Date paymentDate) {
        this.userId = userId;
        this.projectId = projectId;
        this.memberName = memberName;
        this.memberEmail = memberEmail;
        this.paymentOrder = paymentOrder;
        this.paymentDate = paymentDate;
    }

}
