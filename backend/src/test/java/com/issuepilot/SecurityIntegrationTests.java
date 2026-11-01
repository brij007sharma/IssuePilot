package com.issuepilot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.issuepilot.dto.auth.LoginRequest;
import com.issuepilot.dto.auth.RegisterRequest;
import com.issuepilot.entity.Project;
import com.issuepilot.entity.Role;
import com.issuepilot.entity.User;
import com.issuepilot.repository.ProjectRepository;
import com.issuepilot.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private User owner;
    private User otherUser;
    private User admin;
    private Project project;

    @BeforeEach
    void setUp() {

        projectRepository.deleteAll();

        userRepository.findByEmail("security-owner@issuepilot.com")
                .ifPresent(userRepository::delete);

        userRepository.findByEmail("security-other@issuepilot.com")
                .ifPresent(userRepository::delete);

        userRepository.findByEmail("security-admin@issuepilot.com")
                .ifPresent(userRepository::delete);

        owner = new User();
        owner.setName("Security Owner");
        owner.setEmail("security-owner@issuepilot.com");
        owner.setPassword(
                passwordEncoder.encode("OwnerPassword123!")
        );
        owner.setRole(Role.USER);
        owner = userRepository.save(owner);

        otherUser = new User();
        otherUser.setName("Security Other");
        otherUser.setEmail("security-other@issuepilot.com");
        otherUser.setPassword(
                passwordEncoder.encode("OtherPassword123!")
        );
        otherUser.setRole(Role.USER);
        otherUser = userRepository.save(otherUser);

        admin = new User();
        admin.setName("Security Admin");
        admin.setEmail("security-admin@issuepilot.com");
        admin.setPassword(
                passwordEncoder.encode("AdminPassword123!")
        );
        admin.setRole(Role.ADMIN);
        admin = userRepository.save(admin);

        project = new Project();
        project.setName("Security Test Project");
        project.setKey("SECTEST");
        project.setDescription("Security integration test project");
        project.setOwner(owner);
        project = projectRepository.save(project);
    }

    @AfterEach
    void cleanup() {

        projectRepository.deleteAll();

        userRepository.findByEmail("security-owner@issuepilot.com")
                .ifPresent(userRepository::delete);

        userRepository.findByEmail("security-other@issuepilot.com")
                .ifPresent(userRepository::delete);

        userRepository.findByEmail("security-admin@issuepilot.com")
                .ifPresent(userRepository::delete);
    }

    @Test
    void protectedEndpointWithoutTokenShouldReturn401()
            throws Exception {

        mockMvc.perform(
                        get("/api/projects")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registerEndpointShouldBePublic()
            throws Exception {

        RegisterRequest request = new RegisterRequest();

        request.setName("Public Registration User");
        request.setEmail("security-public@issuepilot.com");
        request.setPassword("PublicPassword123!");

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk());

        userRepository.findByEmail("security-public@issuepilot.com")
                .ifPresent(userRepository::delete);
    }

    @Test
    void loginEndpointShouldBePublic()
            throws Exception {

        LoginRequest request = new LoginRequest();

        request.setEmail("security-owner@issuepilot.com");
        request.setPassword("OwnerPassword123!");

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk());
    }

    @Test
    void validUserTokenShouldAllowAccess()
            throws Exception {

        String token = loginAndGetToken(
                "security-owner@issuepilot.com",
                "OwnerPassword123!"
        );

        mockMvc.perform(
                        get("/api/projects")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk());
    }

    @Test
    void ownerShouldBeAllowedToDeleteOwnProject()
            throws Exception {

        String token = loginAndGetToken(
                "security-owner@issuepilot.com",
                "OwnerPassword123!"
        );

        mockMvc.perform(
                        delete("/api/projects/" + project.getId())
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isNoContent());
    }

    @Test
    void nonOwnerShouldReceive403WhenDeletingProject()
            throws Exception {

        String token = loginAndGetToken(
                "security-other@issuepilot.com",
                "OtherPassword123!"
        );

        mockMvc.perform(
                        delete("/api/projects/" + project.getId())
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void adminShouldBeAllowedToDeleteAnotherUsersProject()
            throws Exception {

        String token = loginAndGetToken(
                "security-admin@issuepilot.com",
                "AdminPassword123!"
        );

        mockMvc.perform(
                        delete("/api/projects/" + project.getId())
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isNoContent());
    }

    private String loginAndGetToken(
            String email,
            String password)
            throws Exception {

        LoginRequest request = new LoginRequest();

        request.setEmail(email);
        request.setPassword(password);

        String response =
                mockMvc.perform(
                                post("/api/auth/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                objectMapper.writeValueAsString(
                                                        request
                                                )
                                        )
                        )
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        JsonNode jsonNode =
                objectMapper.readTree(response);

        return jsonNode
                .get("token")
                .asText();
    }
}