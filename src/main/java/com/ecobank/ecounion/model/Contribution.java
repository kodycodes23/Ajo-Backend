package com.ecobank.ecounion.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

@Data
@Entity
@Table(name = "contributions")
public class Contribution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private ProjectMembers contributor;


    @Column(nullable = false)
    private Double amount;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date contributionDate = new Date();

    @Column(nullable = false)
    private String status = "PENDING";

    @Lob
    @Column(name = "receipt_image", columnDefinition = "LONGBLOB")  // Explicitly define LONGBLOB
    private byte[] receiptImage;  // Store image as binary data

    // Getters and setters
    // ...

    // Utility method to set the receipt image from MultipartFile
    public void setReceiptImage(MultipartFile file) throws IOException {
        if (file != null) {
            this.receiptImage = file.getBytes();
        }
    }

    // New field for the due month of the contribution
    @Column(name = "due_month", nullable = false)
    private String dueMonth;
}
