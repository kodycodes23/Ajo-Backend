package com.ecobank.ecounion.service;

import com.ecobank.ecounion.dto.FeedbackResponseDTO;
import com.ecobank.ecounion.model.Feedback;
import com.ecobank.ecounion.model.Project;
import com.ecobank.ecounion.model.User;
import com.ecobank.ecounion.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    @Autowired
    FeedbackRepository feedbackRepository;

    // Method to create feedback
    public Feedback createFeedback(User user, Project project, String message) {
        Feedback feedback = new Feedback();
        feedback.setUser(user);
        feedback.setProject(project);
        feedback.setMessage(message);
        feedback.setFeedbackDate(new Date());  // Automatically set the feedback date

        return feedbackRepository.save(feedback);  // Save feedback to the database
    }

    // Method to get feedback by project
    public List<FeedbackResponseDTO> getFeedbackByProject(Project project) {
        List<Feedback> feedbackList = feedbackRepository.findByProject(project);

        return feedbackList.stream()
                .map(feedback -> FeedbackResponseDTO.builder()
                        .id(feedback.getId())
                        .message(feedback.getMessage())
                        .userName(feedback.getUser().getName())
                        .projectName(feedback.getProject().getProjectName())
                        .feedbackDate(feedback.getFeedbackDate())  // Include feedback date
                        .build())
                .collect(Collectors.toList());
    }
}
