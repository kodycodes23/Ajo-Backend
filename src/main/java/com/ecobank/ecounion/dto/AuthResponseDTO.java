package com.ecobank.ecounion.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class AuthResponseDTO {
    private String token;
    private Long id;
   private String name;
    private String email;
    private Long exp;
    private List<String> projects;
}
