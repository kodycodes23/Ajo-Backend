package com.ecobank.ecounion.controller;

import com.ecobank.ecounion.dto.AuthRequestDTO;
import com.ecobank.ecounion.dto.AuthResponseDTO;
import com.ecobank.ecounion.model.Contribution;
import com.ecobank.ecounion.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("api/auth")
@Validated
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "login", produces = "application/json")
    public ResponseEntity<AuthResponseDTO> authenticate(@Valid @RequestBody AuthRequestDTO authRequestDTO) {
        AuthResponseDTO authResponseDTO = authService.authenticate(authRequestDTO);
        return ResponseEntity.ok().body(authResponseDTO);
    }
}
