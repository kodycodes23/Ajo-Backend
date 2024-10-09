package com.ecobank.ecounion.dto;

import com.ecobank.ecounion.model.Contribution;
import com.ecobank.ecounion.model.ProjectMembers;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ProjectDTO {

    @Autowired
    //private ProjectMembers projectMembers;

    private Long id;
    private String projectName;
    private String description;
    private Double goalAmount;
    private Double currentAmount;
    private Double monthlyDues;
    private String bankName;
    private String accountNumber;
    private Date startDate;
    private String adminEmail; // Include the admin if needed

    // A list to hold only emails of the project members
    private List<String> memberEmails;

    // Custom constructor to map project members to emails
    public ProjectDTO(Long id, String projectName, String description, Double goalAmount, Double currentAmount, Double monthlyDues, String bankName,
                      String accountNumber, Date startDate, List<ProjectMembers> members, UserDTO admin) {
        this.id = id;
        this.projectName = projectName;
        this.description = description;
        this.goalAmount = goalAmount;
        this.currentAmount = currentAmount;
        this.monthlyDues = monthlyDues;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.startDate = startDate;
        this.adminEmail = admin != null ? admin.getEmail() : null;
        // Convert members to a list of emails
        this.memberEmails = members.stream()
                .map(ProjectMembers::getEmail) // Assuming ProjectMembers has a getEmail() method
                .collect(Collectors.toList());
    }

    public ProjectDTO() {}

    public ContributionDTO convertToDTO(Contribution contribution) {
        ContributionDTO dto = new ContributionDTO();
        dto.setProjectName(contribution.getProject().getProjectName());
        dto.setContributorEmail(contribution.getContributor().getEmail());
        dto.setAmount(contribution.getAmount());
        dto.setContributionDate(contribution.getContributionDate());
        dto.setStatus(contribution.getStatus());
        dto.setDueMonth(contribution.getDueMonth());
        // Encode receipt image to Base64 string
        if (contribution.getReceiptImage() != null) {
            dto.setReceiptImage(Base64.getEncoder().encodeToString(contribution.getReceiptImage()));
        } else {
            dto.setReceiptImage(null); // Handle the case where the image is null
        }
        return dto;

    }

}
