package com.ecobank.ecounion.service;

import com.ecobank.ecounion.dto.CreateUserDTO;
import com.ecobank.ecounion.dto.UserDTO;
import com.ecobank.ecounion.model.Project;
import com.ecobank.ecounion.model.User;
import com.ecobank.ecounion.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;



    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;



    public User createUser(CreateUserDTO createUserDTO) {
        // Ensure password is encoded and user is created properly
        User user = new User();
        user.setEmail(createUserDTO.getEmail());
        user.setName(createUserDTO.getName());
        user.setPassword(passwordEncoder.encode(createUserDTO.getPassword()));
        user.setRoles("Member"); // Set default role

        // Save the user in the database
        User createdUser = userRepository.save(user);

        return createdUser;
    }


    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long userId) {
        return userRepository.findById(userId)
                .map(this::convertToDTO)
                .orElseThrow(() ->  new NoSuchElementException("User not found with id: " + userId));
    }

    // Convert User entity to UserDTO
    public UserDTO convertToDTO(User user) {
        List<String> projectNames = user.getProjects()  // Assuming User has a List<Project>
                .stream()
                .map(Project::getProjectName)  // Extract the project names
                .collect(Collectors.toList());

        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRoles(),
                projectNames
        );
    }

    // Convert UserDTO to User entity
    public User toUser(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        return user;
    }



    public Optional<User> updateUser(Long userId, User updatedUser) {
        return userRepository.findById(userId).map(user -> {
            user.setName(updatedUser.getName());
            user.setEmail(updatedUser.getEmail());
            if (updatedUser.getPassword() != null) {
                user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }
            return userRepository.save(user);
        });
    }

    public boolean deleteUser(Long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            return true;
        }
        return false;
    }

}
