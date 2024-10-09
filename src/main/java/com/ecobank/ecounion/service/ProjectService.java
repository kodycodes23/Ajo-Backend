package com.ecobank.ecounion.service;

import com.ecobank.ecounion.dto.*;
import com.ecobank.ecounion.model.PaymentSchedule;
import com.ecobank.ecounion.model.Project;
import com.ecobank.ecounion.model.ProjectMembers;
import com.ecobank.ecounion.model.User;
import com.ecobank.ecounion.repository.PaymentScheduleRepository;
import com.ecobank.ecounion.repository.ProjectMemberRepository;
import com.ecobank.ecounion.repository.ProjectRepository;
import com.ecobank.ecounion.repository.UserRepository;
//import com.ecobank.ecounion.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import com.ecobank.ecounion.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Autowired
    private PaymentScheduleRepository paymentScheduleRepository;

    @Autowired
    private PaymentScheduler paymentScheduler;

    // Ensure UserRepository is injected here
    @Autowired
    private UserRepository userRepository;

    // Create project and set the logged-in user as the admin
    public ProjectResponseDTO createProject(CreateProjectDTO projectDTO) {
        try {
            String userEmail = SecurityUtil.getCurrentUserEmail();
            User currentUser = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            Project project = new Project();
            project.setProjectName(projectDTO.getProjectName());
            project.setDescription(projectDTO.getDescription());
            project.setGoalAmount(projectDTO.getGoalAmount());
            project.setBankName(projectDTO.getBankName());
            project.setAccountNumber(projectDTO.getAccountNumber());
            project.setAdmin(currentUser);

            Project savedProject = projectRepository.save(project);
            System.out.println("Project created: " + savedProject.getId());

            ProjectMembers adminMember = new ProjectMembers();
            adminMember.setUser(currentUser);
            adminMember.setProject(savedProject);
            adminMember.setEmail(currentUser.getEmail());
            adminMember.setRole("Admin");
            ProjectMembers savedAdminMember = projectMemberRepository.save(adminMember);
            System.out.println("Admin member added: " + savedAdminMember);

            // Return the response DTO
            return new ProjectResponseDTO(
                    savedProject.getId(),
                    savedProject.getProjectName(),
                    savedProject.getBankName(),
                    savedProject.getAccountNumber()
            );
        } catch (Exception e) {
            System.err.println("Error while creating project: " + e.getMessage());
            throw new RuntimeException("Error while creating project: " + e.getMessage(), e);
        }
    }


    public Optional<Project>getProjectByName(String projectName) {
        return projectRepository.findByProjectName(projectName);
    }


    public ProjectDTO getProjectById(Long id) {
        // Fetch the project entity
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Project with ID " + id + " not found"));

        // Extract admin and members
        User admin = project.getAdmin();  // Assuming Project has a getAdmin method
        List<ProjectMembers> members = project.getProjectMembers(); // Assuming you have a method to get members

        // Convert admin User to UserDTO
        UserDTO adminDTO = new UserDTO();
        if (admin != null) {
            adminDTO.setEmail(admin.getEmail()); // Set email and any other fields you need
            // Set additional properties as needed
        }
        //Assuming User has a getEmail() method

        // Create and return the ProjectDTO with just the member emails and admin email
        return new ProjectDTO(
                project.getId(),
                project.getProjectName(),
                project.getDescription(),
                project.getGoalAmount(),
                project.getCurrentAmount(),
                project.getMonthlyDues(),
                project.getBankName(),
                project.getAccountNumber(),
                project.getStartDate(),
                members,
                adminDTO
        );
    }

    public ProjectDTO getProjectByProjectName(String projectName) {
        // Find the project by name
        Project project = projectRepository.findByProjectName(projectName)
                .orElseThrow(() -> new NoSuchElementException("Project not found with name: " + projectName));

        // Extract admin and members
        User admin = project.getAdmin();  // Assuming Project has a getAdmin method
        List<ProjectMembers> members = project.getProjectMembers(); // Assuming you have a method to get members

        // Convert admin User to UserDTO
        UserDTO adminDTO = new UserDTO();
        if (admin != null) {
            adminDTO.setEmail(admin.getEmail()); // Set email and any other fields you need
            // Set additional properties as needed
        }
        // Construct and return the ProjectDTO
        return new ProjectDTO(
                project.getId(),
                project.getProjectName(),
                project.getDescription(),
                project.getGoalAmount(),
                project.getCurrentAmount(),
                project.getMonthlyDues(),
                project.getBankName(),
                project.getAccountNumber(),
                project.getStartDate(),
                members,
                adminDTO  // Pass the admin directly
        );
    }

    public List<ProjectDTO> getAllProject() {
        List<Project> projects = projectRepository.findAll();

        return projects.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ProjectDTO convertToDTO(Project project) {
        // Fetch the members for the project
        List<ProjectMembers> members = projectMemberRepository.findByProjectId(project.getId());

        // Fetch the admin user for the project
        User admin = userRepository.findById(project.getAdmin().getId()).orElse(null);
        UserDTO adminDTO = admin != null ? new UserDTO(admin.getId(), admin.getEmail()) : null;

        // Convert to ProjectDTO
        return new ProjectDTO(
                project.getId(),
                project.getProjectName(),
                project.getDescription(),
                project.getGoalAmount(),
                project.getCurrentAmount(),
                project.getMonthlyDues(),
                project.getBankName(),
                project.getAccountNumber(),
                project.getStartDate(),
                members,
                adminDTO
        );
    }

    public Project toProject(ProjectDTO projectDTO) {
        Project project = new Project();
        project.setId(projectDTO.getId());
        project.setProjectName(projectDTO.getProjectName());
        project.setDescription(projectDTO.getDescription());
        project.setGoalAmount(projectDTO.getGoalAmount());
        return project;
    }





//    public Optional<Project> updateProject(Long id, Project updatedProject) {
//        return projectRepository.findById(id).map(project -> {
//            project.setProjectName(updatedProject.getProjectName());
//            project.setDescription(updatedProject.getDescription());
//            project.setGoalAmount(updatedProject.getGoalAmount());
//           // project.setEndDate(updatedProject.getEndDate());
//            return projectRepository.save(project);
//        });
//    }
@Transactional
public Project updateProject(Long projectId,
                             String projectName,
                             String description,
                             Double goalAmount,
                             Double monthlyDues,
                             String accountNumber,
                             String bankName) {
    // Fetch the existing project
    Optional<Project> projectOptional = projectRepository.findById(projectId);

    if (!projectOptional.isPresent()) {
        throw new IllegalArgumentException("Project with ID " + projectId + " not found.");
    }

    Project project = projectOptional.get();

    // Update only the fields that are provided (non-null)
    if (projectName != null) {
        project.setProjectName(projectName);
    }

    if (description != null) {
        project.setDescription(description);
    }

    if (goalAmount != null) {
        project.setGoalAmount(goalAmount);
    }

    // Update monthly dues and reflect it in all project members' outstanding dues
    if (monthlyDues != null) {
        project.setMonthlyDues(monthlyDues);

        // Fetch all project members and update their outstanding dues
        List<ProjectMembers> members = projectMemberRepository.findByProjectId(project.getId());
        for (ProjectMembers member : members) {
            member.setOutstandingDues(monthlyDues);  // Set outstanding dues to match the monthly dues
            projectMemberRepository.save(member);  // Save updated member data
        }
    }

    if (accountNumber != null) {
        project.setAccountNumber(accountNumber);
    }

    if (bankName != null) {
        project.setBankName(bankName);
    }

    // Save the updated project
    return projectRepository.save(project);
}




    public boolean deleteProject(Long id) {
        if (projectRepository.existsById(id)) {
            projectRepository.deleteById(id);
            return true;
        }
        return false;
    }
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

//    // Add member to the project
//    public Project addMemberToProject(String projectName, String memberEmail) {
//        // Fetch the project
//        Project project = projectRepository.findByProjectName(projectName)
//                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
//
//        // Fetch the user to be added as a member
//        User newMember = userRepository.findByEmail(memberEmail)
//                .orElseThrow(() -> new IllegalArgumentException("User not found"));
//
//        // Add the new member to the project's members list
//        if (!project.getProjectMembers().contains(newMember)) { // Prevent adding the same member twice
//            project.getProjectMembers().add(newMember);
//
//            // Also, add the project to the user's projects list (to maintain consistency)
//            newMember.getProjects().add(project);
//
//            // Save the user to update the join table (user_projects)
//            userRepository.save(newMember);
//        }
//
//        // Save and return the updated project
//        return projectRepository.save(project);
//    }


    public Project addMembersAndSchedulePayment(Long projectId, List<String> memberEmails, int intervalInDays) {
        // Fetch the project by name
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        Project project = optionalProject.orElseThrow(() -> new IllegalArgumentException("Project not found"));

        String projectName = project.getProjectName();

        // Fetch the users (members) by their emails from the UserRepository
        List<User> users = memberEmails.stream()
                .map(email -> userRepository.findByEmail(email)
                        .orElseThrow(() -> new IllegalArgumentException("User not found: " + email)))
                .collect(Collectors.toList());

        // Create a list of ProjectMembers and associate them with the project
        List<ProjectMembers> projectMembers = users.stream()
                .map(user -> {
                    ProjectMembers projectMember = new ProjectMembers();
                    projectMember.setEmail(user.getEmail());
                    projectMember.setUser(user); // Associate the user
                    projectMember.setProject(project); // Set project reference
                    projectMember.setRole("Member"); // Default role for added members
                    return projectMember;
                }).collect(Collectors.toList());

        // Save the new members to the ProjectMember repository
        projectMemberRepository.saveAll(projectMembers);

        List<PaymentScheduleResponseDTO> projectSchedule = schedulePayments(projectId, intervalInDays);


        // Save and return the updated project
        return projectRepository.save(project);
    }

    @Transactional
    public List<PaymentScheduleResponseDTO> schedulePayments(Long projectId, int intervalInDays) {
        // Fetch the project by name
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        Project project = optionalProject.orElseThrow(() -> new IllegalArgumentException("Project not found"));

        String projectName = project.getProjectName();

        // Fetch all members associated with the project
        List<ProjectMembers> projectMembers = projectMemberRepository.findByProjectId(projectId);

        if (projectMembers.isEmpty()) {
            throw new IllegalArgumentException("No members found for the project: " + projectName);
        }

        // Generate the payment schedule using the existing members
        List<PaymentSchedule> schedule = paymentScheduler.generatePaymentSchedule(project, projectMembers, intervalInDays);
        // Save the generated payment schedule to the database
        paymentScheduleRepository.saveAll(schedule);

        // Create a list of PaymentScheduleResponse to hold the member details and schedule
        List<PaymentScheduleResponseDTO> paymentScheduleResponses = new ArrayList<>();

        for (PaymentSchedule payment : schedule) {
            ProjectMembers member = payment.getMember(); // Assuming PaymentSchedule has a reference to ProjectMembers
            PaymentScheduleResponseDTO response = new PaymentScheduleResponseDTO(
                    member.getUser().getId(), // Fetch user ID
                    member.getProject().getId(),
                    member.getUser().getName(),
                    member.getEmail(), // Fetch member email
                    payment.getPaymentOrder(), // Get payment order
                    payment.getPaymentDate() // Get payment date
            );
            paymentScheduleResponses.add(response);
        }

        // Sort the schedule by payment order (optional, depending on your paymentScheduler logic)
        paymentScheduleResponses.sort(Comparator.comparingInt(PaymentScheduleResponseDTO::getPaymentOrder));

        // Return the payment schedule in the desired format
        return paymentScheduleResponses;
    }


    // Get all members of a specific project by project name
    public List<ProjectMembers> getMembersByProjectName(String projectName) {
        Project project = projectRepository.findByProjectName(projectName)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        return project.getProjectMembers(); // Returns the list of users who are members of the project
    }

    public List<PaymentScheduleResponseDTO> getPaymentScheduleByProjectId(Long projectId)   {
        // Fetch the payment schedule from the repository using the project ID
        List<PaymentSchedule> paymentSchedules = paymentScheduleRepository.findByProjectId(projectId);

        // Check if the payment schedules exist
        if (paymentSchedules.isEmpty()) {
            throw new IllegalArgumentException("No payment schedule found for project ID: " + projectId);
        }

        // Create a list of PaymentScheduleResponse to hold the member details and schedule
        List<PaymentScheduleResponseDTO> paymentScheduleResponses = new ArrayList<>();

        for (PaymentSchedule payment : paymentSchedules) {
            ProjectMembers member = payment.getMember(); // Assuming PaymentSchedule has a reference to ProjectMembers
            PaymentScheduleResponseDTO response = new PaymentScheduleResponseDTO(
                    member.getUser().getId(), // Fetch user ID
                    member.getProject().getId(),
                    member.getUser().getName(),
                    member.getEmail(), // Fetch member email
                    payment.getPaymentOrder(), // Get payment order
                    payment.getPaymentDate() // Get payment date
            );
            paymentScheduleResponses.add(response);
        }

        // Sort the schedule by payment order (optional)
        paymentScheduleResponses.sort(Comparator.comparingInt(PaymentScheduleResponseDTO::getPaymentOrder));

        // Return the payment schedule in the desired format
        return paymentScheduleResponses;
    }

}
