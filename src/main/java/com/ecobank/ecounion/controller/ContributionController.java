package com.ecobank.ecounion.controller;

import com.ecobank.ecounion.dto.ContributionDTO;
import com.ecobank.ecounion.model.Contribution;
import com.ecobank.ecounion.service.ContributionService;
import com.ecobank.ecounion.service.FileStorageService;
import com.ecobank.ecounion.service.InterestService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/contributions")
public class ContributionController {
    @Autowired
    private ContributionService contributionService;

    @Autowired
    InterestService interestService;

    @Autowired
    private FileStorageService fileStorageService;


    @PostMapping("/create")
    public ResponseEntity<String> createContribution(
            @Parameter(description = "Email of the contributor", required = true)
            @RequestParam String contributorEmail,

            @Parameter(description = "Amount of the contribution", required = true)
            @RequestParam String amount,

            @Parameter(description = "Due month of the contribution", required = true)
            @RequestParam String dueMonth,

            @Parameter(description = "Name of the project", required = true)
            @RequestParam String projectName,
            @Parameter(description = "Receipt image file", required = true)
            @RequestParam MultipartFile receiptImage // File for upload
            ) {

        try {
            // Create a new Contribution object and set its amount
            Contribution contribution = new Contribution();
            Double contributionAmount = Double.parseDouble(amount);

            contribution.setAmount(contributionAmount);

            // Call the service to create the contribution
            Contribution createdContribution = contributionService.createContribution(
                    contributorEmail, contribution, projectName, receiptImage, dueMonth);

            // Return success response
            return new ResponseEntity<>("Contribution created successfully: " + createdContribution.getId(), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // Handle cases where the project or contributor is not found
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            // Handle IO errors when processing the file
            return new ResponseEntity<>("Error processing file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            // Handle any unexpected errors
            return new ResponseEntity<>("Error creating contribution: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/approve")
    public Contribution approveContribution(@PathVariable Long id) {
        return contributionService.approveContribution(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/reject")
    public Contribution rejectContribution(@PathVariable Long id) {
        return contributionService.rejectContribution(id);
    }
//    @PutMapping("/update/{id}")
//    public ResponseEntity<Contribution> updateContribution(@PathVariable Long id, @RequestBody Contribution updatedContribution) {
//        Optional<Contribution> contribution = contributionService.updateContribution(id, updatedContribution);
//        return contribution.map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.notFound().build());
//    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteContribution(@PathVariable Long id) {
        if (contributionService.deleteContribution(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // New endpoint to get all contributions
    @GetMapping("/all")
    public ResponseEntity<List<Contribution>> getAllContribution() {
        List<Contribution> contributions = contributionService.getAllContribution();
        return ResponseEntity.ok(contributions);
    }

    @PostMapping("/contributions/calculate-interest")
    public ResponseEntity<Void> calculateInterest() {
        interestService.calculateDailyInterestForMembers();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/contributions/add-monthly-dues")
    public ResponseEntity<Void> addMonthlyDues() {
        interestService.addMonthlyDues();
        return ResponseEntity.ok().build();
    }


    // Endpoint to get all contributions
    @GetMapping("/contributions")
    public ResponseEntity<List<ContributionDTO>> getAllContributions() {
        List<ContributionDTO> contributions = contributionService.getAllContributions();
        return ResponseEntity.ok(contributions);
    }

    // Endpoint to get contributions by contributor's email
    @GetMapping("/contributions/by-email")
    public ResponseEntity<List<ContributionDTO>> getContributionsByEmail(@RequestParam String contributorEmail) {
        List<ContributionDTO> contributions = contributionService.getContributionsByContributorEmail(contributorEmail);
        return ResponseEntity.ok(contributions);
    }

    // Endpoint to get contributions by project name
    @GetMapping("/contributions/by-project")
    public ResponseEntity<List<ContributionDTO>> getContributionsByProject(@RequestParam String projectName) {
        List<ContributionDTO> contributions = contributionService.getContributionsByProjectName(projectName);
        return ResponseEntity.ok(contributions);
    }

    // Get total dues for a contributor by email
    @GetMapping("/totalDueByEmail")
    public double getTotalDueForContributor(@RequestParam String contributorEmail) {
        return interestService.getTotalDueForCurrentMonth(contributorEmail);
    }

    // Get total dues for a contributor in a specific project
    @GetMapping("/totalDueByProject")
    public double getTotalDueForProjectMember(@RequestParam String projectName, @RequestParam String contributorEmail) {
        return interestService.getTotalDueForProjectAndMember(projectName, contributorEmail);
    }
}
