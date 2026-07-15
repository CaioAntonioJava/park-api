package com.caiohenrique.demo_park_api.web.controller;

import com.caiohenrique.demo_park_api.entity.Client;
import com.caiohenrique.demo_park_api.entity.ParkingSession;
import com.caiohenrique.demo_park_api.entity.ParkingSpot;
import com.caiohenrique.demo_park_api.entity.User;
import com.caiohenrique.demo_park_api.enums.SpotStatus;
import com.caiohenrique.demo_park_api.jwt.JwtUtils;
import com.caiohenrique.demo_park_api.repository.ClientRepository;
import com.caiohenrique.demo_park_api.repository.ParkingSessionRepository;
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

import java.time.LocalDateTime;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ParkingSessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    @Autowired
    private ParkingSessionRepository parkingSessionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String clientToken;
    private Long clientUserId;
    private Client client;
    private ParkingSpot parkingSpot;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        clientRepository.deleteAll();
        parkingSpotRepository.deleteAll();
        parkingSessionRepository.deleteAll();

        // Admin user
        User admin = new User();
        admin.setUsername("admin@test.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(User.Role.ROLE_ADMIN);
        userRepository.save(admin);
        adminToken = JwtUtils.createToken("admin@test.com", "ADMIN").token();

        // Client user
        User clientUser = new User();
        clientUser.setUsername("client@test.com");
        clientUser.setPassword(passwordEncoder.encode("client123"));
        clientUser.setRole(User.Role.ROLE_CLIENT);
        clientUser = userRepository.save(clientUser);
        clientUserId = clientUser.getId();
        clientToken = JwtUtils.createToken("client@test.com", "CLIENT").token();

        // Client entity
        client = new Client();
        client.setName("Cliente Teste");
        client.setCpf("42212125860");
        client.setUser(clientUser);
        client = clientRepository.save(client);

        // Available parking spot
        parkingSpot = new ParkingSpot();
        parkingSpot.setSpotCode("A001");
        parkingSpot.setSpotStatus(SpotStatus.LIVRE);
        parkingSpot = parkingSpotRepository.save(parkingSpot);
    }

    @Test
    void checkIn_withAdminToken_shouldReturn201() throws Exception {
        Map<String, String> body = Map.of(
                "licensePlate", "BRA1234",
                "brand", "Toyota",
                "model", "Corolla",
                "color", "Prata",
                "clientCpf", "42212125860"
        );

        mockMvc.perform(post("/api/v1/parking-sessions/check-in")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.receiptNumber").isString())
                .andExpect(jsonPath("$.licensePlate").value("BRA1234"))
                .andExpect(jsonPath("$.spotCode").value("A001"));
    }

    @Test
    void checkIn_withClientToken_shouldReturn403() throws Exception {
        Map<String, String> body = Map.of(
                "licensePlate", "BRA1234",
                "brand", "Toyota",
                "model", "Corolla",
                "color", "Prata",
                "clientCpf", "42212125860"
        );

        mockMvc.perform(post("/api/v1/parking-sessions/check-in")
                        .header("Authorization", "Bearer " + clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isForbidden());
    }

    @Test
    void checkIn_withNonExistingClientCpf_shouldReturn404() throws Exception {
        Map<String, String> body = Map.of(
                "licensePlate", "BRA1234",
                "brand", "Toyota",
                "model", "Corolla",
                "color", "Prata",
                "clientCpf", "52998224725"
        );

        mockMvc.perform(post("/api/v1/parking-sessions/check-in")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNotFound());
    }

    @Test
    void checkIn_withInvalidLicensePlate_shouldReturn422() throws Exception {
        Map<String, String> body = Map.of(
                "licensePlate", "INVALID",
                "brand", "Toyota",
                "model", "Corolla",
                "color", "Prata",
                "clientCpf", "42212125860"
        );

        mockMvc.perform(post("/api/v1/parking-sessions/check-in")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void checkIn_withActiveSessionForSamePlate_shouldReturn409() throws Exception {
        // First check-in
        ParkingSession session = new ParkingSession();
        session.setReceiptNumber("REC-001");
        session.setLicensePlate("BRA1234");
        session.setBrand("Toyota");
        session.setModel("Corolla");
        session.setColor("Prata");
        session.setCheckIn(LocalDateTime.now());
        session.setClient(client);
        session.setParkingSpot(parkingSpot);
        parkingSessionRepository.save(session);

        // Try second check-in with same plate
        Map<String, String> body = Map.of(
                "licensePlate", "BRA1234",
                "brand", "Honda",
                "model", "Civic",
                "color", "Preto",
                "clientCpf", "42212125860"
        );

        mockMvc.perform(post("/api/v1/parking-sessions/check-in")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isConflict());
    }

    @Test
    void getOpenSessionByReceipt_withAdminToken_shouldReturn200() throws Exception {
        ParkingSession session = new ParkingSession();
        session.setReceiptNumber("REC-TEST-001");
        session.setLicensePlate("BRA1234");
        session.setBrand("Toyota");
        session.setModel("Corolla");
        session.setColor("Prata");
        session.setCheckIn(LocalDateTime.now());
        session.setClient(client);
        session.setParkingSpot(parkingSpot);
        session = parkingSessionRepository.save(session);

        mockMvc.perform(get("/api/v1/parking-sessions/{receipt}", session.getReceiptNumber())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.receiptNumber").value(session.getReceiptNumber()))
                .andExpect(jsonPath("$.licensePlate").value("BRA1234"));
    }

    @Test
    void getOpenSessionByReceipt_withNonExistingReceipt_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/v1/parking-sessions/{receipt}", "NONEXISTENT")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void checkOut_withAdminToken_shouldReturn200() throws Exception {
        ParkingSession session = new ParkingSession();
        session.setReceiptNumber("REC-CHECKOUT-001");
        session.setLicensePlate("BRA1234");
        session.setBrand("Toyota");
        session.setModel("Corolla");
        session.setColor("Prata");
        session.setCheckIn(LocalDateTime.now().minusHours(2));
        session.setClient(client);
        session.setParkingSpot(parkingSpot);
        session = parkingSessionRepository.save(session);

        mockMvc.perform(put("/api/v1/parking-sessions/check-out/{receipt}", session.getReceiptNumber())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.receiptNumber").value(session.getReceiptNumber()))
                .andExpect(jsonPath("$.parkingFee").isNumber());
    }

    @Test
    void checkOut_withNonExistingReceipt_shouldReturn404() throws Exception {
        mockMvc.perform(put("/api/v1/parking-sessions/check-out/{receipt}", "NONEXISTENT")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void getParkingSessionsByCpf_withAdminToken_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/parking-sessions/cpf/{cpf}", "42212125860")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getParkingSessionsByCpf_withNonExistingCpf_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/v1/parking-sessions/cpf/{cpf}", "00000000000")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllUserParkingSessions_withClientToken_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/parking-sessions")
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getAllUserParkingSessions_withAdminToken_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/v1/parking-sessions")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isForbidden());
    }
}
