package com.caiohenrique.demo_park_api.web.controller;

import com.caiohenrique.demo_park_api.entity.ParkingSpot;
import com.caiohenrique.demo_park_api.entity.User;
import com.caiohenrique.demo_park_api.enums.SpotStatus;
import com.caiohenrique.demo_park_api.jwt.JwtUtils;
import com.caiohenrique.demo_park_api.repository.ParkingSpotRepository;
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
class ParkingSpotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    private String adminToken;
    private String clientToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        parkingSpotRepository.deleteAll();

        User admin = new User();
        admin.setUsername("admin@test.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(User.Role.ROLE_ADMIN);
        userRepository.save(admin);
        adminToken = jwtUtils.createToken("admin@test.com", "ADMIN").token();

        User client = new User();
        client.setUsername("client@test.com");
        client.setPassword(passwordEncoder.encode("client123"));
        client.setRole(User.Role.ROLE_CLIENT);
        userRepository.save(client);
        clientToken = jwtUtils.createToken("client@test.com", "CLIENT").token();
    }

    @Test
    void create_withAdminToken_shouldReturn201() throws Exception {
        Map<String, Object> body = Map.of(
                "spotCode", "A001",
                "spotStatus", "LIVRE"
        );

        mockMvc.perform(post("/api/v1/parking-spots")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.spotCode").value("A001"))
                .andExpect(jsonPath("$.spotStatus").value("LIVRE"));
    }

    @Test
    void create_withClientToken_shouldReturn403() throws Exception {
        Map<String, Object> body = Map.of(
                "spotCode", "A001",
                "spotStatus", "LIVRE"
        );

        mockMvc.perform(post("/api/v1/parking-spots")
                        .header("Authorization", "Bearer " + clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isForbidden());
    }

    @Test
    void create_withDuplicateCode_shouldReturn409() throws Exception {
        ParkingSpot spot = new ParkingSpot();
        spot.setSpotCode("A001");
        spot.setSpotStatus(SpotStatus.LIVRE);
        parkingSpotRepository.save(spot);

        Map<String, Object> body = Map.of(
                "spotCode", "A001",
                "spotStatus", "LIVRE"
        );

        mockMvc.perform(post("/api/v1/parking-spots")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isConflict());
    }

    @Test
    void create_withInvalidSpotCode_shouldReturn422() throws Exception {
        Map<String, Object> body = Map.of(
                "spotCode", "AB",  // too short
                "spotStatus", "LIVRE"
        );

        mockMvc.perform(post("/api/v1/parking-spots")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void create_withInvalidEnum_shouldReturn400() throws Exception {
        String body = """
                {
                    "spotCode": "A001",
                    "spotStatus": "INVALIDO"
                }
                """;

        mockMvc.perform(post("/api/v1/parking-spots")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getSpotByCode_withAdminToken_shouldReturn200() throws Exception {
        ParkingSpot spot = new ParkingSpot();
        spot.setSpotCode("B002");
        spot.setSpotStatus(SpotStatus.OCUPADA);
        parkingSpotRepository.save(spot);

        mockMvc.perform(get("/api/v1/parking-spots/{spotCode}", "B002")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.spotCode").value("B002"))
                .andExpect(jsonPath("$.spotStatus").value("OCUPADA"));
    }

    @Test
    void getSpotByCode_withNonExistingCode_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/v1/parking-spots/{spotCode}", "Z999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAll_withAdminToken_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/parking-spots")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getAll_withClientToken_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/v1/parking-spots")
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void create_withoutToken_shouldReturn401() throws Exception {
        Map<String, Object> body = Map.of(
                "spotCode", "A001",
                "spotStatus", "LIVRE"
        );

        mockMvc.perform(post("/api/v1/parking-spots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnauthorized());
    }
}
