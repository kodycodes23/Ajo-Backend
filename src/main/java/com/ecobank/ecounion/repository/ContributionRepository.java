package com.ecobank.ecounion.repository;

import com.ecobank.ecounion.model.Contribution;
import com.ecobank.ecounion.model.Project;
import com.ecobank.ecounion.model.ProjectMembers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContributionRepository extends JpaRepository<Contribution, Long> {
   // List<Contribution> findByProjectName(String projectName);
   List<Contribution> findByProject(Project Project);

   List<Contribution> findByContributorEmail(String email);

   List<Contribution> findByProject_ProjectName(String projectName);
   List<Contribution> findByContributorAndStatus(ProjectMembers contributor, String status);
}
