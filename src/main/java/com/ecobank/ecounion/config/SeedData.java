package com.ecobank.ecounion.config;

import com.ecobank.ecounion.model.Project;
import com.ecobank.ecounion.model.ProjectMembers;
import com.ecobank.ecounion.model.User;
import com.ecobank.ecounion.repository.ProjectMemberRepository;
import com.ecobank.ecounion.repository.ProjectRepository;
import com.ecobank.ecounion.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SeedData implements CommandLineRunner {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;

    @Override
    public void run(String... args) throws Exception {


        // Check if the database is empty before seeding
        if (userRepository.count() == 0 && projectRepository.count() == 0) {
            // Create some initial users
            User adminUser = new User();
            adminUser.setName("Kody Agorua");
            adminUser.setEmail("kody.agorua@gmail.com");
            adminUser.setPassword(passwordEncoder.encode("password"));
            adminUser.setRoles("Member");

            User memberUser = new User();
            memberUser.setName("Peter Mmuo");
            memberUser.setEmail("petermmuo05@gmail.com");
            memberUser.setPassword(passwordEncoder.encode("password"));
            memberUser.setRoles("Member");

            User memberUser2 = new User();
            memberUser2.setName("Kamsiyo Mmuo");
            memberUser2.setEmail("mmuokamsiyo@gmail.com");
            memberUser2.setPassword(passwordEncoder.encode("password"));
            memberUser2.setRoles("Member");

            User memberUser3 = new User();
            memberUser3.setName("Nifemi Ecobank");
            memberUser3.setEmail("nifiroze2005@gmail.com");
            memberUser3.setPassword(passwordEncoder.encode("password"));
            memberUser3.setRoles("Member");

            // Save the users
            userRepository.save(adminUser);
            userRepository.save(memberUser);
            userRepository.save(memberUser2);
            userRepository.save(memberUser3);

            // Re-fetch users to ensure they are managed by the persistence context
            adminUser = userRepository.findByEmail(adminUser.getEmail()).orElse(null);
            memberUser = userRepository.findByEmail(memberUser.getEmail()).orElse(null);
            memberUser2 = userRepository.findByEmail(memberUser2.getEmail()).orElse(null);

            if (adminUser != null && memberUser != null) {
                // Create a project
                Project project = new Project();
                project.setProjectName("Community Ajo");
                project.setDescription("A community savings project.");
                project.setGoalAmount(10000.0);
                project.setAccountNumber("1234567890");
                project.setBankName("Ecobank");
                project.setAdmin(adminUser);

                Project project2 = new Project();
                project2.setProjectName("Ajo Group");
                project2.setDescription("Group project for monthly savings.");
                project2.setGoalAmount(50000.0);
                project2.setAccountNumber("2051583112");
                project2.setBankName("Ecobank");
                project2.setAdmin(memberUser2);

                Project project3 = new Project();
                project3.setProjectName("Ajo Group2");
                project3.setDescription("Group project for monthly savings2.");
                project3.setGoalAmount(30000.0);
                project3.setAccountNumber("2203003884");
                project3.setBankName("Ecobank");
                project3.setAdmin(memberUser);

                // Save the project (which will also save the project members)
                projectRepository.save(project);
                projectRepository.save(project2);
                projectRepository.save(project3);

                // Create ProjectMembers for the admin and member
                List<ProjectMembers> projectMembersList = new ArrayList<>();
                List<ProjectMembers> projectMembersList2 = new ArrayList<>();

                ProjectMembers adminProjectMember = new ProjectMembers();
                adminProjectMember.setUser(adminUser);
                adminProjectMember.setProject(project);
                adminProjectMember.setRole("Admin");
                adminProjectMember.setEmail(adminUser.getEmail());
                projectMembersList.add(adminProjectMember);

                ProjectMembers memberProjectMember = new ProjectMembers();
                memberProjectMember.setUser(memberUser);
                memberProjectMember.setProject(project);
                memberProjectMember.setRole("Member");
                memberProjectMember.setEmail(memberUser.getEmail());
                projectMembersList.add(memberProjectMember);

                ProjectMembers memberProjectMember2 = new ProjectMembers();
                memberProjectMember2.setUser(memberUser2);
                memberProjectMember2.setProject(project2);
                memberProjectMember2.setRole("Admin");
                memberProjectMember2.setEmail(memberUser2.getEmail());
                projectMembersList2.add(memberProjectMember2);

                ProjectMembers memberProjectMember3 = new ProjectMembers();
                memberProjectMember3.setUser(adminUser);
                memberProjectMember3.setProject(project2);
                memberProjectMember3.setRole("Member");
                memberProjectMember3.setEmail(adminUser.getEmail());
                projectMembersList2.add(memberProjectMember3);

                // Set the project members to the project
                project.setProjectMembers(projectMembersList);
                project2.setProjectMembers(projectMembersList2);

                projectMemberRepository.save(adminProjectMember);
                projectMemberRepository.save(memberProjectMember);
                projectMemberRepository.save(memberProjectMember2);
                projectMemberRepository.save(memberProjectMember3);





                System.out.println("Seed data created successfully!");

            }
        }


    }
}
