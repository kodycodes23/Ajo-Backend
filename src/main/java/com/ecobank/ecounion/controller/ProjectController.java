package com.ecobank.ecounion.controller;

import com.ecobank.ecounion.dto.*;
import com.ecobank.ecounion.model.*;
import com.ecobank.ecounion.repository.PaymentScheduleRepository;
import com.ecobank.ecounion.repository.ProjectMemberRepository;
import com.ecobank.ecounion.repository.ProjectRepository;
import com.ecobank.ecounion.repository.UserRepository;
import com.ecobank.ecounion.service.PaymentScheduler;
import com.ecobank.ecounion.service.ProjectService;
import com.ecobank.ecounion.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/projects")

public class ProjectController {

    @Autowired
    private PaymentScheduleRepository paymentScheduleRepository;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    ProjectService projectService;

    @Autowired
    PaymentScheduler paymentScheduler;

    @Autowired
    ProjectMemberRepository projectMemberRepository;

    @Autowired
    UserRepository userRepository;

    @ApiResponse(responseCode = "200", description = "List of users")
    @ApiResponse(responseCode = "401", description = "Token missing")
    @ApiResponse(responseCode = "403", description = "Token error")
    @Operation(summary = "List user API")
    @SecurityRequirement(name = "soole-demo-api")
    // Endpoint to create a project
    @PostMapping("/create")
    public ResponseEntity<ProjectResponseDTO> createProject(@RequestBody CreateProjectDTO projectDTO) {
        ProjectResponseDTO projectResponse = projectService.createProject(projectDTO);
        return ResponseEntity.ok(projectResponse);
    }


    // Endpoint to add members to a project
    @PutMapping("/{projectname}/addMember")
    public ResponseEntity<String> addMemberToProject(@PathVariable String projectname, @RequestParam String memberemail) {
        // Fetch the currently authenticated user (admin)
        // Use SecurityUtil to get the authenticated user
        // Fetch user
        User currentUser = userRepository.findByEmail(memberemail).orElseThrow();
        System.out.println(currentUser.toString());
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        Optional<Project> projectOpt = projectService.getProjectByName(projectname);

        if (projectOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project not found");
        }

        Project project = projectOpt.get();
        User admin = project.getAdmin();

        // Ensure the signed-in user is the admin of the project
        if (!admin.getEmail().equals(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to add members to this project");
        }

        // Add the new member to the project
        return ResponseEntity.ok("Member added successfully");
    }


    @PutMapping("/addMembers/")
    public ResponseEntity<List<PaymentScheduleResponseDTO>> addMembersAndSchedulePayment(
            @RequestParam Long projectId,
            @RequestBody AddMemberRequest request) {

        try {
            // Call the service method to add members and schedule payments
            projectService.addMembersAndSchedulePayment(
                    projectId, request.getMemberEmails(), request.getIntervalInDays());

            // Return the payment schedule response DTOs
            List<PaymentScheduleResponseDTO> paymentSchedule = projectService.schedulePayments(projectId, request.getIntervalInDays());
            return ResponseEntity.ok(paymentSchedule);

        } catch (IllegalArgumentException e) {
            // Handle cases where the project or users are not found
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            // Handle any unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }



//    @PutMapping("/update/{id}")
//    public ResponseEntity<Project> updateProject(@PathVariable Long id, @RequestBody Project updatedProject) {
//        Optional<Project> contribution = projectService.updateProject(id, updatedProject);
//        return contribution.map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.notFound().build());
//    }
// Endpoint for updating project details
@PatchMapping("/{id}")
public Project updateProject(
        @PathVariable Long id,
        @RequestParam(required = false) String projectName,
        @RequestParam(required = false) String description,
        @RequestParam(required = false) Double goalAmount,
        @RequestParam(required = false) Double monthlyDues,
        @RequestParam(required = false) String accountNumber,
        @RequestParam(required = false) String bankName) {

    // Call the service method to perform the update
    return projectService.updateProject(id, projectName, description, goalAmount, monthlyDues, accountNumber, bankName);
}

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteContribution(@PathVariable Long id) {
        if (projectService.deleteProject(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // New endpoint to get all contributions
    @GetMapping("/all")
    public List<ProjectDTO> getAllProjects() {
        return projectService.getAllProject();
    }


    @GetMapping("/payment-schedule/")
    public ResponseEntity<List<PaymentScheduleResponseDTO>>getPaymentSchedule(@RequestParam Long projectId) {
        try {
            List<PaymentScheduleResponseDTO> paymentScheduleResponses = projectService.getPaymentScheduleByProjectId(projectId);
            return ResponseEntity.ok(paymentScheduleResponses);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    // Get project by name
    @GetMapping("/{projectName}")
    public ResponseEntity<ProjectDTO> getProjectByName(@PathVariable String projectName) {
        ProjectDTO projectDTO = projectService.getProjectByProjectName(projectName);
        return ResponseEntity.ok(projectDTO);
    }

    @PostMapping("/schedulePayments")
    public ResponseEntity<List<PaymentScheduleResponseDTO>> schedulePayments(
            @RequestParam Long projectId,
            @RequestParam int intervalInDays) {
        try {
            // Get the payment schedule response
            List<PaymentScheduleResponseDTO> paymentSchedule = projectService.schedulePayments(projectId, intervalInDays);
            return ResponseEntity.ok(paymentSchedule);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
