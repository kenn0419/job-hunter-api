package com.kennn.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

import com.kennn.jobhunter.domain.Company;
import com.kennn.jobhunter.domain.Role;
import com.kennn.jobhunter.domain.User;
import com.kennn.jobhunter.domain.response.ResultPaginationDTO;
import com.kennn.jobhunter.domain.response.user.CreateUserResponseDTO;
import com.kennn.jobhunter.domain.response.user.UpdateUserReponseDTO;
import com.kennn.jobhunter.domain.response.user.UserReponseDTO;
import com.kennn.jobhunter.repository.UserRepository;
import com.kennn.jobhunter.util.error.IdInvalidException;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CompanyService companyService;
    private final RoleService roleService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, CompanyService companyService,
            RoleService roleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.companyService = companyService;
        this.roleService = roleService;
    }

    public CreateUserResponseDTO save(User currentUser) throws IdInvalidException {
        User user = new User();
        if (currentUser.getCompany() != null) {
            Company existCompany = this.companyService.fetchById(currentUser.getCompany().getId());
            if (existCompany != null) {
                user.setCompany(existCompany);
            }
        }
        if (currentUser.getRole() != null) {
            Role role = this.roleService.fetchRoleById(currentUser.getRole().getId());
            if (role != null) {
                user.setRole(role);
            }
        }
        user.setEmail(currentUser.getEmail());
        user.setName(currentUser.getName());
        user.setPassword(passwordEncoder.encode(currentUser.getPassword()));
        user.setAddress(currentUser.getAddress());
        user.setAge(currentUser.getAge());
        user.setGender(currentUser.getGender());

        return this.convertToCreateUserResponseDTO(this.userRepository.save(user));
    }

    public void removeById(long id) {
        this.userRepository.deleteById(id);
    }

    public ResultPaginationDTO fetchAllUsers(Specification<User> spec, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(spec, pageable);

        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageUser.getTotalPages());
        meta.setTotal(pageUser.getTotalElements());

        result.setMeta(meta);
        List<UserReponseDTO> users = pageUser.getContent().stream().map(user -> this.convertTUserReponseDTO(user))
                .collect(Collectors.toList());
        result.setResult(users);
        return result;
    }

    public User fetchUserById(long id) {
        Optional<User> optionalUser = this.userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return user;
        }
        return null;
    }

    public UpdateUserReponseDTO updateUser(User user) throws IdInvalidException {
        User updatedUser = this.fetchUserById(user.getId());
        if (updatedUser != null) {
            // boolean isExistEmail = this.isEmailExist(user.getEmail());
            // if (isExistEmail) {
            // throw new IdInvalidException("Email " + user.getEmail() + " đã tồn tại...");
            // }
            updatedUser.setEmail(user.getEmail());
            updatedUser.setName(user.getName());
            // updatedUser.setPassword(passwordEncoder.encode(user.getPassword()));
            updatedUser.setAddress(user.getAddress());
            updatedUser.setAge(user.getAge());
            updatedUser.setGender(user.getGender());
            if (user.getCompany() != null) {
                Company existCompany = this.companyService.fetchById(user.getCompany().getId());
                if (existCompany != null) {
                    updatedUser.setCompany(existCompany);
                }
            }
            if (user.getRole() != null) {
                Role role = this.roleService.fetchRoleById(user.getRole().getId());
                if (role != null) {
                    updatedUser.setRole(role);
                }
            }
            updatedUser = this.userRepository.save(updatedUser);
        }
        return this.convertToUpdateUserResponseDTO(updatedUser);
    }

    public User getUserByUsername(String username) {
        User user = this.userRepository.findByEmail(username);
        return user;
    }

    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public CreateUserResponseDTO convertToCreateUserResponseDTO(User user) {
        CreateUserResponseDTO res = new CreateUserResponseDTO();

        res.setId(user.getId());
        res.setName(user.getName());
        res.setEmail(user.getEmail());
        res.setAddress(user.getAddress());
        res.setAge(user.getAge());
        res.setGender(user.getGender());
        res.setCreatedAt(user.getCreatedAt());

        if (user.getCompany() != null) {
            CreateUserResponseDTO.Company company = new CreateUserResponseDTO.Company();
            company.setId(user.getCompany().getId());
            company.setName(user.getCompany().getName());
            res.setCompany(company);
        }

        return res;
    }

    public UpdateUserReponseDTO convertToUpdateUserResponseDTO(User user) {
        UpdateUserReponseDTO res = new UpdateUserReponseDTO();

        res.setId(user.getId());
        res.setName(user.getName());
        res.setEmail(user.getEmail());
        res.setAddress(user.getAddress());
        res.setAge(user.getAge());
        res.setGender(user.getGender());
        res.setUpdatedAt(user.getUpdatedAt());
        if (user.getCompany() != null) {
            UpdateUserReponseDTO.Company company = new UpdateUserReponseDTO.Company();
            company.setId(user.getCompany().getId());
            company.setName(user.getCompany().getName());
            res.setCompany(company);
        }
        return res;
    }

    public UserReponseDTO convertTUserReponseDTO(User user) {
        UserReponseDTO res = new UserReponseDTO();

        res.setId(user.getId());
        res.setName(user.getName());
        res.setEmail(user.getEmail());
        res.setAddress(user.getAddress());
        res.setAge(user.getAge());
        res.setGender(user.getGender());
        res.setCreatedAt(user.getCreatedAt());
        res.setUpdatedAt(user.getUpdatedAt());

        if (user.getCompany() != null) {
            UserReponseDTO.Company company = new UserReponseDTO.Company();
            company.setId(user.getCompany().getId());
            company.setName(user.getCompany().getName());
            res.setCompany(company);
        }

        if (user.getRole() != null) {
            UserReponseDTO.Role role = new UserReponseDTO.Role();
            role.setId(user.getRole().getId());
            role.setName(user.getRole().getName());
            res.setRole(role);
        }
        return res;
    }

    public void updateUserToken(String email, String token) {
        User existUser = this.getUserByUsername(email);

        if (existUser != null) {
            existUser.setRefreshToken(token);
            this.userRepository.save(existUser);
        }
    }

    public User getUserByEmailAndRefreshToken(String email, String refreshToken) {
        return this.userRepository.findByEmailAndRefreshToken(email, refreshToken);
    }

    public UserReponseDTO.Company convertToUserResponseDToCompany(Company company) {
        if (company != null) {
            UserReponseDTO.Company uCompany = new UserReponseDTO.Company();

            uCompany.setId(company.getId());
            uCompany.setName(company.getName());

            return uCompany;
        }
        return null;
    }
}
