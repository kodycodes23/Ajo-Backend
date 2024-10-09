package com.ecobank.ecounion.model;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

import java.util.Date;
import java.util.List;

@Data
@Entity

public class PaymentSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private ProjectMembers member;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private Date paymentDate;

    @Column
    private int duePayment;

    @Column(nullable = false)
    private int paymentOrder;

    @Column
    private double interest;

    @Column
    private double totalPayment;
}
