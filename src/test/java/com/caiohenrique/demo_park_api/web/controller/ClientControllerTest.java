package com.caiohenrique.demo_park_api.web.controller;

import com.caiohenrique.demo_park_api.entity.Client;
import com.caiohenrique.demo_park_api.entity.User;
import com.caiohenrique.demo_park_api.jwt.JwtUtils;
import com.caiohenrique.demo_park_api.repository.ClientRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    private String adminToken;
    private String clientToken;
    private Long adminUserId;
    private Long clientUserId;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        clientRepository.deleteAll();

        // Create admin user
        User admin = new User();
        admin.setUsername("admin@test.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(User.Role.ROLE_ADMIN);
        admin = userRepository.save(admin);
        adminUserId = admin.getId();
        adminToken = jwtUtils.createToken("admin@test.com", "ADMIN").token();

        // Create client user
        User clientUser = new User();
        clientUser.setUsername("client@test.com");
        clientUser.setPassword(passwordEncoder.encode("client123"));
        clientUser.setRole(User.Role.ROLE_CLIENT);
        clientUser = userRepository.save(clientUser);
        clientUserId = clientUser.getId();
        clientToken = jwtUtils.createToken("client@test.com", "CLIENT").token();
    }

    @Test
    void create_withClientToken_shouldReturn201() throws Exception {
        Map<String, String> body = Map.of(
                "name", "João da Silva",
                "cpf", "42212125860"
        );

        mockMvc.perform(post("/api/v1/clients")
                        .header("Authorization", "Bearer " + clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("João da Silva"))
                .andExpect(jsonPath("$.cpf").value("42212125860"));
    }

    @Test
    void create_withAdminToken_shouldReturn403() throws Exception {
        Map<String, String> body = Map.of(
                "name", "Admin Client",
                "cpf", "42212125860"
        );

        mockMvc.perform(post("/api/v1/clients")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isForbidden());
    }

    @Test
    void create_withInvalidCpf_shouldReturn422() throws Exception {
        Map<String, String> body = Map.of(
                "name", "João da Silva",
                "cpf", "00000000000"
        );

        mockMvc.perform(post("/api/v1/clients")
                        .header("Authorization", "Bearer " + clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void create_withoutToken_shouldReturn401() throws Exception {
        Map<String, String> body = Map.of(
                "name", "João da Silva",
                "cpf", "42212125860"
        );

        mockMvc.perform(post("/api/v1/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getById_withAdminToken_shouldReturn200() throws Exception {
        // First create a client
        Client client = new Client();
        client.setName("Maria Souza");
        client.setCpf("12345678909");
        User clientUser = userRepository.findById(clientUserId).orElseThrow();
        client.setUser(clientUser);
        client = clientRepository.save(client);

        mockMvc.perform(get("/api/v1/clients/{id}", client.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Maria Souza"))
                .andExpect(jsonPath("$.cpf").value("12345678909"));
    }

    @Test
    void getById_withClientToken_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/v1/clients/{id}", 1L)
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAll_withAdminToken_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/clients")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getAll_withClientToken_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/v1/clients")
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void getDetails_withClientToken_shouldReturn200() throws Exception {
        // First create a client linked to the client user
        Client client = new Client();
        client.setName("Client Details");
        client.setCpf("42212125860");
        User clientUser = userRepository.findById(clientUserId).orElseThrow();
        client.setUser(clientUser);
        clientRepository.save(client);

        mockMvc.perform(get("/api/v1/clients/details")
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Client Details"));
    }

    @Test
    void getDetails_withAdminToken_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/v1/clients/details")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isForbidden());
    }
}
