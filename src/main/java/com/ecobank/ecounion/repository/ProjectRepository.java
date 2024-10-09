package com.ecobank.ecounion.repository;

import com.ecobank.ecounion.model.Contribution;
import com.ecobank.ecounion.model.Project;
import com.ecobank.ecounion.model.ProjectMembers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long>{
    Optional<Project> findByProjectName(String projectName);
    Optional<Project> findById(Long id);





}
