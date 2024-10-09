package com.ecobank.ecounion.repository;

import com.ecobank.ecounion.model.Feedback;
import com.ecobank.ecounion.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByProject(Project project);

}
