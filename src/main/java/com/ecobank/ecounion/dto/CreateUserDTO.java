package com.ecobank.ecounion.dto;

import lombok.Data;

@Data
public class CreateUserDTO {

     private String email;

     private String name;

     private String password;

     private String roles;
}


