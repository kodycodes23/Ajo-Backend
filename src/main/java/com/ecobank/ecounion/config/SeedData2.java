//package com.ecobank.ecounion.config;
//import com.ecobank.ecounion.model.Project;
//import com.ecobank.ecounion.model.ProjectMembers;
//import com.ecobank.ecounion.model.User;
//import com.ecobank.ecounion.repository.ProjectRepository;
//import com.ecobank.ecounion.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//public class SeedData2 implements CommandLineRunner {
//    private final PasswordEncoder passwordEncoder;
//    private final UserRepository userRepository;
//    private final ProjectRepository projectRepository;
//    @Override
//    public void run(String... args) throws Exception {
//        if (userRepository.count() == 0 && projectRepository.count() == 0) {
//            // Create initial users
//            User adminUser = createUser("Kody Agorua", "kody.agorua@gmail.com", "password");
//            User memberUser = createUser("Peter Mmuo", "petermmuo05@gmail.com", "password");
//            User memberUser2 = createUser("Kamsiyo Mmuo", "mmuokamsiyo@gmail.com", "password");
//            User memberUser3 = createUser("Nifemi Ecobank", "nifiroze2005@gmail.com", "password");
//
//            // Save users
//            userRepository.saveAll(List.of(adminUser, memberUser, memberUser2, memberUser3));
//
//            // Re-fetch users to ensure they are managed
//            User adminUser1 = userRepository.findByEmail(adminUser.getEmail()).orElse(null);
//            User memberUser1= userRepository.findByEmail(memberUser.getEmail()).orElse(null);
//            User memberUser21 = userRepository.findByEmail(memberUser2.getEmail()).orElse(null);
//            User memberUser31 = userRepository.findByEmail(memberUser3.getEmail()).orElse(null);
//
//            if (adminUser1 != null && memberUser1 != null && memberUser21 != null && memberUser31 != null) {
//                // Create projects
//                Project project = createProject("Community Ajo", "A community savings project.", 10000.0, adminUser1, "1234567890");
//                Project project2 = createProject("Ajo Group", "Group project for monthly savings.", 50000.0, memberUser21, "2051583112");
//                Project project3 = createProject("Ajo Group2", "Group project for monthly savings2.", 30000.0, memberUser1, "2256827887");
//
//                // Save projects
//                projectRepository.saveAll(List.of(project, project2, project3));
//
//                System.out.println("Seed data created successfully!");
//            }
//        }
//    }
//
//    private User createUser(String name, String email, String password) {
//        User user = new User();
//        user.setName(name);
//        user.setEmail(email);
//        user.setPassword(passwordEncoder.encode(password));
//        user.setRoles("Member");
//        return user;
//    }
//
//    private Project createProject(String name, String description, Double goalAmount, User admin, String accountNumber) {
//        Project project = new Project();
//        project.setProjectName(name);
//        project.setDescription(description);
//        project.setGoalAmount(goalAmount);
//        project.setAccountNumber(accountNumber); // Use unique account numbers for each project
//        project.setBankName("Ecobank");
//        project.setAdmin(admin);
//
//        // Create ProjectMembers
//        ProjectMembers adminProjectMember = new ProjectMembers();
//        adminProjectMember.setUser(admin);
//        adminProjectMember.setProject(project);
//        adminProjectMember.setRole("Admin");
//        adminProjectMember.setEmail(admin.getEmail());
//
//        ProjectMembers memberProjectMember = new ProjectMembers();
//        memberProjectMember.setUser(admin); // Replace with other users as needed
//        memberProjectMember.setProject(project);
//        memberProjectMember.setRole("Member");
//        memberProjectMember.setEmail(admin.getEmail());
//
//        project.setProjectMembers(List.of(adminProjectMember, memberProjectMember));
//        return project;
//    }
//
//}
//
