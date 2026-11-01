package com.issuepilot;

import com.issuepilot.dto.auth.AuthResponse;
import com.issuepilot.dto.auth.LoginRequest;
import com.issuepilot.dto.auth.RegisterRequest;
import com.issuepilot.entity.Role;
import com.issuepilot.entity.User;
import com.issuepilot.repository.UserRepository;
import com.issuepilot.security.JwtService;
import com.issuepilot.service.AuthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class AuthSecurityTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtService jwtService;

    @AfterEach
    void cleanup() {
        userRepository.findByEmail("security-test@issuepilot.com")
                .ifPresent(userRepository::delete);

        userRepository.findByEmail("security-login@issuepilot.com")
                .ifPresent(userRepository::delete);

        userRepository.findByEmail("security-duplicate@issuepilot.com")
                .ifPresent(userRepository::delete);
    }

    @Test
    void passwordShouldBeStoredAsBCryptHash() {

        User user = new User();
        user.setName("Security Test User");
        user.setEmail("security-test@issuepilot.com");
        user.setPassword(passwordEncoder.encode("TestPassword123!"));
        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);

        assertNotNull(savedUser.getId());

        assertNotEquals(
                "TestPassword123!",
                savedUser.getPassword()
        );

        assertTrue(
                savedUser.getPassword().startsWith("$2")
        );

        assertTrue(
                passwordEncoder.matches(
                        "TestPassword123!",
                        savedUser.getPassword()
                )
        );
    }

    @Test
    void defaultUserRoleShouldBeUser() {

        User user = new User();

        assertEquals(
                Role.USER,
                user.getRole()
        );
    }

    @Test
    void registrationShouldCreateUserWithHashedPassword() {

        RegisterRequest request = new RegisterRequest();

        request.setName("Security Registration User");
        request.setEmail("security-test@issuepilot.com");
        request.setPassword("TestPassword123!");

        User savedUser = authService.register(request);

        assertNotNull(savedUser.getId());

        assertEquals(
                "security-test@issuepilot.com",
                savedUser.getEmail()
        );

        assertEquals(
                Role.USER,
                savedUser.getRole()
        );

        assertNotEquals(
                "TestPassword123!",
                savedUser.getPassword()
        );

        assertTrue(
                passwordEncoder.matches(
                        "TestPassword123!",
                        savedUser.getPassword()
                )
        );
    }

    @Test
    void duplicateEmailShouldBeRejected() {

        User existingUser = new User();

        existingUser.setName("Existing User");
        existingUser.setEmail("security-duplicate@issuepilot.com");
        existingUser.setPassword(
                passwordEncoder.encode("TestPassword123!")
        );
        existingUser.setRole(Role.USER);

        userRepository.save(existingUser);

        RegisterRequest request = new RegisterRequest();

        request.setName("Duplicate User");
        request.setEmail("security-duplicate@issuepilot.com");
        request.setPassword("AnotherPassword123!");

        assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(request)
        );
    }

    @Test
    void loginWithCorrectCredentialsShouldReturnJwt() {

        User user = new User();

        user.setName("Security Login User");
        user.setEmail("security-login@issuepilot.com");
        user.setPassword(
                passwordEncoder.encode("TestPassword123!")
        );
        user.setRole(Role.USER);

        userRepository.save(user);

        LoginRequest request = new LoginRequest();

        request.setEmail("security-login@issuepilot.com");
        request.setPassword("TestPassword123!");

        AuthResponse response = authService.login(request);

        assertNotNull(response);

        assertNotNull(response.getToken());

        assertFalse(
                response.getToken().isBlank()
        );

        assertEquals(
                "Bearer",
                response.getTokenType()
        );

        assertEquals(
                user.getId(),
                response.getUserId()
        );

        assertEquals(
                "security-login@issuepilot.com",
                response.getEmail()
        );

        assertEquals(
                "USER",
                response.getRole()
        );
    }

    @Test
    void loginWithWrongPasswordShouldFail() {

        User user = new User();

        user.setName("Security Login User");
        user.setEmail("security-login@issuepilot.com");
        user.setPassword(
                passwordEncoder.encode("CorrectPassword123!")
        );
        user.setRole(Role.USER);

        userRepository.save(user);

        LoginRequest request = new LoginRequest();

        request.setEmail("security-login@issuepilot.com");
        request.setPassword("WrongPassword123!");

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(request)
        );
    }

    @Test
    void generatedJwtShouldContainUserEmail() {

        User user = new User();

        user.setName("Security JWT User");
        user.setEmail("security-test@issuepilot.com");
        user.setPassword(
                passwordEncoder.encode("TestPassword123!")
        );
        user.setRole(Role.USER);

        userRepository.save(user);

        LoginRequest request = new LoginRequest();

        request.setEmail("security-test@issuepilot.com");
        request.setPassword("TestPassword123!");

        AuthResponse response = authService.login(request);

        String token = response.getToken();

        assertNotNull(token);

        String username =
                jwtService.extractUsername(token);

        assertEquals(
                "security-test@issuepilot.com",
                username
        );
    }

    @Test
    void generatedJwtShouldBeValid() {

        User user = new User();

        user.setName("Security JWT User");
        user.setEmail("security-test@issuepilot.com");
        user.setPassword(
                passwordEncoder.encode("TestPassword123!")
        );
        user.setRole(Role.USER);

        userRepository.save(user);

        LoginRequest request = new LoginRequest();

        request.setEmail("security-test@issuepilot.com");
        request.setPassword("TestPassword123!");

        AuthResponse response = authService.login(request);

        String token = response.getToken();

        var userDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername(user.getEmail())
                        .password(user.getPassword())
                        .authorities("ROLE_USER")
                        .build();

        assertTrue(
                jwtService.isTokenValid(
                        token,
                        userDetails
                )
        );
    }
}