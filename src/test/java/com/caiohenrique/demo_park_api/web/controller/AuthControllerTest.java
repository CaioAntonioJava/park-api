package com.caiohenrique.demo_park_api.web.controller;

import com.caiohenrique.demo_park_api.entity.User;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        User user = new User();
        user.setUsername("user@test.com");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setRole(User.Role.ROLE_CLIENT);
        userRepository.save(user);
    }

    @Test
    void login_withValidCredentials_shouldReturn200WithToken() throws Exception {
        Map<String, String> body = Map.of(
                "username", "user@test.com",
                "password", "password123"
        );

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void login_withInvalidPassword_shouldReturn401() throws Exception {
        Map<String, String> body = Map.of(
                "username", "user@test.com",
                "password", "wrongpassword"
        );

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Credenciais inválidas."));
    }

    @Test
    void login_withNonExistingUser_shouldReturn401() throws Exception {
        Map<String, String> body = Map.of(
                "username", "nonexistent@test.com",
                "password", "password123"
        );

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_withInvalidEmailFormat_shouldReturn422() throws Exception {
        Map<String, String> body = Map.of(
                "username", "invalid-email",
                "password", "password123"
        );

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void login_withBlankFields_shouldReturn422() throws Exception {
        Map<String, String> body = Map.of(
                "username", "",
                "password", ""
        );

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void login_withAdminCredentials_shouldReturn200WithToken() throws Exception {
        User admin = new User();
        admin.setUsername("admin@test.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(User.Role.ROLE_ADMIN);
        userRepository.save(admin);

        Map<String, String> body = Map.of(
                "username", "admin@test.com",
                "password", "admin123"
        );

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString());
    }
}
