package com.ecobank.ecounion.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

@Data
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The admin user who makes the payment
    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;  // Assuming User is your user entity

    // The recipient user who receives the payment
    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;  // Assuming User is your user entity

    // Project associated with this payment
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private Double amount;

    // Date the payment is made
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date paymentDate = new Date();

    // Status of the payment (PENDING, APPROVED)
    @Column(nullable = false)
    private String status = "PENDING";

    // Receipt image for the payment
    @Lob
    @Column(name = "receipt_image", columnDefinition = "LONGBLOB")  // Explicitly define LONGBLOB for large images
    private byte[] receiptImage;

    // Utility method to set the receipt image from a MultipartFile
    public void setReceiptImage(MultipartFile file) throws IOException {
        if (file != null) {
            this.receiptImage = file.getBytes();
        }
    }
}
