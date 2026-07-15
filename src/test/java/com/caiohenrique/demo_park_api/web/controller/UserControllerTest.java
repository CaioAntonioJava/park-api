package com.caiohenrique.demo_park_api.web.controller;

import com.caiohenrique.demo_park_api.entity.User;
import com.caiohenrique.demo_park_api.jwt.JwtToken;
import com.caiohenrique.demo_park_api.jwt.JwtUtils;
import com.caiohenrique.demo_park_api.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String clientToken;
    private Long adminId;
    private Long clientId;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        // Create admin user
        User admin = new User();
        admin.setUsername("admin@test.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(User.Role.ROLE_ADMIN);
        admin = userRepository.save(admin);
        adminId = admin.getId();
        adminToken = JwtUtils.createToken("admin@test.com", "ADMIN").token();

        // Create client user
        User client = new User();
        client.setUsername("client@test.com");
        client.setPassword(passwordEncoder.encode("client123"));
        client.setRole(User.Role.ROLE_CLIENT);
        client = userRepository.save(client);
        clientId = client.getId();
        clientToken = JwtUtils.createToken("client@test.com", "CLIENT").token();
    }

    @Test
    void create_withValidData_shouldReturn201() throws Exception {
        Map<String, String> body = Map.of(
                "username", "newuser@test.com",
                "password", "password123"
        );

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username").value("newuser@test.com"))
                .andExpect(jsonPath("$.role").value("CLIENT"));
    }

    @Test
    void create_withDuplicateUsername_shouldReturn409() throws Exception {
        Map<String, String> body = Map.of(
                "username", "admin@test.com",
                "password", "password123"
        );

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isConflict());
    }

    @Test
    void create_withInvalidEmail_shouldReturn422() throws Exception {
        Map<String, String> body = Map.of(
                "username", "invalid-email",
                "password", "password123"
        );

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void create_withBlankFields_shouldReturn422() throws Exception {
        Map<String, String> body = Map.of(
                "username", "",
                "password", ""
        );

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void getById_withAdminToken_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/users/{id}", clientId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(clientId))
                .andExpect(jsonPath("$.username").value("client@test.com"));
    }

    @Test
    void getById_withClientTokenAccessingOwnData_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/users/{id}", clientId)
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(clientId));
    }

    @Test
    void getById_withClientTokenAccessingOtherUser_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/v1/users/{id}", adminId)
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void getById_withoutToken_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/v1/users/{id}", 1L))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getById_withNonExistingId_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/v1/users/{id}", 999L)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAll_withAdminToken_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getAll_withClientToken_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void changePassword_withValidData_shouldReturn204() throws Exception {
        Map<String, String> body = Map.of(
                "currentPassword", "client123",
                "newPassword", "newPass123",
                "confirmPassword", "newPass123"
        );

        mockMvc.perform(patch("/api/v1/users/{id}/password", clientId)
                        .header("Authorization", "Bearer " + clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNoContent());
    }

    @Test
    void changePassword_withWrongCurrentPassword_shouldReturn400() throws Exception {
        Map<String, String> body = Map.of(
                "currentPassword", "wrongPassword",
                "newPassword", "newPass123",
                "confirmPassword", "newPass123"
        );

        mockMvc.perform(patch("/api/v1/users/{id}/password", clientId)
                        .header("Authorization", "Bearer " + clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void changePassword_withMismatchedConfirm_shouldReturn400() throws Exception {
        Map<String, String> body = Map.of(
                "currentPassword", "client123",
                "newPassword", "newPass123",
                "confirmPassword", "differentPass"
        );

        mockMvc.perform(patch("/api/v1/users/{id}/password", clientId)
                        .header("Authorization", "Bearer " + clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void resetPassword_withAdminToken_shouldReturn204() throws Exception {
        Map<String, String> body = Map.of(
                "newPassword", "resetPass123",
                "confirmPassword", "resetPass123"
        );

        mockMvc.perform(patch("/api/v1/users/{id}/reset-password", clientId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNoContent());
    }

    @Test
    void resetPassword_withClientToken_shouldReturn403() throws Exception {
        Map<String, String> body = Map.of(
                "newPassword", "resetPass123",
                "confirmPassword", "resetPass123"
        );

        mockMvc.perform(patch("/api/v1/users/{id}/reset-password", clientId)
                        .header("Authorization", "Bearer " + clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isForbidden());
    }
}
