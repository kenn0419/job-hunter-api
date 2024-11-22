package com.kennn.jobhunter.controller;

import com.kennn.jobhunter.domain.User;
import com.kennn.jobhunter.domain.response.ResultPaginationDTO;
import com.kennn.jobhunter.domain.response.user.CreateUserResponseDTO;
import com.kennn.jobhunter.domain.response.user.UpdateUserReponseDTO;
import com.kennn.jobhunter.domain.response.user.UserReponseDTO;
import com.kennn.jobhunter.service.UserService;
import com.kennn.jobhunter.util.annotation.APIMessage;
import com.kennn.jobhunter.util.error.IdInvalidException;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping({ "/users" })
    @APIMessage("fetch all users")
    public ResponseEntity<ResultPaginationDTO> getUsers(@Filter Specification<User> spec, Pageable pageable) {

        return ResponseEntity.ok(this.userService.fetchAllUsers(spec, pageable));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserReponseDTO> getUserById(@PathVariable("id") long id) {
        return ResponseEntity.ok(this.userService.convertTUserReponseDTO(this.userService.fetchUserById(id)));
    }

    @PostMapping({ "/users" })
    public ResponseEntity<CreateUserResponseDTO> createUser(@Valid @RequestBody User user) throws IdInvalidException {
        boolean isExistEmail = this.userService.isEmailExist(user.getEmail());
        if (isExistEmail) {
            throw new IdInvalidException("Email " + user.getEmail() + " đã tồn tại...");
        }

        CreateUserResponseDTO newUser = this.userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @DeleteMapping("/users/{id}")
    @APIMessage("Deleted user successfully")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) throws IdInvalidException {
        User existUser = this.userService.fetchUserById(id);
        if (existUser == null) {
            throw new IdInvalidException("User với id: " + id + " không tồn tại");
        }
        this.userService.removeById(id);
        return ResponseEntity.ok(null);
    }

    @PutMapping({ "/users" })
    public ResponseEntity<UpdateUserReponseDTO> updateUser(@RequestBody User user) throws IdInvalidException {
        User existUser = this.userService.fetchUserById(user.getId());
        if (existUser == null) {
            throw new IdInvalidException("User với id: " + user.getId() + " không tồn tại");
        }
        return ResponseEntity.ok(this.userService.updateUser(user));
    }
}
