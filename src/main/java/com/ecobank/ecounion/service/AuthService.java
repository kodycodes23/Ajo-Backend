package com.ecobank.ecounion.service;

import com.ecobank.ecounion.dto.AuthRequestDTO;
import com.ecobank.ecounion.dto.AuthResponseDTO;
import com.ecobank.ecounion.model.User;
import com.ecobank.ecounion.model.Project;
import com.ecobank.ecounion.repository.UserRepository;
import com.ecobank.ecounion.security.JWTUtil;
import com.ecobank.ecounion.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class AuthService {

    private  final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    private final long expireInMs = 300 * 10000;

    public AuthResponseDTO authenticate(AuthRequestDTO authRequestDTO) {

        // Authenticate user
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername(), authRequestDTO.getPassword()));

        // Fetch user
        User user = userRepository.findByEmail(authRequestDTO.getUsername()).orElseThrow();

        // Get list of project names
        List<String> projectNames = user.getProjects()
                .stream()
                .map(Project::getProjectName)  // Fetch only the project names
                .collect(Collectors.toList());

        // Generate token
        String token = jwtUtil.generateToken(user);

        System.out.println(token);

        // Return token
        AuthResponseDTO responseDTO = AuthResponseDTO.builder()
                .token(token)
                .id(user.getId())
                .name(user.getName())
                .email(user.getUsername())
                .exp(expireInMs)
                .projects(projectNames)
                .build();

        System.out.println(responseDTO.toString());

        return responseDTO;
    }
}
