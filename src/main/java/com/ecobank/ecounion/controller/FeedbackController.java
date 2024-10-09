package com.ecobank.ecounion.controller;

import com.ecobank.ecounion.dto.FeedbackRequestDTO;
import com.ecobank.ecounion.dto.FeedbackResponseDTO;
import com.ecobank.ecounion.dto.ProjectDTO;
import com.ecobank.ecounion.dto.UserDTO;
import com.ecobank.ecounion.model.Feedback;
import com.ecobank.ecounion.model.Project;
import com.ecobank.ecounion.model.User;
import com.ecobank.ecounion.repository.ProjectMemberRepository;
import com.ecobank.ecounion.service.FeedbackService;
import com.ecobank.ecounion.service.ProjectService;
import com.ecobank.ecounion.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final ProjectService projectService;
    private final UserService userService;

    @Autowired
    ProjectMemberRepository projectMemberRepository;

    // Endpoint for submitting feedback
    @PostMapping("/{projectId}")
    public ResponseEntity<String> submitFeedback(@PathVariable Long projectId, @RequestBody FeedbackRequestDTO feedbackRequest) {
        // Fetch DTOs from the services
        Project project = projectService.toProject(projectService.getProjectById(projectId));
        User user = userService.toUser(userService.getUserById(feedbackRequest.getUserId()));

        // Check if the user is a member of the project
        if (!isUserMemberOfProject(projectId, user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is not a member of the project");
        }

        // Create feedback
        feedbackService.createFeedback(user, project, feedbackRequest.getMessage());

        return ResponseEntity.ok("Feedback submitted successfully");
    }

    // Method to check if the user is a member of the project
    private boolean isUserMemberOfProject(Long projectId, Long userId) {
        return projectMemberRepository.findByProjectIdAndUserId(projectId, userId).isPresent();
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<List<FeedbackResponseDTO>> getProjectFeedback(@PathVariable Long projectId) {
        Project project = projectService.toProject(projectService.getProjectById(projectId));
        List<FeedbackResponseDTO> feedbackList = feedbackService.getFeedbackByProject(project)
                .stream()
                .map(feedback -> FeedbackResponseDTO.builder()
                        .id(feedback.getId())
                        .message(feedback.getMessage())
                        .userName(feedback.getUserName())
                        .projectName(feedback.getProjectName())
                        .feedbackDate(feedback.getFeedbackDate())
                        .build())
                .toList();

        return ResponseEntity.ok(feedbackList);
    }
}
