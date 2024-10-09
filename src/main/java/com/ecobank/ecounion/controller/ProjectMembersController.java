package com.ecobank.ecounion.controller;

import com.ecobank.ecounion.dto.ProjectMembersDTO;
import com.ecobank.ecounion.service.ProjectMembersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/projectmembers")
public class ProjectMembersController {

    @Autowired
    ProjectMembersService projectMembersService;

    // Endpoint to get all project members by project ID
    @GetMapping("/{projectId}/members")
    public ResponseEntity<List<ProjectMembersDTO>> getAllProjectMembers(@PathVariable Long projectId) {
        List<ProjectMembersDTO> projectMembers = projectMembersService.getAllProjectMembersByProjectId(projectId);
        return ResponseEntity.ok(projectMembers);
    }

    // Endpoint to get details of a specific project member
    @GetMapping("/{projectId}/members/{userId}")
    public ResponseEntity<ProjectMembersDTO> getProjectMemberDetails(
            @PathVariable Long projectId,
            @PathVariable Long userId) {
        ProjectMembersDTO memberDetails = projectMembersService.getProjectMemberDetails(projectId, userId);
        return ResponseEntity.ok(memberDetails);
    }
}
