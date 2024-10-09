package com.ecobank.ecounion.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String roles;
    private List<String> projects;  // List of project names

    // No-argument constructor
    public UserDTO() {}

    // Constructor with ID and Email
    public UserDTO(Long id, String email) {
        this.id = id;
        this.email = email;
    }

    // Full constructor
    public UserDTO(Long id, String name, String email, String roles, List<String> projects) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.roles = roles;
        this.projects = projects;
    }
}
