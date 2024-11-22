package com.kennn.jobhunter.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kennn.jobhunter.domain.Permission;
import com.kennn.jobhunter.domain.Role;
import com.kennn.jobhunter.domain.User;
import com.kennn.jobhunter.repository.PermissionRepository;
import com.kennn.jobhunter.repository.RoleRepository;
import com.kennn.jobhunter.repository.UserRepository;
import com.kennn.jobhunter.util.constant.GenderEnum;

@Service
public class DatabaseInitializer implements CommandLineRunner {
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(PermissionRepository permissionRepository, RoleRepository roleRepository,
            UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        long permissionCounts = this.permissionRepository.count();
        long roleCounts = this.roleRepository.count();
        long userCounts = this.userRepository.count();

        if (permissionCounts == 0) {
            ArrayList<Permission> arr = new ArrayList<>();
            arr.add(new Permission("Create a company", "/api/v1/companies", "POST", "COMPANIES"));
            arr.add(new Permission("Get a company by id", "/api/v1/companies/{id}", "GET", "COMPANIES"));
            arr.add(new Permission("Create all companies", "/api/v1/companies", "GET", "COMPANIES"));
            arr.add(new Permission("Update a company", "/api/v1/companies", "PUT", "COMPANIES"));
            arr.add(new Permission("Delete a company", "/api/v1/companies/{id}", "DELETE", "COMPANIES"));

            arr.add(new Permission("Create a job", "/api/v1/jobs", "POST", "JOBS"));
            arr.add(new Permission("Get a job by id", "/api/v1/jobs/{id}", "GET", "JOBS"));
            arr.add(new Permission("Create all jobs", "/api/v1/jobs", "GET", "JOBS"));
            arr.add(new Permission("Update a job", "/api/v1/jobs", "PUT", "JOBS"));
            arr.add(new Permission("Delete a job", "/api/v1/jobs/{id}", "DELETE", "JOBS"));

            arr.add(new Permission("Create a permission", "/api/v1/permissions", "POST", "PERMISSIONS"));
            arr.add(new Permission("Get a permission by id", "/api/v1/permissions/{id}", "GET", "PERMISSIONS"));
            arr.add(new Permission("Create all permissions", "/api/v1/permissions", "GET", "PERMISSIONS"));
            arr.add(new Permission("Update a permission", "/api/v1/permissions", "PUT", "PERMISSIONS"));
            arr.add(new Permission("Delete a permission", "/api/v1/permissions/{id}", "DELETE", "PERMISSIONS"));

            arr.add(new Permission("Create a resume", "/api/v1/resumes", "POST", "RESUMES"));
            arr.add(new Permission("Get a resume by id", "/api/v1/resumes/{id}", "GET", "RESUMES"));
            arr.add(new Permission("Create all resumes", "/api/v1/resumes", "GET", "RESUMES"));
            arr.add(new Permission("Update a resume", "/api/v1/resumes", "PUT", "RESUMES"));
            arr.add(new Permission("Delete a resume", "/api/v1/resumes/{id}", "DELETE", "RESUMES"));

            arr.add(new Permission("Create a role", "/api/v1/roles", "POST", "ROLES"));
            arr.add(new Permission("Get a role by id", "/api/v1/roles/{id}", "GET", "ROLES"));
            arr.add(new Permission("Create all roles", "/api/v1/roles", "GET", "ROLES"));
            arr.add(new Permission("Update a role", "/api/v1/roles", "PUT", "ROLES"));
            arr.add(new Permission("Delete a role", "/api/v1/roles/{id}", "DELETE", "ROLES"));

            arr.add(new Permission("Create a user", "/api/v1/users", "POST", "USERS"));
            arr.add(new Permission("Get a user by id", "/api/v1/users/{id}", "GET", "USERS"));
            arr.add(new Permission("Create all users", "/api/v1/users", "GET", "USERS"));
            arr.add(new Permission("Update a user", "/api/v1/users", "PUT", "USERS"));
            arr.add(new Permission("Delete a user", "/api/v1/users/{id}", "DELETE", "USERS"));

            arr.add(new Permission("Create a subscriber", "/api/v1/subscribers", "POST", "SUBSCRIBERS"));
            arr.add(new Permission("Get a subscriber by id", "/api/v1/subscribers/{id}", "GET", "SUBSCRIBERS"));
            arr.add(new Permission("Create all subscribers", "/api/v1/subscribers", "GET", "SUBSCRIBERS"));
            arr.add(new Permission("Update a subscriber", "/api/v1/subscribers", "PUT", "SUBSCRIBERS"));
            arr.add(new Permission("Delete a subscriber", "/api/v1/subscribers/{id}", "DELETE", "SUBSCRIBERS"));

            arr.add(new Permission("Download a file", "/api/v1/files", "POST", "FILES"));
            arr.add(new Permission("Upload a file", "/api/v1/files", "GET", "FILES"));

            this.permissionRepository.saveAll(arr);
        }

        if (roleCounts == 0) {
            List<Permission> permissions = this.permissionRepository.findAll();

            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            adminRole.setDescription("Full permisisons");
            adminRole.setActive(true);
            adminRole.setPermissions(permissions);

            this.roleRepository.save(adminRole);
        }

        if (userCounts == 0) {
            User adminUser = new User();
            adminUser.setEmail("admin@gmail.com");
            adminUser.setAddress("QN");
            adminUser.setAge(22);
            adminUser.setGender(GenderEnum.MALE);
            adminUser.setName("Admin");
            adminUser.setPassword(passwordEncoder.encode("123123az"));

            Role adminRole = this.roleRepository.findByName("ADMIN");
            if (adminRole != null) {
                adminUser.setRole(adminRole);
            }
            this.userRepository.save(adminUser);
        }

        if (permissionCounts > 0 && roleCounts > 0 && userCounts > 0) {
            System.out.println("Skip init database...");
        }
    }

}
