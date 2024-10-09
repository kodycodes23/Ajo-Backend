package com.ecobank.ecounion.service;

import com.ecobank.ecounion.dto.ContributionDTO;
import com.ecobank.ecounion.dto.ProjectContributionDTO;
import com.ecobank.ecounion.model.Project;
import com.ecobank.ecounion.model.ProjectMembers;
import com.ecobank.ecounion.repository.ContributionRepository;
import com.ecobank.ecounion.repository.ProjectMemberRepository;
import com.ecobank.ecounion.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import com.ecobank.ecounion.dto.ProjectMembersDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectMembersService {

    @Autowired
    ProjectMemberRepository projectMemberRepository;

    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    ContributionRepository contributionRepository;

    @Transactional
    public List<ProjectMembersDTO> getAllProjectMembersByProjectId(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        // Get all project members
        List<ProjectMembers> projectMembers = projectMemberRepository.findByProjectId(projectId);

        // Convert members to DTO with required details
        List<ProjectMembersDTO> projectMemberDTOs = new ArrayList<>();
        for (ProjectMembers member : projectMembers) {
            ProjectMembersDTO dto = new ProjectMembersDTO();
            dto.setId(member.getUser().getId());
            dto.setMemberEmail(member.getEmail());
            dto.setMemberName(member.getUser().getName());
            dto.setOutstandingDues(member.getOutstandingDues());
            dto.setRole(member.getRole());
            dto.setInterestAccrued(member.getInterestAccrued());


            projectMemberDTOs.add(dto);
        }

        return projectMemberDTOs;
    }

    @Transactional
    public ProjectMembersDTO getProjectMemberDetails(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        // Find the specific project member by project and user ID
        ProjectMembers member = projectMemberRepository.findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Project member not found"));

        // Build DTO with member details
        ProjectMembersDTO dto = new ProjectMembersDTO();
        dto.setId(member.getUser().getId());
        dto.setMemberEmail(member.getEmail());
        dto.setMemberName(member.getUser().getName());
        dto.setOutstandingDues(member.getOutstandingDues());
        dto.setRole(member.getRole());
        dto.setInterestAccrued(member.getInterestAccrued());

        return dto;
    }



}
