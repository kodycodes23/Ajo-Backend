package com.ecobank.ecounion.service;

import com.ecobank.ecounion.dto.ReportAnalyticsDTO;
import com.ecobank.ecounion.model.Contribution;
import com.ecobank.ecounion.model.Project;
import com.ecobank.ecounion.model.ProjectMembers;
import com.ecobank.ecounion.model.User;
import com.ecobank.ecounion.repository.ContributionRepository;
import com.ecobank.ecounion.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    ContributionRepository contributionRepository;

    public List<ReportAnalyticsDTO> getProjectAnalytics(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        // Fetch all contributions for the project
        List<Contribution> contributions = contributionRepository.findByProject(project);

        // Calculate total contributions and balance
        Double totalContributions = contributions.stream()
                .mapToDouble(Contribution::getAmount)
                .sum();
        Double cooperativeAccountBalance = project.getCurrentAmount(); // You can adjust if needed

        List<ReportAnalyticsDTO> reportData = new ArrayList<>();

        // Generate report for each member
        for (ProjectMembers member : project.getProjectMembers()) {
            ReportAnalyticsDTO report = new ReportAnalyticsDTO();
            report.setProjectName(project.getProjectName());
            report.setTotalContributions(totalContributions);
            report.setCooperativeAccountBalance(cooperativeAccountBalance);
            report.setMemberName(member.getEmail());

            // Calculate member's total contribution
            Double memberContribution = contributions.stream()
                    .filter(contribution -> contribution.getContributor().equals(member.getEmail()))
                    .mapToDouble(Contribution::getAmount)
                    .sum();
            report.setMemberContribution(memberContribution);

            // Add report to list
            reportData.add(report);
        }

        return reportData;
    }
}
