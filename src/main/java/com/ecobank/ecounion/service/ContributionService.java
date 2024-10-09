package com.ecobank.ecounion.service;

import com.ecobank.ecounion.dto.ContributionDTO;
import com.ecobank.ecounion.model.Contribution;
import com.ecobank.ecounion.model.Project;
import com.ecobank.ecounion.model.ProjectMembers;
import com.ecobank.ecounion.model.User;
import com.ecobank.ecounion.repository.ContributionRepository;
import com.ecobank.ecounion.repository.ProjectMemberRepository;
import com.ecobank.ecounion.repository.ProjectRepository;
import com.ecobank.ecounion.repository.UserRepository;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@Service
public class ContributionService {

    @Autowired
    private ContributionRepository contributionRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    ProjectMemberRepository projectMemberRepository;

    @Autowired
    private EmailService emailService;

    @Transactional
    public Contribution createContribution(@Parameter(description = "Email of the contributor") String contributorEmail,
                                           @Parameter(description = "Contribution details") Contribution contribution,
                                           @Parameter(description = "Name of the project") String projectName,
                                           @Parameter(description = "Receipt image file") @RequestParam MultipartFile receiptImage,
                                           @Parameter(description = "Due month of the contribution") String dueMonth) throws IOException {
        // Fetch the project by name
        Optional<Project> projectOpt = projectRepository.findByProjectName(projectName);
        if (projectOpt.isEmpty()) {
            throw new IllegalArgumentException("Project not found with name: " + projectName);
        }

        Project project = projectOpt.get();

        // Fetch the contributor by their email and project
        Optional<ProjectMembers> contributorOpt = projectMemberRepository.findByEmailAndProject(contributorEmail, project);
        if (contributorOpt.isEmpty()) {
            throw new IllegalArgumentException("Contributor not found with email: " + contributorEmail + " for project: " + projectName);
        }

        ProjectMembers contributor = contributorOpt.get();

        // Set contribution details
        contribution.setProject(project); // Assign the project to the contribution
        contribution.setContributor(contributor); // Assign the contributor to the contribution
        if (receiptImage != null) {
            contribution.setReceiptImage(receiptImage);  // Set the receipt image as binary data
        }
        contribution.setDueMonth(dueMonth);  // Set the due month
        contribution.setStatus("PENDING");  // Set status to PENDING

        // Save the contribution
        return contributionRepository.save(contribution);
    }

    // Convert entity to DTO
    private ContributionDTO convertToDTO(Contribution contribution) {
        ContributionDTO dto = new ContributionDTO();
        dto.setId(contribution.getId());
        dto.setProjectName(contribution.getProject().getProjectName());
        dto.setContributorName(contribution.getContributor().getUser().getName());
        dto.setContributorEmail(contribution.getContributor().getEmail());
        dto.setAmount(contribution.getAmount());
        dto.setContributionDate(contribution.getContributionDate());
        dto.setStatus(contribution.getStatus());
        dto.setDueMonth(contribution.getDueMonth());
        // Convert receipt image to Base64 string
        byte[] receiptImageBytes = contribution.getReceiptImage(); // Assuming this is byte[]
        if (receiptImageBytes != null) {
            String base64ReceiptImage = Base64.getEncoder().encodeToString(receiptImageBytes);
            dto.setReceiptImage(base64ReceiptImage); // Set the Base64 string
        } else {
            dto.setReceiptImage(null); // Handle case where there is no image
        }
        return dto;

    }

    // Get all contributions
    public List<ContributionDTO> getAllContributions() {
        List<Contribution> contributions = contributionRepository.findAll();
        return contributions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get contributions by contributor email
    public List<ContributionDTO> getContributionsByContributorEmail(String contributorEmail) {
        List<Contribution> contributions = contributionRepository.findByContributorEmail(contributorEmail);
        return contributions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get contributions by project name
    public List<ContributionDTO> getContributionsByProjectName(String projectName) {
        List<Contribution> contributions = contributionRepository.findByProject_ProjectName(projectName);
        return contributions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


//    public Optional<Contribution> updateContribution(Long id, Contribution updatedContribution) {
//        return contributionRepository.findById(id).map(contribution -> {
//            contribution.setProjectName(updatedContribution.getProjectName());
//            contribution.setDescription(updatedContribution.getDescription());
//            contribution.setGoalAmount(updatedContribution.getGoalAmount());
//            contribution.setEndDate(updatedContribution.getEndDate());
//            return contributionRepository.save(contribution);
//        });
//    }

        public boolean deleteContribution(Long id) {
            if (contributionRepository.existsById(id)) {
                contributionRepository.deleteById(id);
                return true;
            }
            return false;
        }

        public List<Contribution> getAllContribution() {
            return contributionRepository.findAll();
        }

    @Transactional
    public Contribution approveContribution(Long contributionId) {
        Contribution contribution = contributionRepository.findById(contributionId)
                .orElseThrow(() -> new IllegalArgumentException("Contribution not found"));

        // Check if the contribution has already been approved or rejected
        if ("APPROVED".equals(contribution.getStatus())) {
            throw new IllegalArgumentException("Contribution has already been approved.");
        }

        if ("REJECTED".equals(contribution.getStatus())) {
            throw new IllegalArgumentException("Contribution has been rejected and cannot be approved.");
        }

        // Set status to APPROVED
        contribution.setStatus("APPROVED");

        Project project = contribution.getProject();
        ProjectMembers contributor = contribution.getContributor();

        // Deduct the contribution amount from the contributor's outstanding dues
        double contributionAmount = contribution.getAmount();
        contributor.setOutstandingDues(contributor.getOutstandingDues() - contributionAmount);

        // Ensure outstanding dues never go below 0
        if (contributor.getOutstandingDues() < 0) {
            contributor.setOutstandingDues(0.0);
        }

        // Update the last payment date to now
        contributor.setLastPaymentDate(new Date());

        // Update the project's current amount
        project.setCurrentAmount(project.getCurrentAmount() + contributionAmount);

        // Save the updated project and contributor
        projectRepository.save(project);
        projectMemberRepository.save(contributor);

        return contributionRepository.save(contribution);
    }



    @Transactional
    public Contribution rejectContribution(Long contributionId) {
        Contribution contribution = contributionRepository.findById(contributionId)
                .orElseThrow(() -> new IllegalArgumentException("Contribution not found"));

        // Check if the contribution has already been approved or rejected
        if ("APPROVED".equals(contribution.getStatus())) {
            throw new IllegalArgumentException("Contribution has already been approved and cannot be rejected.");
        }

        if ("REJECTED".equals(contribution.getStatus())) {
            throw new IllegalArgumentException("Contribution has already been rejected.");
        }

        // Set status to REJECTED
        contribution.setStatus("REJECTED");

        // Notify the user (if required, uncomment email logic)
        // ProjectMembers contributor = contribution.getContributor();
        // emailService.sendEmail(contributor.getEmail(), "Contribution Rejected",
        //     "Your contribution to the project " + contribution.getProject().getProjectName() + " has been rejected.");

        return contributionRepository.save(contribution);
    }

    }
