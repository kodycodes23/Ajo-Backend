package com.ecobank.ecounion.repository;

import com.ecobank.ecounion.model.Project;
import com.ecobank.ecounion.model.ProjectMembers;
import com.ecobank.ecounion.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMembers, Long> {
    Optional<ProjectMembers> findByEmail(String email);
    List<ProjectMembers> findAllByEmail(String email);
    List<ProjectMembers> findByProject(Project project);
    List<ProjectMembers> findAllProjectIdById(Long Id);
    List<ProjectMembers> findByProjectId(Long projectId);
    Optional<ProjectMembers> findByEmailAndProject(String email, Project project);
    Optional<ProjectMembers> findByProjectIdAndUserId(Long projectId, Long userId);
}
