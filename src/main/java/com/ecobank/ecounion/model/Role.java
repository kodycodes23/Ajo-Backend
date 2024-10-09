//Reportservice.jave
//        package com.ecobank.ecounion.service;
//
//import com.ecobank.ecounion.dto.ReportAnalyticsDTO;
//import com.ecobank.ecounion.model.Project;
//import com.ecobank.ecounion.model.ProjectMembers;
//import com.ecobank.ecounion.model.User;
//import com.ecobank.ecounion.repository.ProjectRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class ReportService {
//
//    @Autowired
//    private ProjectRepository projectRepository;
//
//    public List<ReportAnalyticsDTO> getProjectAnalytics(Long projectId) {
//        Project project = projectRepository.findById(projectId).orElseThrow(() -> new IllegalArgumentException("Project not found"));
//        List<ReportAnalyticsDTO> reportData = new ArrayList<>();
//
//        for (ProjectMembers member : project.getProjectMembers()) {
//            ReportAnalyticsDTO report = new ReportAnalyticsDTO();
//            report.setProjectName(project.getProjectName());
//            report.setTotalContributions(project.getCurrentAmount());
//            // report.setProjectedGoalAchievementDate(project.getEndDate());  // Assuming it's the goal achievement date
//            report.setCooperativeAccountBalance(project.getCurrentAmount());  // Assuming it's the same as currentAmount
//            report.setMemberName(member.getEmail());
//
//            // Calculate member's total contribution
//            Double memberContribution = member.getContributions().stream()
//                    .filter(contribution -> contribution.getProject().getId().equals(projectId))
//                    .mapToDouble(contribution -> contribution.getAmount())
//                    .sum();
//            report.setMemberContribution(memberContribution);
//
//            reportData.add(report);
//        }
//
//        return reportData;
//    }
//}

//
//import com.ecobank.ecounion.dto.PaymentScheduleResponseDTO;
//import com.ecobank.ecounion.model.PaymentSchedule;
//
//import java.util.List;
//import java.util.stream.Collectors;// Now that members are added, schedule payments using the `schedulePayments` method
//List<PaymentScheduleResponseDTO> paymentScheduleDTOs = schedulePayments(projectId, intervalInDays);
//
//// Convert DTOs to PaymentSchedule entities
//List<PaymentSchedule> paymentSchedules = paymentScheduleDTOs.stream()
//        .map(dto -> {
//            PaymentSchedule schedule = new PaymentSchedule();
//            schedule.setMember(projectMemberRepository.findByEmail(dto.getMemberEmail())
//                    .orElseThrow(() -> new IllegalArgumentException("Member not found: " + dto.getMemberEmail())));
//            schedule.setProject(project);
//            schedule.setPaymentDate(dto.getPaymentDate());
//            schedule.setPaymentOrder(dto.getPaymentOrder());
//            return schedule;
//        }).collect(Collectors.toList());
//
//// Set the payment schedules to the project
//        project.setPaymentSchedule(paymentSchedules);
