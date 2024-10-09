package com.ecobank.ecounion.service;

import com.ecobank.ecounion.model.Project;
import com.ecobank.ecounion.model.ProjectMembers;
import com.ecobank.ecounion.repository.ProjectMemberRepository;
import com.ecobank.ecounion.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
public class InterestService {

    private static final double DAILY_INTEREST_RATE = 0.01;

    @Autowired
    ProjectMemberRepository projectMemberRepository;

    @Autowired
    ProjectRepository projectRepository;

    @Transactional
    public void calculateDailyInterestForMembers() {
        List<ProjectMembers> projectMembers = projectMemberRepository.findAll();
        Date today = new Date();

        for (ProjectMembers member : projectMembers) {
            // Calculate interest if the member has outstanding dues
            if (member.getOutstandingDues() > 0) {
                long daysLate = calculateDaysLate(member);
                double interest = member.getOutstandingDues() * DAILY_INTEREST_RATE * daysLate;

                // Update interest accrued and total outstanding dues
                member.setInterestAccrued(interest);
                member.setOutstandingDues(member.getOutstandingDues() + interest);

                // Save the updated member
                projectMemberRepository.save(member);
            }
        }
    }

    private long calculateDaysLate(ProjectMembers member) {
        Date today = new Date();
        // Assuming there's a field in ProjectMembers to track the last payment or due date
        Date lastPaymentDate = member.getLastPaymentDate();
        if (lastPaymentDate != null && today.after(lastPaymentDate)) {
            long diffInMillies = today.getTime() - lastPaymentDate.getTime();
            return diffInMillies / (1000 * 60 * 60 * 24); // Convert milliseconds to days
        }
        return 0;
    }

    @Transactional
    public void addMonthlyDues() {
        List<Project> projects = projectRepository.findAll();
        for (Project project : projects) {
            for (ProjectMembers member : project.getProjectMembers()) {
                // Add new month’s dues to outstanding dues
                member.setOutstandingDues(member.getOutstandingDues() + project.getMonthlyDues());

                // Save the updated member
                projectMemberRepository.save(member);
            }
        }
    }

    /**
     * Calculates the total amount due for the current month for each project member.
     * This includes the current month's dues, any outstanding dues from previous months,
     * and the accrued interest on past due amounts.
     */
    @Transactional
    public double getTotalDueForMember(ProjectMembers member) {
        // Add current month's dues to outstanding dues
        addMonthlyDuesForMember(member);

        // Calculate any daily interest on overdue amounts
        calculateInterestForMember(member);

        // Return total dues, which includes outstanding dues and accrued interest
        return member.getOutstandingDues() + member.getInterestAccrued();
    }

    // Helper method to add monthly dues for a single member
    private void addMonthlyDuesForMember(ProjectMembers member) {
        Project project = member.getProject();  // Assuming ProjectMembers has a reference to the Project
        if (project != null) {
            member.setOutstandingDues(member.getOutstandingDues() + project.getMonthlyDues());
        }
    }


    // Helper method to calculate interest for a single member
    private void calculateInterestForMember(ProjectMembers member) {
        if (member.getOutstandingDues() > 0) {
            long daysLate = calculateDaysLate(member);
            double interest = member.getOutstandingDues() * DAILY_INTEREST_RATE * daysLate;
            member.setInterestAccrued(interest);
            member.setOutstandingDues(member.getOutstandingDues() + interest);
            projectMemberRepository.save(member);
        }
    }

    // Calculate the total dues (including interest) for a contributor for the current month
    @Transactional
    public double getTotalDueForCurrentMonth(String contributorEmail) {
        List<ProjectMembers> members = projectMemberRepository.findAllByEmail(contributorEmail);

        if (members.isEmpty()) {
            throw new IllegalArgumentException("No member found with email: " + contributorEmail);
        }

        // Sum the outstanding dues and interest across all the records for this contributor
        return members.stream()
                .mapToDouble(member -> member.getOutstandingDues() + member.getInterestAccrued())
                .sum();
    }

    @Transactional
    public double getTotalDueForProjectAndMember(String projectName, String contributorEmail) {
        Project project = projectRepository.findByProjectName(projectName)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectName));

        ProjectMembers member = projectMemberRepository.findByEmailAndProject(contributorEmail, project)
                .orElseThrow(() -> new IllegalArgumentException("Member not found for the project: " + projectName));

        // Calculate total due including outstanding dues and interest
        return getTotalDueForMember(member);
    }


}
