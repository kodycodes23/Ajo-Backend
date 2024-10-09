package com.ecobank.ecounion.model;

import com.ecobank.ecounion.dto.PaymentScheduleResponseDTO;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String projectName;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Double goalAmount;

    // Set monthly dues for all members of this project
    @Column(nullable = false)
    private Double monthlyDues =0.0;

    @Column(nullable = false)
    private Double currentAmount = 0.0;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    //private LocalDate startDate;
    private Date startDate = new Date();

//    @Temporal(TemporalType.TIMESTAMP)
//    private Date endDate;

//    @ManyToOne
//    @JoinColumn(name = "admin_id", nullable = false)
//    private User admin;
@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
@JoinColumn(name = "admin_id", nullable = false)
@JsonBackReference
private User admin;



    // Added fields for account number and bank name
    @Column(nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private String bankName;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectMembers> projectMembers = new ArrayList<>();;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Contribution> contributions = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "project_id")
    private List<PaymentSchedule> paymentSchedule;

  //   Add a helper method to get the list of users directly
    @Transient
    public List<User> getUsers() {
        return projectMembers.stream()
                .map(ProjectMembers::getUser)
                .collect(Collectors.toList());
    }

}

