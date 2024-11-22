package com.kennn.jobhunter.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import com.kennn.jobhunter.domain.User;
import com.kennn.jobhunter.domain.request.LoginRequestDTO;
import com.kennn.jobhunter.domain.response.LoginResponseDTO;
import com.kennn.jobhunter.domain.response.user.CreateUserResponseDTO;
import com.kennn.jobhunter.service.UserService;
import com.kennn.jobhunter.util.SecurityUtil;
import com.kennn.jobhunter.util.annotation.APIMessage;
import com.kennn.jobhunter.util.error.IdInvalidException;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private final SecurityUtil securityUtil;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserService userService;

    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,
            UserService userService) {
        this.securityUtil = securityUtil;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userService = userService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginDTO) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(), loginDTO.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // create access token
        SecurityContextHolder.getContext().setAuthentication(authentication);
        LoginResponseDTO responseDTO = new LoginResponseDTO();

        User currentUser = this.userService.getUserByUsername(loginDTO.getUsername());
        if (currentUser != null) {
            LoginResponseDTO.UserLogin userLogin = new LoginResponseDTO.UserLogin(
                    currentUser.getId(),
                    currentUser.getEmail(),
                    currentUser.getName(),
                    currentUser.getRole());
            responseDTO.setUser(userLogin);
        }
        String access_token = this.securityUtil.createAccessToken(authentication.getName(), responseDTO);
        responseDTO.setAccessToken(access_token);

        // create refresh token
        String refreshToken = this.securityUtil.createRefreshToken(loginDTO.getUsername(), responseDTO);
        this.userService.updateUserToken(loginDTO.getUsername(), refreshToken);

        // set refresh token to cookies
        ResponseCookie resCookie = ResponseCookie
                .from("refresh-token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, resCookie.toString()).body(responseDTO);
    }

    @GetMapping("/auth/account")
    @APIMessage("Fetch account")
    public ResponseEntity<LoginResponseDTO.UserGetAccount> getAccount() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";

        User currentUser = this.userService.getUserByUsername(email);
        LoginResponseDTO.UserLogin userLogin = new LoginResponseDTO.UserLogin();
        LoginResponseDTO.UserGetAccount userGetAccount = new LoginResponseDTO.UserGetAccount();
        if (currentUser != null) {
            userLogin.setId(currentUser.getId());
            userLogin.setEmail(currentUser.getEmail());
            userLogin.setName(currentUser.getName());
            userLogin.setRole(currentUser.getRole());
            userGetAccount.setUser(userLogin);
        }
        return ResponseEntity.ok().body(userGetAccount);
    }

    @GetMapping("/auth/refresh")
    @APIMessage("Get user by refresh token")
    public ResponseEntity<LoginResponseDTO> getRefreshToken(@CookieValue(name = "refresh-token") String refreshToken)
            throws IdInvalidException {
        // check valid token
        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refreshToken);
        String email = decodedToken.getSubject();

        // check user by email and refresh-token
        User existUser = this.userService.getUserByEmailAndRefreshToken(email, refreshToken);
        if (existUser == null) {
            throw new IdInvalidException("Refresh token không hợp lệ");
        }

        LoginResponseDTO responseDTO = new LoginResponseDTO();

        User currentUser = this.userService.getUserByUsername(email);
        if (currentUser != null) {
            LoginResponseDTO.UserLogin userLogin = new LoginResponseDTO.UserLogin(currentUser.getId(),
                    currentUser.getEmail(), currentUser.getName(), currentUser.getRole());
            responseDTO.setUser(userLogin);
        }

        String access_token = this.securityUtil.createAccessToken(email, responseDTO);
        responseDTO.setAccessToken(access_token);

        // create refresh token
        String new_refreshToken = this.securityUtil.createRefreshToken(email, responseDTO);
        this.userService.updateUserToken(email, new_refreshToken);

        // set refresh token to cookies
        ResponseCookie resCookie = ResponseCookie
                .from("refresh-token", new_refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, resCookie.toString()).body(responseDTO);
    }

    @PostMapping("/auth/logout")
    @APIMessage("Logout successfully")
    public ResponseEntity<Void> logout() throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        if (email.equals("")) {
            throw new IdInvalidException("Access token không hợp lệ");
        }

        this.userService.updateUserToken(email, null);
        ResponseCookie deleteCookie = ResponseCookie
                .from("refresh-token", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, deleteCookie.toString()).body(null);
    }

    @PostMapping({ "/auth/register" })
    public ResponseEntity<CreateUserResponseDTO> createUser(@Valid @RequestBody User user) throws IdInvalidException {
        boolean isExistEmail = this.userService.isEmailExist(user.getEmail());
        if (isExistEmail) {
            throw new IdInvalidException("Email " + user.getEmail() + " đã tồn tại...");
        }

        CreateUserResponseDTO newUser = this.userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

}
