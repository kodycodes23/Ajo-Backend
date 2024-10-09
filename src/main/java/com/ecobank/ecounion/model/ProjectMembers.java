//package com.ecobank.ecounion.model;
//
//import jakarta.persistence.*;
//import lombok.Data;
//
//import java.util.List;
//
//@Data
//@Entity
//@Table(name = "project_members")
//public class ProjectMembers {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "project_id", nullable = false)
//    private Project project;
//
//    @Column(nullable = false)
//    private String email;  // Storing user email
//
//    @Column(nullable = false)
//    private String role;  // Storing user role (admin or member)
//
//    @OneToMany(mappedBy = "contributor")
//    private List<Contribution> contributions;
//
//
//}
package com.ecobank.ecounion.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "project_members")
@Data
public class ProjectMembers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    @JsonManagedReference  // To avoid circular reference issues during serialization
    private Project project;



    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    // Individual monthly dues
    @Column(nullable = false)
    private Double monthlyDues = 0.0;

    // Total outstanding dues
    @Column(nullable = false)
    private Double outstandingDues = 0.0;

    @Column(nullable = false)
    private Double interestRate = 0.1;

    // Interest accrued daily
    @Column(nullable = false)
    private Double interestAccrued = 0.0;

    // Last payment date
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastPaymentDate;


    @Column(nullable = false)
    private String role;

    @OneToMany(mappedBy = "contributor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Contribution> contributions = new ArrayList<>();



}
